#include <ESP8266WiFi.h>          //ESP8266 Core WiFi Library (you most likely already have this in your sketch)
#include <DNSServer.h>            //Local DNS Server used for redirecting all requests to the configuration portal
#include <ESP8266WebServer.h>     //Local WebServer used to serve the configuration portal
#include <WiFiManager.h>          //https://github.com/tzapu/WiFiManager WiFi Configuration Magic

void setup() {
  Serial.begin(115200);
  // put your setup code here, to run once:
  WiFiManager wifiManager;
  wifiManager.setAPCallback(configModeCallback);
  
  wifiManager.autoConnect("Beat Bracelet");

  //TURN WIFI-LED ON
  Serial.println("connected...yeey :)");


  

}

void loop() {
  // put your main code here, to run repeatedly:

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
