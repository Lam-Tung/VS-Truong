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

Aufgabe 5:

xclient           | [Timer-0] INFO ExternalClient - Get all info performed successfully...
xclient           | [Timer-0] INFO ExternalClient - Client 21:
xclient           | 237, 491, 200, 505, 454, 571, 451, 359, 221, 352, 184, 
xclient           | Client 41:
xclient           | 107, 210, 195, 198, 106, 99, 199, 297, 255, 246, 
xclient           | Client 11:
xclient           | 230, 325, 386, 355, 289, 251, 346, 151, 
xclient           | Client 31:
xclient           | -51, 246, -21, 233, 55, 310, 73, 393, 226, 334, 225, 
xclient           | Client status: 
xclient           | Client 21:
xclient           | up, 
xclient           | Client 41:
xclient           | up, 
xclient           | Client 11:
xclient           | up, 
xclient           | Client 31:
xclient           | up, 
xclient           | Client 32:
xclient           | 171, 322, 154, 405, 158, 502, 120, 332, 124, 232, 109, 
xclient           | Client 22:
xclient           | 243, 357, 287, 348, 314, 412, 383, 414, 129, 177, 
xclient           | Client 42:
xclient           | 235, 234, 322, 240, 261, 181, 324, 123, 32, 158, -34, 189, 
xclient           | Client 12:
xclient           | 290, 245, 275, 142, 99, 
xclient           | Client status: 
xclient           | Client 32:
xclient           | up, 
xclient           | Client 22:
xclient           | up, 
xclient           | Client 42:
xclient           | up, 
xclient           | Client 12:
xclient           | up, 
xclient           | Client 33:
xclient           | 121, -10, 78, -100, 138, -46, 188, 291, 279, 240, 
xclient           | Client 23:
xclient           | 212, 212, 279, 335, 
xclient           | Client 43:
xclient           | 277, 56, 306, 84, 241, 2, 157, 240, 259, 315, 163, 
xclient           | Client 13:
xclient           | 315, 225, 462, 179, 385, 107, 328, 105, 308, 290, 384, 191, 
xclient           | Client status: 
xclient           | Client 33:
xclient           | up, 
xclient           | Client 23:
xclient           | up, 
xclient           | Client 43:
xclient           | up, 
xclient           | Client 13:
xclient           | up, 
xclient           | 




