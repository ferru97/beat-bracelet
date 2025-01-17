#include <ESP8266WiFi.h>          //ESP8266 Core WiFi Library (you most likely already have this in your sketch)
#include <DNSServer.h>            //Local DNS Server used for redirecting all requests to the configuration portal
#include <ESP8266WebServer.h>     //Local WebServer used to serve the configuration portal
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager WiFi Configuration Magic
#include <PubSubClient.h>

#include <Wire.h>
#include "MAX30105.h"
#include "heartRate.h"

const char* mqttServer = "192.168.178.41";
const int mqttPort = 1883;
const char* mqttID = "5ea15877032e6dcf1174e65c";
const char* mqttUser = "test_user";
const char* mqttPassword = "1234";
WiFiClient espClient;
PubSubClient client(espClient);
const char* update_topic = "5ea15877032e6dcf1174e65c/new_inter";
const char* update_hb_range = "5ea15877032e6dcf1174e65c/new_hb_range";
const char* new_measure_topic = "5ea15877032e6dcf1174e65c/new_measure";
const char* new_alert = "5ea15877032e6dcf1174e65c/alert";
const char* new_alert_hb = "5ea15877032e6dcf1174e65c/alert_hb";
char measure_string[10];
int monitorDelay = 60000; //delay between two monitoring session in ms


//Beat sensor
MAX30105 particleSensor;

const byte RATE_SIZE = 4; //Increase this for more averaging. 4 is good.
byte rates[RATE_SIZE]; //Array of heart rates
byte rateSpot = 0;
long lastBeat = 0; //Time at which the last beat occurred

float beatsPerMinute;
int beatAvg;
const int ten_sec = 20000;

const int buzzer = 14;
const int freq = 1100;

const int wifiLed = 12;
const int touch_button = 13;

bool receivedTime = false;
bool received_min_hb = false;
bool received_max_hb = false;

int interval = 0;
int min_hb = 0;
int max_hb = 0;

void ICACHE_RAM_ATTR buttonPressed ();
void setup() {
  Serial.begin(115200);  

  pinMode(buzzer,OUTPUT);
  pinMode(wifiLed,OUTPUT);

  pinMode(touch_button, INPUT);
  if(digitalRead(touch_button) == HIGH)
    resetWifiSettings();

  pinMode(touch_button, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(touch_button), buttonPressed, RISING);

  connectToWiFi();

  client.setServer(mqttServer, mqttPort);
  client.setCallback(messageReceived);
  connectToServer();
  while(!receivedTime || !received_min_hb || !received_max_hb){
    client.loop();
    delay(200);
  }

  //Beat sensor configurations
  if (!particleSensor.begin(Wire, I2C_SPEED_FAST)) //Use default I2C port, 400kHz speed
  {
    Serial.println("MAX30105 was not found. Please check wiring/power. ");
    while (1);
  }
  particleSensor.setup(); //Configure sensor with default settings
  particleSensor.setPulseAmplitudeRed(0x0A); //Turn Red LED to low to indicate sensor is running
  particleSensor.setPulseAmplitudeGreen(0); //Turn off Green LED

}

void loop() {
  // put your main code here, to run repeatedly:
  monitorBeat();
  Serial.print("Avg BPM=");
  Serial.println(beatAvg);
  Serial.println("");
  itoa(beatAvg,measure_string,10);

  checkConnections();

  if(client.connected())
    client.loop();

  client.publish(new_measure_topic,measure_string);
  if(beatAvg>max_hb || beatAvg<min_hb){
    client.publish(new_alert_hb,measure_string);
    Serial.print("Measurement Alert!");
  }
    
  beatAvg = 0;

  wifi_set_sleep_type(MODEM_SLEEP_T);
  delay(interval);
  
}

void checkConnections(){
  if(!WiFi.isConnected())
    connectToWiFi();
  if(!client.connected())
    connectToServer();
}

void monitorBeat(){
  particleSensor.wakeUp();
  playBuzzer(2);
  int start = millis();
  while(millis()-start < ten_sec){
    long irValue = particleSensor.getIR();

    if (checkForBeat(irValue) == true)
    {
      //We sensed a beat!
      long delta = millis() - lastBeat;
      lastBeat = millis();
  
      beatsPerMinute = 60 / (delta / 1000.0);
  
      if (beatsPerMinute < 255 && beatsPerMinute > 20)
      {
        rates[rateSpot++] = (byte)beatsPerMinute; //Store this reading in the array
        rateSpot %= RATE_SIZE; //Wrap variable
  
        //Take average of readings
        beatAvg = 0;
        for (byte x = 0 ; x < RATE_SIZE ; x++)
          beatAvg += rates[x];
        beatAvg /= RATE_SIZE;
      }
    }
  }
   playBuzzer(1);
   particleSensor.shutDown();
  
}

void connectToWiFi(){
  WiFiManager wifiManager;
  wifiManager.setAPCallback(configModeCallback);
  
  wifiManager.autoConnect("Beat Bracelet");

  digitalWrite(wifiLed,LOW);
  Serial.println("Wifi connected");  
}


void connectToServer(){
  //Connect to the mqtt server
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
 
    if (client.connect(mqttID, mqttUser, mqttPassword )) {
      Serial.println("Mqtt server connected"); 
    } else {
 
      Serial.print("Mqtt connection failed with state ");
      Serial.print(client.state());
      delay(2000);
    }
  }
  client.subscribe(update_topic,1);
  client.subscribe(update_hb_range,1);
}


void messageReceived(char* topic, byte* payload, unsigned int length){
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  
  
  if(strcmp(topic,update_topic)==0){
    receivedTime = true;
    interval = atoi((char*)payload)*60000;
    Serial.print("Interval(ms): ");
    Serial.println(interval);
  }
  if(strcmp(topic,update_hb_range)==0){
    received_min_hb = true;
    received_max_hb = true;

    boolean del_found = false;
    int po = 0;
    int num;
    for (int i=length-1; i>=0; i--) {
      if(payload[i]=='-'){
        del_found = true;
        po = 0;
      }else{
        if(!del_found){
          int num = (int)payload[i]-48;
          max_hb = max_hb + (num*pow(10,po));
          po++;
        }
        if(del_found){
          int num = (int)payload[i]-48;
          min_hb = min_hb + (num*pow(10,po));
          po++;
        }
      }
      
    }
    
    Serial.print("Min HB: ");
    Serial.println(min_hb);
    Serial.print("Max HB: ");
    Serial.println(max_hb);

    Serial.println();
  Serial.println("-----------------------");
  }
 
}

void configModeCallback (WiFiManager *myWiFiManager) {
  Serial.println("Entered config mode");
  digitalWrite(wifiLed,HIGH);
}

void resetWifiSettings(){
  WiFiManager wifiManager;
  wifiManager.resetSettings();
  ESP.reset();
}



void playBuzzer(int times){
   for(int i=0; i<times; i++){
    tone(buzzer,freq,500);
    delay(1000);
   }
   
}

void buttonPressed(){
  Serial.println("Button pressed");
  bool stop = false;
  int start =  millis();
  while(!stop && digitalRead(touch_button)==HIGH){
    if(millis()-start >=2000){
      Serial.println("Button pressed for 2 sec");
      checkConnections();
      client.publish(new_alert,mqttID);
      stop = true;
    }
  }
}


int string_to_int(const char* number, int size)
  {
      char stackbuf[size+1];
      memcpy(stackbuf, number, size);
      stackbuf[size] = '\0'; //or you could use strncpy
      return atoi(stackbuf);
  }
