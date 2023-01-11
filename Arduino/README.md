# IMUOSC for Arduino IDE

## Overview
A Program to control SuperCollider using IMU data from M5StickC Plus.

[![](https://img.youtube.com/vi/TP_IzwO8O2c/0.jpg)](https://www.youtube.com/watch?v=TP_IzwO8O2c)

## Required
- [M5StickC Plus](https://shop.m5stack.com/collections/m5-controllers/products/m5stickc-plus-esp32-pico-mini-iot-development-kit)
- [SuperCollider](https://supercollider.github.io/)
- [ArduinoOSC (by Hideaki Tai)](https://github.com/hideakitai/ArduinoOSC)
- (WiFi connection)

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
- Open IMUOSC/Arduino/main/main.ino
- Change M5ID value (= sensor ID)
```
#define M5ID 0  // Set the sensor ID
```
- Set the SSID and PIN of your access point (Wifi router)
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
- Place [IMU-utils.sc](https://github.com/piperauritum/IMUOSC/blob/main/SuperCollider/IMU-utils.sc) into Platform.userExtensionDir or Platform.systemExtensionDir

## Receive the data with SuperCollider
- See [plotter_test.scd](https://github.com/piperauritum/IMUOSC/blob/main/SuperCollider/plotter_test.scd)

## Operate the M5StickC Plus
- Press button A to display screen for 5 seconds.
- Press button B to switch pc_addr.
- Press power button for 2 seconds to power on.
- Press power button for 6 seconds to power off.