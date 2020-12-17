#include <WiFi.h>
#include <HTTPClient.h>
#include <LinkedList.h>

const char* ssid = "Livebox-Matthieu";
const char* password = "Mattbal38";
const String serverName = "https://firestore.googleapis.com/v1/projects/projetiot-19922/databases/(default)/documents/tableaux/";

LinkedList<String> myLinkedList;
SemaphoreHandle_t mutexList = NULL;

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
      xSemaphoreGive(mutexList);
      
      // Your Domain name with URL path or IP address with path
      //http.begin(serverName + paintingName + String("/incident/") + split(currentIncident,';',0));
      http.begin(serverName + split(currentIncident,';',0));

  
      http.addHeader("key", "AIzaSyCsWE_ThZEuGLXrYArWVcyd9");
      http.addHeader("Content-Type", "application/json");
      
      if (split(currentIncident,';',1) == "PATCH"){
        httpResponseCode = http.PATCH(split(currentIncident,';',2));
      }else{
        Serial.println(split(currentIncident,';',2));
        httpResponseCode = http.POST(split(currentIncident,';',2));
      }
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
        
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
