# IMUOSC (for Arduino IDE)

[![](https://img.youtube.com/vi/TP_IzwO8O2c/0.jpg)](https://www.youtube.com/watch?v=TP_IzwO8O2c)

## Overview
A Program to control SuperCollider using IMU data from M5StickC Plus.
<br>
The battery duration of the device with this sketch is approx. 1 hour.
<br>
Note that the device is turned on as soon as it is connected to a USB power source.
<br>
To prevent yaw drifting, keep it stationary until gyroZs are stocked (2 sec) after power-on.

## Required
- [M5StickC Plus](https://shop.m5stack.com/collections/m5-controllers/products/m5stickc-plus-esp32-pico-mini-iot-development-kit)
- [SuperCollider](https://supercollider.github.io/)
- [ArduinoOSC](https://github.com/hideakitai/ArduinoOSC)
- WiFi connection

## Setup Arduino IDE

### Install Arduino IDE (2.0.3 or later)
https://www.arduino.cc/en/software
- Download ZIP
- Unzip and run 'Arduino IDE.exe' (as administrator)

### M5Stack's board management
https://docs.m5stack.com/en/quick_start/arduino
- Do step 1 to 3
#### Step 4 (corrected)
- Click 'Select board' > An available serial port (ex. COM3)
- Select Other Board and Port > Select 'M5Stick-C-Plus' from BOARDS > OK

### Hello World
https://docs.m5stack.com/ja/quick_start/m5stickc_plus/arduino

### Install ArduinoOSC
https://github.com/hideakitai/ArduinoOSC
- Download ZIP
- Sketch > Include Library > Add .ZIP Library... > Select ZIP

## Upload sketch
- Open 'IMUOSC/Arduino/main/main.ino'
- Change M5ID value (= sensor ID)
```
#define M5ID 0  // Set the sensor ID
```
- Set the SSID and PIN of your access point (= Wifi router)
```
const char ssid[] = "SSID"; // SSID of your access point - router
const char pass[] = "PIN";  // PIN of your access point - router
```
- Set these parameters to match your PC's IP address
```
char *pc_addr[] = { "192.168.10.100", "192.168.10.101" }; // Static IP of your PC
```
```
IPAddress ip(192, 168, 10, 120 + M5ID); // The first three numbers are the same as IP of your PC
IPAddress gateway(192, 168, 10, 1);     // Default gateway of your PC
IPAddress subnet(255, 255, 255, 0);     // Subnet mask of your PC
```
- Upload it to M5StickC Plus

## Setup SuperCollider
- 'Windows Defender Firewall' > 'Allow an app or feature through Windows Defender Firewall' > Add 'scide.exe', 'sclang.exe' and 'scsynth.exe' and check 'Private' (For Windows)
- Place '[SuperCollider](https://github.com/piperauritum/IMUOSC/tree/main/SuperCollider)/IMU-utils' folder into Platform.userExtensionDir or Platform.systemExtensionDir

## Receive the data with SuperCollider
- See '[plotter_test.scd](https://github.com/piperauritum/IMUOSC/blob/main/SuperCollider/plotter_test.scd)'

## Operate the M5StickC Plus
- Press button A to display screen for 3 seconds.
- Press button B to switch pc_addr.
- Press power switch for approx. 1 second to power on/off.