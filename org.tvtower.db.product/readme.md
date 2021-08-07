# Das Editor-Produkt-Plugin

Dieses Plugin-Projekt dient einzig der Erstellung des Standalone-Editors.
Um Java-8-Kompatibilität sicherzustellen, sollte für die Erstellung ein Eclipse4Java-Produkt in der Version 2020-06 zusammen mit einem Java-8-JDK verwendet werden.
Spätere Eclipse-Versionen verlangen Java-11.
Mir war es zu ungewiss, Java-8 lediglich als Runtime-Umgebung festzusetzen.

Ausgangspunkt für die Produktkonfiguration war eine lauffähige, relativ minimale Eclipse-Run-Konfiguration.
Da diese nach dem Export nicht fehlerfrei gestartet hat (Eclipse geht auf, aber Editor startet nicht), wurden die "Installation-Details" des aus Eclipse heraus gestarteten Runtime-Eclipse und des exportierten Eclipse verglichen.
Es stellte sich heraus, dass für einige Plugins unterschiedliche Versionen verwendet waren.
Für diese wurde dann die Version in der Produktkonfiguration entsprechend des Runtime-Eclipse explizit festgelegt.
Danach funktionierte auch der Editor im Export.

Damit der Start möglichst reibungslos ist, wird ein Workspace (../workspace) inklusive Datenbankdateien mit ausgeliefert.
Der Basisworkspace liegt unter resources.

Bei einem Export müssen also noch
* das Exportverzeichnis umbenannt
* der workspace ergänzt
* die Datenbankdateien in das Projekt kopiert

werden.
Das Ergebnis zippen, teststarten und veröffentlichen.