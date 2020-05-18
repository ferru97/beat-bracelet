#Beat Bracelet
![](https://lh3.googleusercontent.com/5GtG0xIfpFIlJCm2MsdGa1MYo2sihoaa9xkutDN5pMkCv0HyEZAD0QMdgh8jO9rj_J_K9MDCPkknuP7h5h72SCQXxH2XqURZ8BA2UVLW3YH6w2c-n3dmtHLOLUmv_ZWlNyirdxSItkSnKLUc-zRyiksqeNdWDqeLbZJFbtVAKYhn1o6tY4evTHPojIjggmcGOfGUVA4dvFAc6LvvkxS19FjqaX17cIOktEbGsb_ra0YNRmEuGz9BvJLv8KE4mKKmwZqBQt8yoONcmKE_tUsN2zN-1Q__pJ9D3wdNFi8pBpgBDzszORsr7p6KejFZrpe1N1CBdiZ7mCshDqmMvyvoAGFx0-EEJSa30jrqJCG5rafDRW5-i9z1mqA9mic7XwjW7DFPjITTuYnsoWqJLMV_9fto8I1swi0YtdhI7TuI_0o8096ZP7MlncgxBXH2BQdJQjExr5su3tvWc_9v8saPjZdPTyvI5zrcIx_5hGb3KPLGZ-pe54o74jZPnJAtWaHy6pegwoAWpxFEmxVwZ3xA0rARhfNz4Y9MGoOMjT_vZm2jT2LNSvZ76_hCnyitVjDHKLX-_yisUyDO6HRl4qHZeHfFPJd3GEYZnfnCElkQKAqa_k8-bNQTNW0RfU6teDH05pjftm3fFQhBGd_-TtnXdA9tZbpTnpJWiMNaxaUaxynbbXSuJppv9KMzvJFD=w503-h403-no?authuser=0)

Beat Bracelet is an indoor, wifi connected smart bracelet useful to keep track of the health state of a person.

It is used to:
- Constantly measure and remotely store the user's heart rate
- Send a real-time alert in case of anomalous heart rate detection
- Send a real-time alert in case of pressing of an on-board “panic button”

The measurements and alert can be then viewed on a dedicated mobile applica􀆟on.


##Context
Living alone at home, especially in old age, can sometimes be dangerous. 

In the event of an accident or illness, requesting immediate help is indispensable and sometimes necessary to save a life.

However, this is not always possible if there is no one at home who can help the injured person and call for help.


##Technologies
![](https://lh3.googleusercontent.com/eDReuFOIi2Z6bIKryhM4WrnoUPz6BqaDUgkDyzksobGorEWsAupqfvNWTWIG10PXc2nsrpCesefnpCLTI26VAh38p9-IvZ0sA6QGCfloY3RcNkJcmn-Lx_bTIFURwwPlHwluKpX60M3BBzm-z2RAam2efNJgfRnteWDFbCNGIzJdctrLIhnKjZE8dvUMCpmQ19-XKKLCz4elYTA8SuVQRfe46Y_IAQ6KBYjabskLEeXNM2EDlUjkiqWSa6-zVM390W_NYAeIWSLV4fkA32PYIiEzmC6Oq3GcEVrDglVcpFav7Nhe4TII3RVnbVZZISjz8uSqbCVQNaLr-JmU4Hpeei_cOFedveAPwQCn0fY1GPJoFalrclrdyVc6-wadN8yBkJx2uIqXYBvU0Y0oJOLENPd9X_3o7Lec0U-uJbdvjXEN4LUH57PoeiKyG-se25tSMGcDdXshJYY3vOD1lZFzJwRKSnzA0bppuKsUU81SXTrW5FCNazV4czIqaxEMRmOVauRstkkWVVvwHirtLZP9c-2e9o_DYNkfrPXWicQup3YdFT7FSUk5t6snXEw7PRUGZu_xJijIR6Bqp9mTKD_VhWKFn8nH0VrFeJzoRU7Nwo5ImezQGH_zVBQnSSfDgmtbenT0RiZqU66_LcXUJpflc5PZotM_SNdwfdXshjVNh9Ax8Mt3Ri8RY_AO_tal=w1740-h262-no?authuser=0)

- ESP8266-12F: the main core of the bracelet
- MQTT: bracelet-to-server & bracelet-to-app communication
- MongoDB database: store the alert events and measurements
- Apache Web Server: provide API for the mobile application
- Android Studio: development of the android application

##System Architecture
![](https://lh3.googleusercontent.com/njLDT3X9jS5649AqHNC6kBE1L4lH0HNQeFemS5CSYOdv0td5NmhDYkCU-kcriyI6-ax9uQ3H__ThplFBYzgYb-cSKtL9N1oV5H3D5OwJwbBy8EuqncMUCaYEJT5cd4oWHyezllHMqMzgib_qG-5nZhIDq6aZtPDGNXCOqpYkhJZk9oV0iag1y_jJj65WnD3E1nVghG-zqBg9EC_iIDWEeIzVQy6uTPDFYfEcKko36f-MhJcaWDH2ovC_WtqC1SfaZHKt-6NiBaZJZBVnOVhUjC6k3TXSQYJM6gQJJPVCdt56SMQJVJCtFxSpEOTtEr1CCf8275xFwenWy7I3rY4KaTgThN-f6lY-xpHtfmZOT5tpYvnpjzwVaa9YpDCLrn5Dle13w2H9VIBHmwcKHY3NjJKwGv28riT72lFbLEvBUh212onCxVZOySlT8oe7wFqCC_Sj9GV6D64jwoWbvfebM64i77EpTLbfGkF8dscRcIw7tjLcKempil9G0lOxWAkZ1J6uNJKjsSVV-faade8t0qf4Q2S8xZyK4KEa5LwZjjR34IBftTX3z-I5z6g98SBqi0_m0Njb5pHwJP4XHuyIpAT2dBd54qa-ngZBEZJgiMZATsZPHh3p-SkU4p-FzXnifErd2NA03b3f7rxbl9NWWTGo58Nm7mZBvNxQ80S4cG71LVDxQAtwY7od6o63=w857-h888-no?authuser=0)

The smart bracelets communicate with the **MQTT server** to publish new heart rate measurements, send alerts and get operational parameters.

The users mobile application interacts with the** MQTT server** to get real-time alerts and with the **Apache server** to manage the login, bracelet association and retrieve measurement data.

The measurements values, alert events and user application data are stored on a **MongoDB database**.

##Prototype
![](https://lh3.googleusercontent.com/xkwJ7gwDXGIJbXbSefj5gI_e8mCcOFWwLpcteELtsvK_dpsmHESYXz98oY_M1zu-AETaJAr-I2Z7x2ZUmpPfzmK5QlBNIKng4UE-Vd6NQyyjkj497HwSJJAzHD9Rz31l1xAxrkPOsxbJIKqnT66hpJITd_8ioCEnGwFpZZdX_UC4pt4P8jTwBF6yvas6Njf9M-BW-YYZq2bR6oD6Hksh69OgeQr5XXmUF3HebsKg7ZpUCsPzsyFTu15PRrSFE4yYiEPtPuLq0RmxoxcWAXAPPDpBvO9QKfIrq6PJ2ffkyiuJZnFeYj0MK7DqCfOaWX3anbAO8aHA-QUOW8IeVtyTN4Kka2RcIOqzWzdmIaP60APceub7pNTu60sfueaOBgg1FKgUhjv21pteO9BG3LXzDexVLDO7hJLfdqMI-qRYHdS4qNYlqpHoV0rOs71mepnlxESRYTWzodaWB6I6EDQGNRhL6K66VgTqjGp4PscXM31zEvzOwTDLW3lnczxNLOM232y17XHri6NKInEGfRftcCE_okO4ew2N5hhNLePcFHiUQl-9Zwd2s1XOVApzgoZd1R8iJMWHKWjnalt0W4Gg2wECCWAWkIxhKnemwhjUuariIexf79jKlhYxejb0VG2aupXY4HdUwNyWY9PAZHJMmPb5lWslqBGs4PKY227u-hPL8oTq_WjOVNfnXAsk=w1772-h766-no?authuser=0)


#### Directories organization
- The folder **Android_app** contains the source code of the Android Application
- The folder **Esc2866 Client** contains the source code of the bracelet software
- **HTTPserver.js** contains the source code of the HTTP server
- **MQTTserver.js** contains the source code of the MQTT server


