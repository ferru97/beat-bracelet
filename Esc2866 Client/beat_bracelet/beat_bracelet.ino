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
const char* new_measure_topic = "5ea15877032e6dcf1174e65c/new_measure";
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

void ICACHE_RAM_ATTR buttonPressed ();
void setup() {
  Serial.begin(115200);  

  pinMode(buzzer,OUTPUT);
  pinMode(wifiLed,OUTPUT);

  pinMode(touch_button, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(touch_button), buttonPressed, RISING);

  connectToWiFi();

  client.setServer(mqttServer, mqttPort);
  client.setCallback(messageReceived);
  connectToServer();

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
  Serial.println("Avg BPM=");
  Serial.print(beatAvg);
  Serial.println("");
  itoa(beatAvg,measure_string,10);

  if(!WiFi.isConnected())
    connectToWiFi();
  if(!client.connected())
    connectToServer();

  if(client.connected())
    client.loop();

  client.publish(new_measure_topic,measure_string);
  beatAvg = 0;

  delay(10000);
}

void pubMeasurement(){
  
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
  client.subscribe(update_topic);
}


void messageReceived(char* topic, byte* payload, unsigned int length){
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  /*if(topic == "update_topic"){
    
  }*/
 
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
 
  Serial.println();
  Serial.println("-----------------------");
 
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
    //tone(buzzer,freq,500);
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
      stop = true;
    }
  }
}
