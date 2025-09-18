#include <SPI.h>
#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>
#include <MFRC522.h>

// Firebase
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

// Wi-Fi Credentials
#define WIFI_SSID "Saurabh"
#define WIFI_PASSWORD "1234567888"

// Firebase Credentials
#define API_KEY "AIzaSyBT3DNEB4IUVQvWcpxAb_c-wFo_wN_JEa4"
#define DATABASE_URL "https://attendance-7613c-default-rtdb.asia-southeast1.firebasedatabase.app/"

// RFID Pins (change if needed)
#define SS_PIN 4   // SDA
#define RST_PIN 5  // RST

MFRC522 mfrc522(SS_PIN, RST_PIN);

// Firebase Objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// Time (NTP)
#include <time.h>
const char* ntpServer = "pool.ntp.org";
const long gmtOffset_sec = 19800;   // GMT +5:30 IST
const int daylightOffset_sec = 0;

bool signupOK = false;

void setup() {
  Serial.begin(115200);

  SPI.begin();
  mfrc522.PCD_Init();
  Serial.println("Place your RFID card...");

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi");

  // Configure Firebase
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("Firebase signup OK");
    signupOK = true;
  } else {
    Serial.printf("Signup error: %s\n", config.signer.signupError.message.c_str());
  }

  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // Init NTP
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
}

void loop() {
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
    String cardID = getCardUID();
    Serial.println("Card Scanned: " + cardID);

    // Get Date & Time
    struct tm timeinfo;
    if (!getLocalTime(&timeinfo)) {
      Serial.println("Failed to get time");
      return;
    }

    char dateStr[20];
    strftime(dateStr, sizeof(dateStr), "%d-%m-%Y", &timeinfo);

    char timeStr[10];
    strftime(timeStr, sizeof(timeStr), "%H:%M", &timeinfo);

    // Build Firebase Path
    String basePath = "/test/" + String(dateStr) + "/" + String(timeStr);

    // Push Data
    if (Firebase.RTDB.setString(&fbdo, basePath + "/id", cardID)) {
      Serial.println("ID saved: " + cardID);
    } else {
      Serial.println("Error saving ID: " + fbdo.errorReason());
    }

    if (Firebase.RTDB.setString(&fbdo, basePath + "/time", timeStr)) {
      Serial.println("Time saved: " + String(timeStr));
    } else {
      Serial.println("Error saving time: " + fbdo.errorReason());
    }

    // Store Date once
    Firebase.RTDB.setString(&fbdo, "/test/" + String(dateStr) + "/date", dateStr);

    delay(2000);
  }
}

String getCardUID() {
  String cardUID = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    cardUID += String(mfrc522.uid.uidByte[i] < 0x10 ? "0" : "");
    cardUID += String(mfrc522.uid.uidByte[i], HEX);
  }
  cardUID.toUpperCase();
  return cardUID;
}
