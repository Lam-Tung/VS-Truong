# VS-Truong
Verteilte Systeme 2021

**Usage:**

Starten der Anwendung: `docker compose up`
- startet alle Clients + HQ

**Tests:**

Aufgabe 2:

Funktional   | Performance  |
------------ | ------------ |
Bei Aufruf von unbekannten REST-Endpoints(z.B. /rest-api/bla) antwortet der Server mit: *Cannot handle rest call...* und der Server laeuft ohne Absturz weiter. |Aufruf von /rest-api/history hat eine durchschnittliche Antwortdauer von 8.61 ms.


Aufgabe 3:

Funktional   | Performance  |
------------ | ------------ |
Anpassung der Strommenge ueber die RPC-Schnittstelle ist erfolgreich. Die Strommenge berechnert sich aus: `BASE_POWER +/- 100`. Vor der Aenderung ist `BASE_POWER=200`. Nach der Aenderung ist `BASE_POWER=400`.<br />`[Thread-0] INFO ClientThriftImpl - Change power request received...`<br />`[Thread-0] INFO ClientThriftImpl - Client 2 power set to 400...`<br />`[Timer-0] INFO HQ - Change power performed successfully...`<br />`[main] INFO HQ - Received UDP packet from /172.18.0.7:37940{"name":"water","id":2,"power":353,"type":"PRODUCER"}` | Das gesamte System lief erfolgreich ohne Fehler ueber 5 min.


Aufgabe 4:

Funktional   | Performance  |
------------ | ------------ |
Daten von Sensoren(Publisher) werden im Topic `POWER_UPDATE`  veröffentlich und die Zentrale/HQ (Subscriber) kann diese abrufen. <br />`producer2  [Timer-0] INFO Client - Mqtt message sent...` <br /> `hq [MQTT Call: 5b5b7a9c-e678-47a9-9c26-d670c7bea319] INFO HQ - Received Mqtt data: {"name":"water","id":2,"power":477,"type":"PRODUCER"}` |  Uebertagung von 100 Nachrichten mit jeweils 1 Sekunde delay <br /> MQTT : 115.040047685s <br /> UDP : 115.01238619s



