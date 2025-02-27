# HSBI Rooms
Dieses GitHub-Repository enthält den Quellcode für ein Raumbuchungssystem, das an einer Hochschule verwendet werden kann. Das System ermöglicht es Mitarbeitern und Studierenden, Räume für Veranstaltungen, Meetings und andere Zwecke zu buchen.
Das Projekt entstand im Rahmen des Softwareprojekts an der Hochschule Bielefeld.

## Voraussetzungen
- Windows
- Java JDK 21 oder höher
- Maven
- PostgresSQL Server
- Browser (Chrome, Firefox...)
- IntelliJ / Eclipse

## Postgres Datenbank
Um die Software lokal auszuführen benötigen sie eine postgres Datenbank.
In IntelliJ Pro ist eine Datenbank Erstellung integriert. Hier können sie über IntelliJ eine lokale Datenbank erzeugen.

Alternativ können sie selber postgresql auf ihrem Rechner installieren.
Diese können Sie kostenfrei unter https://www.postgresql.org/ herunterladen.
Führen sie die Installation durch und starten sie pgAdmin um eine neue Datenbank zu erstellen.

Folgende Parameter sind standardmäßig gesetzt. Sollten sie andere Parameter für ihre Datenbank benutzen müssen Sie diese in den application.properties ändern.
spring.datasource.url=jdbc:postgresql://localhost:5432/BookARoom
spring.datasource.username=dev
spring.datasource.password=dev

## Verschiedene User mit Rollen stehen neben der Registrierung zur Verfügung
| Rolle          |Username                       |Passwort                     |
|----------------|-------------------------------|-----------------------------|
|Admin|admin@gmail.com            |admin            |
|FBPlanung          |fbplanung@gmail.com            |fbplanung            |
|Dozent          |testdozent@gmail.com|dozent|
|Dozent          |smeier@hsbi.de|sozial|

Wenn sie die Datenbank passend eingerichtet haben und alle Bibliotheken über Maven heruntergeladen haben können Sie das Programm über Run Application starten.
Anschließend öffnet sich ein Browserfenster mit dem Link http://localhost:8080/login.
