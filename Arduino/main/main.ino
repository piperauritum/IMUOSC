#include <M5StickCPlus.h>
#include <ArduinoOSCWiFi.h>

#define M5ID 0  // Set the sensor ID

float lcd_off;
float batt_chk;
float batt_thr;
bool low_batt = false;

// Wifi
const char ssid[] = "SSID"; // SSID of your access point - router
const char pass[] = "PIN";  // PIN of your access point - router

// SuperCollider
char *pc_addr[] = { "192.168.0.1", "192.168.10.100" }; // Static IP of your PC
const int pc_port = 57120;
int ipx = 0;

// Static IP of M5Stick
IPAddress ip(192, 168, 10, 120 + M5ID);
IPAddress gateway(192, 168, 10, 1);
IPAddress subnet(255, 255, 255, 0);

// IMU data
float acc[3];
float gyro[3];
float ahrs[3];

// stock gyroZ values
float stockedGyroZs[10];
int stockCnt = 0;
float adjustGyroZ = 0;
int stockedGyroZLength = 0;

float battery()
{
  float MAX_BATTERY_VOLTAGE = 4.2f;
  float MIN_BATTERY_VOLTAGE = 3.0f;
  float _vbat = M5.Axp.GetBatVoltage();
  float percent = (_vbat - MIN_BATTERY_VOLTAGE) / (MAX_BATTERY_VOLTAGE - MIN_BATTERY_VOLTAGE);
  return (percent * 100.0f);
}

void wifi_begin()
{
  WiFi.disconnect(true, true);
  delay(500);

  WiFi.config(ip, gateway, subnet);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, pass);
}

void setup()
{
  stockedGyroZLength = sizeof(stockedGyroZs) / sizeof(int); // for gyroZ LPF

  M5.begin();
  setCpuFrequencyMhz(80);
  // M5.Axp.begin(false,false,false,false,true);
  M5.Axp.ScreenBreath(15);
  M5.Lcd.setTextSize(2);

  wifi_begin();

  while (WiFi.status() != WL_CONNECTED)
  {
    M5.Lcd.setCursor(0, 0);
    M5.Lcd.print("Connecting to WiFi");
    delay(1000);
  }

  M5.Lcd.setCursor(0, 0);
  M5.Lcd.print(WiFi.localIP());
  M5.Lcd.print("    ");
  M5.Lcd.setCursor(0, 32);
  M5.Lcd.printf("BATT %3.1f", battery());
  M5.Lcd.print("% ");
  lcd_off = millis() + 5000;
  batt_chk = millis() + 10000;
  batt_thr = battery() - 10;

  M5.IMU.Init();
}

void loop()
{
  M5.update();

  // Turn off screen after a set amount of time
  if (millis() > lcd_off)
  {
    M5.Axp.ScreenBreath(0);
    M5.Axp.SetLDO2(false);
  }

  // Press A to display screen for 5 seconds
  if (M5.BtnA.isPressed())
  {
    lcd_off = millis() + 5000;
    M5.Axp.ScreenBreath(15);
    M5.Axp.SetLDO2(true);
    M5.Lcd.setCursor(0, 32);
    M5.Lcd.printf("BATT %3.1f", battery());
    M5.Lcd.print("%");
    OscWiFi.send(pc_addr[ipx], pc_port, "/batt_chk", M5ID);
  }

  // Press B to switch pc_addr
  if (M5.BtnB.wasPressed())
  {
    ipx = (ipx + 1) % 2;
  }

  // Check battery level every 10 seconds
  if (millis() > batt_chk)
  {
    // Send OSC when the battery level is low
    if (M5.Axp.GetWarningLeve() == 1 && low_batt == false)
    {
      OscWiFi.send(pc_addr[ipx], pc_port, "/low_batt", M5ID);
      low_batt = true;
    }

    // Send OSC every time the battery is 10% down
    if (battery() < batt_thr)
    {
      OscWiFi.send(pc_addr[ipx], pc_port, "/batt_chk", M5ID);
      batt_thr = battery() - 10;
    }

    batt_chk = millis() + 10000;
  }

  // Obtain IMU data
  M5.IMU.getGyroData(&gyro[0], &gyro[1], &gyro[2]);
  M5.IMU.getAccelData(&acc[0], &acc[1], &acc[2]);
  // M5.IMU.getAhrsData(&ahrs[0], &ahrs[1], &ahrs[2]); // pitch, roll, yaw

  // Prevent yaw drifting (But not very effective)
  if (stockCnt < stockedGyroZLength)
  {
    stockedGyroZs[stockCnt] = gyro[2];
    stockCnt++;
  }
  else
  {
    if (adjustGyroZ == 0)
    {
      for (int i = 0; i < stockedGyroZLength; i++)
      {
        adjustGyroZ += stockedGyroZs[i] / stockedGyroZLength;
      }
    }
    // Correct gyroZ using the average
    gyro[2] -= adjustGyroZ;

    MahonyAHRSupdateIMU(gyro[0] * DEG_TO_RAD, gyro[1] * DEG_TO_RAD, gyro[2] * DEG_TO_RAD, acc[0], acc[1], acc[2], &ahrs[0], &ahrs[1], &ahrs[2]);
  }

  OscWiFi.send(pc_addr[ipx], pc_port, "/senddata", M5ID, ahrs[0], ahrs[1], ahrs[2]);

  delay(50);
}