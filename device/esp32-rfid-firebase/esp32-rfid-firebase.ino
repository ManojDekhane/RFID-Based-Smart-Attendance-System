
---

# device/esp32-rfid-firebase/esp32-rfid-firebase.ino
```cpp
#include <WiFi.h>
#include <HTTPClient.h>
#include <MFRC522.h>
#include <SPI.h>
#include <time.h>

// ====== EDIT THESE ======
const char* WIFI_SSID = "YOUR_WIFI";
const char* WIFI_PASS = "YOUR_PASS";
const char* DATABASE_URL = "https://<your-project-id>.firebaseio.com"; // no trailing slash
String authParam = ""; // e.g. "?auth=<ID_TOKEN>" if you add device auth
// MFRC522 pins for ESP32 (adjust if needed)
#define RST_PIN  22
#define SS_PIN   21
// ========================

MFRC522 rfid(SS_PIN, RST_PIN);
unsigned long lastScanMs = 0;
String lastCard = "";

void setup() {
  Serial.begin(115200);
  SPI.begin();
  rfid.PCD_Init();

  WiFi.begin(WIFI_SSID, WIFI_PASS);
  Serial.print("Connecting WiFi");
  while (WiFi.status() != WL_CONNECTED) { delay(400); Serial.print("."); }
  Serial.println("\nWiFi connected");

  // Optional NTP for real epoch time
  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
}

String uidToString(MFRC522::Uid *uid) {
  String s = "";
  for (byte i = 0; i < uid->size; i++) {
    if (uid->uidByte[i] < 0x10) s += "0";
    s += String(uid->uidByte[i], HEX);
  }
  s.toUpperCase();
  return s;
}

unsigned long epochMillis() {
  time_t now;
  time(&now);
  if (now < 100000) return millis(); // fallback to uptime if NTP not ready
  return (unsigned long)now * 1000UL;
}

void pushScanToFirebase(const String& cardId, unsigned long ts) {
  if (WiFi.status() != WL_CONNECTED) return;
  String url = String(DATABASE_URL) + "/scanQueue.json" + authParam;
  String payload = "{\"cardId\":\"" + cardId + "\",\"ts\":" + String(ts) + ",\"readerId\":\"gate-1\"}";

  HTTPClient http;
  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  int code = http.POST(payload);
  Serial.printf("POST %d\n", code);
  http.end();
}

void loop() {
  if (!rfid.PICC_IsNewCardPresent() || !rfid.PICC_ReadCardSerial()) { delay(100); return; }
  String cardId = uidToString(&rfid.uid);
  unsigned long ts = epochMillis();

  // debounce: ignore same card within 10s
  if (cardId == lastCard && (millis() - lastScanMs) < 10000) {
    Serial.println("Ignored duplicate: " + cardId);
  } else {
    Serial.println("Scanned: " + cardId);
    pushScanToFirebase(cardId, ts);
    lastCard = cardId;
    lastScanMs = millis();
  }

  rfid.PICC_HaltA();
  delay(400);
}