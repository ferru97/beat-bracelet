#include <ESP8266WiFi.h>          //ESP8266 Core WiFi Library (you most likely already have this in your sketch)
#include <DNSServer.h>            //Local DNS Server used for redirecting all requests to the configuration portal
#include <ESP8266WebServer.h>     //Local WebServer used to serve the configuration portal
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager WiFi Configuration Magic

const char* mqttServer = "192.168.178.41";
const int mqttPort = 1883;
const char* mqttID = "id-user-test";
const char* mqttUser = "test_user";
const char* mqttPassword = "test_password";
WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
  Serial.begin(115200);
  // put your setup code here, to run once:
  WiFiManager wifiManager;
  wifiManager.setAPCallback(configModeCallback);
  
  wifiManager.autoConnect("Beat Bracelet");

  //TURN WIFI-LED ON
  Serial.println("Wifi connected");

  //Connect to the mqtt server
  client.setServer(mqttServer, mqttPort);
  client.setCallback(messageReceived);
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

}

void loop() {
  // put your main code here, to run repeatedly:

}

void messageReceived(){
  
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
