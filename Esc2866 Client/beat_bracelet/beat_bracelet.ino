#include <ESP8266WiFi.h>          //ESP8266 Core WiFi Library (you most likely already have this in your sketch)
#include <DNSServer.h>            //Local DNS Server used for redirecting all requests to the configuration portal
#include <ESP8266WebServer.h>     //Local WebServer used to serve the configuration portal
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager WiFi Configuration Magic
#include <PubSubClient.h>

const char* mqttServer = "192.168.178.41";
const int mqttPort = 1883;
const char* mqttID = "1";
const char* mqttUser = "test_user";
const char* mqttPassword = "1234";
WiFiClient espClient;
PubSubClient client(espClient);

const char* update_topic = "1/new_inter";
int monitorDelay = 60000; //delay between two monitoring session in ms

void setup() {
  Serial.begin(115200);  

  connectToWiFi();
  connectToServer();

}

void loop() {
  // put your main code here, to run repeatedly:
  delay(500);
  if(!WiFi.isConnected())
    connectToWiFi();
  if(client.connected() == false)
    connectToServer();
    
  client.loop();
}

void connectToWiFi(){
  WiFiManager wifiManager;
  wifiManager.setAPCallback(configModeCallback);
  
  wifiManager.autoConnect("Beat Bracelet");

  //TURN WIFI-LED ON
  Serial.println("Wifi connected");
}


void connectToServer(){
  //Connect to the mqtt server
  client.setServer(mqttServer, mqttPort);
  client.setCallback(messageReceived);
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
 
    if (client.connect(mqttID, mqttUser, mqttPassword )) {
 
      Serial.println("Mqtt server connected"); 
      client.subscribe(update_topic);  
    } else {
 
      Serial.print("Mqtt connection failed with state ");
      Serial.print(client.state());
 
    }
    delay(2000);
  }
}


void messageReceived(char* topic, byte* payload, unsigned int length){
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  if(topic == "update_topic"){
    
  }
 
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
 
  Serial.println();
  Serial.println("-----------------------");
 
}

void configModeCallback (WiFiManager *myWiFiManager) {
  Serial.println("Entered config mode");
  //TURN WIFI-LED ON
}

void resetWifiSettings(){
  WiFiManager wifiManager;
  wifiManager.resetSettings();
  ESP.reset();
}
