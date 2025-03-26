## Übung

Übungsserver: https://fhir.echinos.eu/

#### erstellen von Ressourcen
- erstellen Sie einen https://simplifier.net/packages/kbv.ita.for/1.1.0/files/720094 kompatiblen Patienten für eine eAU
- Fügen Sie dem Patienten eine ICD Diagnose https://simplifier.net/eau/kbvpreauconditionicd hinzu
- Fügen Sie der Diagnose den ausstellenden Arzt hinzu: https://simplifier.net/packages/kbv.ita.for/1.1.0/files/720095

#### Ressourcen auf Server anlegen
- Erstellen Sie die Ressourcen auf dem Server
- Merken Sie sich die ID, unter der die Ressource angelegt wurde, verlinken Sie ihre Ressourcen

#### Ressourcen auf Server updaten
- modifizieren Sie eine oder mehrere ihrer Ressourcen und updaten Sie die Ressourcen auf dem Server

#### Ressourcen auf Server suchen
- Suchen Sie nach ihrem Patienten (anhand des Namens und Geburtsdatum)
- Suchen Sie nach ihrem Patienten, fügen Sie mittels include Diagnose und Fall zum Suchergebnis dazu

#### Ressourcen auf Server löschen
- löschen Sie ihre Patientenressource, es wird ein Fehler geworfen, wieso?
- löschen Sie ihre Diagnoseressource, lesen Sie die Ressource nach dem Löschen mittels der Ressourcen.id