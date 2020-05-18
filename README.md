# Beat Bracelet
![](https://github.com/ferru97/beat-bracelet/blob/master/img/prev.png?raw=true)

Beat Bracelet is an indoor, wifi connected smart bracelet useful to keep track of the health state of a person.

It is used to:
- Constantly measure and remotely store the user's heart rate
- Send a real-time alert in case of anomalous heart rate detection
- Send a real-time alert in case of pressing of an on-board “panic button”

The measurements and alert can be then viewed on a dedicated mobile applica􀆟on.


## Context
Living alone at home, especially in old age, can sometimes be dangerous. 

In the event of an accident or illness, requesting immediate help is indispensable and sometimes necessary to save a life.

However, this is not always possible if there is no one at home who can help the injured person and call for help.


## Technologies
![](https://github.com/ferru97/beat-bracelet/blob/master/img/tech.png?raw=true)

- ESP8266-12F: the main core of the bracelet
- MQTT: bracelet-to-server & bracelet-to-app communication
- MongoDB database: store the alert events and measurements
- Apache Web Server: provide API for the mobile application
- Android Studio: development of the android application

## System Architecture
![](https://github.com/ferru97/beat-bracelet/blob/master/img/arc.png?raw=true)

The smart bracelets communicate with the **MQTT server** to publish new heart rate measurements, send alerts and get operational parameters.

The users mobile application interacts with the** MQTT server** to get real-time alerts and with the **Apache server** to manage the login, bracelet association and retrieve measurement data.

The measurements values, alert events and user application data are stored on a **MongoDB database**.

## Prototype
![](https://github.com/ferru97/beat-bracelet/blob/master/img/proto.png?raw=true)


#### Directories organization
- The folder **Android_app** contains the source code of the Android Application
- The folder **Esc2866 Client** contains the source code of the bracelet software
- **HTTPserver.js** contains the source code of the HTTP server
- **MQTTserver.js** contains the source code of the MQTT server


