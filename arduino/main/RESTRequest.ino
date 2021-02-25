#include <WiFi.h>
#include <HTTPClient.h>
#include <LinkedList.h>

const char* ssid = "Livebox-Matthieu";
const char* password = "Mattbal38";
const String serverName = "https://firestore.googleapis.com/v1/projects/projetiot-19922/databases/(default)/documents/tableaux/";

LinkedList<String> myLinkedList;
LinkedList<String> myLinkedListNotif;
SemaphoreHandle_t mutexList = NULL;
SemaphoreHandle_t mutexListNotif = NULL;
bool isAlarmActivatedFlag = true;

void connectWifi(){
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void initRestRequest(){
  mutexList = xSemaphoreCreateMutex();
  mutexListNotif = xSemaphoreCreateMutex();
  connectWifi();
}

void RestRequestTask(void *pvParameters){
  for(;;){
    while (myLinkedList.size() == 0){
      delay(500);
    }
    if(WiFi.status()== WL_CONNECTED){
      int httpResponseCode = -1;
      HTTPClient http;

      xSemaphoreTake(mutexList, portMAX_DELAY);
      String currentIncident = myLinkedList.shift();
      String sizeList = String(myLinkedList.size());
      xSemaphoreGive(mutexList);
      
      // Your Domain name with URL path or IP address with path
      //http.begin(serverName + paintingName + String("/incident/") + split(currentIncident,';',0));
      http.begin(serverName + split(currentIncident,';',0));

  
      http.addHeader("key", "AIzaSyCsWE_ThZEuGLXrYArWVcyd9");
      http.addHeader("Content-Type", "application/json");
      
      if (split(currentIncident,';',1) == "PATCH"){
        httpResponseCode = http.PATCH(split(currentIncident,';',2));
      }else{
        httpResponseCode = http.POST(split(currentIncident,';',2));
      }
      Serial.print("HTTP Response code: ");
      Serial.print(httpResponseCode);
      Serial.print(" -- Size List: ");
      Serial.println(sizeList);
      http.end();

      if (httpResponseCode != 200){
        xSemaphoreTake(mutexList, portMAX_DELAY);
        myLinkedList.add(currentIncident);
        xSemaphoreGive(mutexList);
      }
      
    }
    else {
      Serial.println("WiFi Disconnected");
      connectWifi();
    }   
  }
}

void addingQueue(String param){
  xSemaphoreTake(mutexList, portMAX_DELAY);
  myLinkedList.add(param);
  xSemaphoreGive(mutexList);
  
}

void addingQueueNotif(String param){
  xSemaphoreTake(mutexListNotif, portMAX_DELAY);
  myLinkedListNotif.add(param);
  xSemaphoreGive(mutexListNotif);
  
}

String split(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
        found++;
        strIndex[0] = strIndex[1]+1;
        strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void RestRequestNotifTask(void *pvParameters){
  for(;;){
    while (myLinkedList.size() == 0){
      delay(500);
    }
    if(WiFi.status()== WL_CONNECTED){
      int httpResponseCode = -1;
      HTTPClient http;

      xSemaphoreTake(mutexList, portMAX_DELAY);
      String currentNotif = myLinkedListNotif.shift();
      String sizeList = String(myLinkedListNotif.size());
      xSemaphoreGive(mutexList);

      if(split(currentNotif,';',0) == "POST"){
      
      http.begin("https://fcm.googleapis.com/fcm/send");
  
      http.addHeader("Authorization", "Bearer AAAAiLXKia8:APA91bHu6FLOyTrMYdK6FqofVq5v1J4x4IiUMKDsK7ENSstJ5ABhTnZQ3dxQHRa0PPdDmSkVxDBegqGlUuXNLjLoUvB4Pb3Fsj8zeo3JuxB1G0mAL7LfWb1KsLQxzYl42XrjQdy_0vUw");
      http.addHeader("Content-Type", "application/json");
      
      httpResponseCode = http.POST(split(currentNotif,';',1));
      }
      else{
        http.begin("https://firestore.googleapis.com/v1/projects/projetiot-19922/databases/(default)/documents/oeuvres/femmeAuChapeau/");
  
        http.addHeader("key", "AIzaSyCsWE_ThZEuGLXrYArWVcyd9");
        http.addHeader("Content-Type", "application/json");
        
        httpResponseCode = http.PATCH(split(currentNotif,';',1));
      }
      
      Serial.print("HTTP Response code: ");
      Serial.print(httpResponseCode);
      Serial.print(" -- Size List: ");
      Serial.println(sizeList);
      http.end();

      if (httpResponseCode != 200){
        xSemaphoreTake(mutexList, portMAX_DELAY);
        myLinkedList.add(currentIncident);
        xSemaphoreGive(mutexList);
      }
      
    }
    else {
      Serial.println("WiFi Disconnected");
      connectWifi();
    }   
  }
}

void GetFlagTask(void *pvParameters){
  String isAlarmActivated = "isAlarmActivated";
  String isAloramOn = "isAloramOn";
  for(;;){
    delay(500);
    if(WiFi.status()== WL_CONNECTED){
      int httpResponseCode = -1;
      HTTPClient http;

      http.begin("https://firestore.googleapis.com/v1/projects/projetiot-19922/databases/(default)/documents/oeuvres/femmeAuChapeau");
  
      http.addHeader("key", "AIzaSyCsWE_ThZEuGLXrYArWVcyd9");
      http.addHeader("Content-Type", "application/json");
      
      httpResponseCode = http.GET();

      if (httpResponseCode == 200){
        String payload = http.getString();
        char res[payload.length() + 1];
        
        if(res[payload.indexOf(isAlarmActivated)+31] = 't'){
          ButtonInterrupt();
          
        }
        if(res[payload.indexOf(isAloramOn)+46] = 't'){
          isAlarmActivatedFlag = true;
          
        }
        else{
          isAlarmActivatedFlag = false;
        }
      }

      http.end();
      
    }
    else {
      Serial.println("WiFi Disconnected");
      connectWifi();
    }   
  }
}

bool getisAlarmActivated(){
  return isAlarmActivatedFlag;
}
