# iotdevicetracker
IoT Devices werden zunehmend auf mobilen Systemen wie PKW, LKW, Bahn, etc. implementiert. Es ist eine Systemlösung zu entwickeln, in der IoT Devices (simulierbar durch Notebooks oder Smartphones) über MQTT regelmäßig ihre Standortdaten sowie die aktuelle CPU Temperatur über MQTT an einen Backendserver liefern. Die IoT Devices lassen sich über ein Frontend verwalten. Mit dem Frontend kann sich die Bewegungen ausgewählter IoT Devices in definierten Zeitfenstern auf einer Karte nachvollziehen und die Temperatur als Zeitdiagramm darstellen. Systemkomponenten:

    Backend-System inklusive MQTT Broker zur Annahme der Daten der IoT Devices und Bereitstellung verschiedener RESTful Web-Services entsprechend der Anforderungen an die Ausgangskanäle.

    Datenbanksystem zur Speicherung aller erfassten relevanten Daten. Aus Sicherheitsgründen werden Daten auch lokal gespeichert bis sie erfolgreich in der Datenbank abgelegt werden.

    Webbasiertes Frontend-System zur Verwaltung der IoT Devices und zur Anzeige der Bewegungs- und Messdaten. Die Darstellung der Informationen sollen „responsive“ sein. Die Implementierung und Validierung diverser Sicherheitsmechanismen zum Schutz des Gesamtsystems soll sich an den Empfehlungen von OWASP Top 10 orientieren. (https://www.owasp.org/index.php/Germany/Projekte/Top_10)

vorgesehener techn. Stack / empfohlene Kenntnisse

    RESTful API
    MQTT
    NoSQL
    Git
