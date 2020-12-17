#include <WiFi.h>
#include <HTTPClient.h>
#include <time.h>
#include <DHTesp.h>

const int IRPin =  14;  
const int BuzzPin =  15; 
const int DTHPin = 26;

const int maxTemp = 30;
const int maxHumidity = 50;

const String paintingName = "femmeAuChapeau";
const char* ntpServer = "time.google.com";
const long  gmtOffset_sec = 0;
const int   daylightOffset_sec = 3600;

int IRValue = HIGH;
bool IRTriggered = false;
bool accelerometerTriggered = false;
bool humidityTriggered = false;
bool temperatureTiggered = false;
String currentIncident = "";
int currentNumberIncident = 0;

DHTesp dht;

SemaphoreHandle_t mutexIncident = NULL;

void IRAM_ATTR IRTask() {
  //digitalWrite(BuzzPin, HIGH);
  IRValue = digitalRead(IRPin);
  Serial.print("IR capteur value change to : ");
  Serial.println(digitalRead(IRPin));
  addingQueue("test");
}

void setup() {
  Serial.begin(115200);
  while (!Serial);
  
  pinMode(IRPin,INPUT);
  pinMode(BuzzPin,OUTPUT);
  dht.setup(DTHPin, DHTesp::DHT11);
  
  initRestRequest();
  mutexIncident = xSemaphoreCreateMutex();

  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);

  //attachInterrupt(digitalPinToInterrupt(IRPin), IRTask, FALLING);
  xTaskCreate(RestRequestTask, "RestRequestTask1", 10000, NULL, 1, NULL);
  xTaskCreate(RestRequestTask, "RestRequestTask2", 10000, NULL, 1, NULL);
  xTaskCreate(IRTask, "IRTask", 10000, NULL, 10, NULL);
  xTaskCreate(DTHTask, "DTHTask", 10000, NULL, 5, NULL);
  
  for(;;);
}

void loop() {
  
}

void IRTask(void *pvParameters) {
  for(;;){
    if (digitalRead(IRPin) != IRValue){
      xSemaphoreTake(mutexIncident, portMAX_DELAY);
      struct tm timeinfo;
      char currentIncidentTime[20];

      if(!getLocalTime(&timeinfo))
      {
        Serial.println("Failed to obtain time");
        String("TimeError").toCharArray(currentIncidentTime, 20);
      }else{
        strftime(currentIncidentTime, 20, "%m-%d-%Y_%H:%M:%S", &timeinfo);
      }
      
      //digitalWrite(BuzzPin, HIGH);
      IRValue = digitalRead(IRPin);
      Serial.print("IR capteur value change to : ");
      Serial.println(digitalRead(IRPin));

      if (currentIncident == ""){
        currentIncident = String(currentIncidentTime);
      }
      
      addingQueue(paintingName + String("/incident/")+currentIncident+String("/default/")+ 
                  currentNumberIncident+String(";PATCH;") + String("{\"fields\": {\"sensor\": {\"stringValue\": \"IR\"},\"value\": {\"integerValue\": \"") +
                  String(IRValue) + String("\"}, \"dateTime\": {\"stringValue\": \"") + String(currentIncidentTime) + String("\"}}}"));
                  
      if (!IRTriggered){
        IRTriggered = true;
        addingQueue(paintingName + String("/incident/")+currentIncident+String(";PATCH;") + 
                  String("{\"fields\": {\"IR\": {\"booleanValue\": \""+String(IRTriggered)+"\"},\"accelerometer\": {\"booleanValue\": \""+
                  String(accelerometerTriggered)+"\"}, \"humidity\": {\"booleanValue\": \""+String(humidityTriggered)+"\"}, \"temperature\": {\"booleanValue\": \""+
                  String(temperatureTiggered)+"\"}}}"));
      }
      
      currentNumberIncident += 1;
      
      xSemaphoreGive(mutexIncident);
    }
    delay(100);
  }
}

void DTHTask(void *pvParameters) {
  for(;;){
    xSemaphoreTake(mutexIncident, portMAX_DELAY);
    struct tm timeinfo;
    char currentIncidentTime[20];
    
    if(!getLocalTime(&timeinfo))
    {
      Serial.println("Failed to obtain time");
      String("TimeError").toCharArray(currentIncidentTime, 20);
    }else{
      strftime(currentIncidentTime, 20, "%m-%d-%Y_%H:%M:%S", &timeinfo);
    }

    TempAndHumidity newValues = dht.getTempAndHumidity();

    if (dht.getStatus() != 0) {
      Serial.println("DHT error status: " + String(dht.getStatusString()));
      //Todo Envoyer une erreur
    }
    else{
      Serial.println(" T:" + String(newValues.temperature) + " H:" + String(newValues.humidity));
      addingQueue(paintingName + String("/humidityAndTemperature/")+String(currentIncidentTime)+ 
                String(";PATCH;") + String("{\"fields\": {\"temperature\": {\"doubleValue\": \""+ String(newValues.temperature) +"\"},\"humidity\": {\"doubleValue\": \"") +
                String(newValues.humidity) + String("\"}}}"));
      
      if (newValues.temperature > maxTemp or newValues.humidity > maxHumidity){
        //digitalWrite(BuzzPin, HIGH);

        if (currentIncident == ""){
          currentIncident = String(currentIncidentTime);
        }

        if (newValues.temperature > maxTemp){
          currentNumberIncident += 1;
          addingQueue(paintingName + String("/incident/")+currentIncident+String("/default/")+ 
                  currentNumberIncident+String(";PATCH;") + String("{\"fields\": {\"sensor\": {\"stringValue\": \"temperature\"},\"value\": {\"doubleValue\": \"") +
                  String(newValues.temperature) + String("\"}, \"dateTime\": {\"stringValue\": \"") + String(currentIncidentTime) + String("\"}}}"));
          if (!temperatureTiggered){
            temperatureTiggered = true;
            addingQueue(paintingName + String("/incident/")+currentIncident+String(";PATCH;") + 
                  String("{\"fields\": {\"IR\": {\"booleanValue\": \""+String(IRTriggered)+"\"},\"accelerometer\": {\"booleanValue\": \""+
                  String(accelerometerTriggered)+"\"}, \"humidity\": {\"booleanValue\": \""+String(humidityTriggered)+"\"}, \"temperature\": {\"booleanValue\": \""+
                  String(temperatureTiggered)+"\"}}}"));
          }
        
        }
        if(newValues.humidity > maxHumidity){
          currentNumberIncident += 1;
          addingQueue(paintingName + String("/incident/")+currentIncident+String("/default/")+ 
                  currentNumberIncident+String(";PATCH;") + String("{\"fields\": {\"sensor\": {\"stringValue\": \"humidity\"},\"value\": {\"doubleValue\": \"") +
                  String(newValues.humidity) + String("\"}, \"dateTime\": {\"stringValue\": \"") + String(currentIncidentTime) + String("\"}}}"));
         if (!humidityTriggered){
            humidityTriggered = true;
            addingQueue(paintingName + String("/incident/")+currentIncident+String(";PATCH;") + 
                  String("{\"fields\": {\"IR\": {\"booleanValue\": \""+String(IRTriggered)+"\"},\"accelerometer\": {\"booleanValue\": \""+
                  String(accelerometerTriggered)+"\"}, \"humidity\": {\"booleanValue\": \""+String(humidityTriggered)+"\"}, \"temperature\": {\"booleanValue\": \""+
                  String(temperatureTiggered)+"\"}}}"));
          }
        }
        
      }
    }
    xSemaphoreGive(mutexIncident);
    if (humidityTriggered or temperatureTiggered){
      delay (10000);
    }else{
      delay (60000);
    }
    
  }
}
