# Undo/Redo Todo (Modul m323, Funktionale Programmierung)

Eine kleine CLI-Todo-Anwendung in Kotlin. Man kann Aufgaben und Unteraufgaben erstellen, sie abhaken und jede Aktion rückgängig machen (Undo) oder wiederherstellen (Redo). 

## Funktionen

- `add <task>` erstellt eine Aufgabe auf oberster Ebene
- `add <subtask> to <parent>` verschachtelt eine Unteraufgabe unter einer übergeordneten Aufgabe
- `complete <task>` schliesst eine Aufgabe inklusive ihres gesamten Teilbaums ab
- `undo` macht die letzte Änderung rückgängig
- `redo` wendet eine rückgängig gemachte Änderung erneut an
- `quit` beendet das Programm
- Nach jeder Aktion wird der aktuelle Zustand der Liste angezeigt
- Eine übergeordnete Aufgabe wird automatisch als erledigt markiert, sobald alle ihre Unteraufgaben abgeschlossen sind (rekursiv über beliebige Tiefe)
- Nach jeder Aktion wird eine Fortschrittsanzeige im Format `Progress: <erledigt>/<gesamt> completed` ausgegeben

## Ausführen

Voraussetzung: ab JDK 17 lauffähig

Gradle:

```
gradle run      # startet die App
gradle test     # führt alle Unit-Tests aus
```


## Beispielsitzung

```
> add Trip
[ ] Trip
Progress: 0/1 completed
> add Tickets to Trip
[ ] Trip
  [ ] Tickets
Progress: 0/2 completed
> add Hotel to Trip
[ ] Trip
  [ ] Tickets
  [ ] Hotel
Progress: 0/3 completed
> complete Tickets
[ ] Trip
  [x] Tickets
  [ ] Hotel
Progress: 1/3 completed
> complete Hotel
[x] Trip
  [x] Tickets
  [x] Hotel
Progress: 3/3 completed
> undo
[ ] Trip
  [x] Tickets
  [ ] Hotel
Progress: 1/3 completed
> redo
[x] Trip
  [x] Tickets
  [x] Hotel
Progress: 3/3 completed
```

## Projektstruktur

Der gesamte Code ist in einen reinen Kern (pure core) und eine dünne, unreine Schale (impure shell) aufgeteilt.

| Datei | Rolle | Rein? |
|---|---|---|
| `Model.kt` | Datenmodell (`Task`, `TodoList`) und Logik (add, complete, isComplete, countProgress) | ja |
| `Command.kt` | `sealed` Befehlshierarchie und `applyEdit` | ja |
| `History.kt` | Undo/Redo über unveränderliche Snapshot-Listen | ja |
| `Parser.kt` | String zu Command | ja |
| `Render.kt` | TodoList zu Text-Baum und Fortschrittsanzeige | ja |
| `Main.kt` | Konsolenschleife, einziger Ort mit IO | nein |

## Funktionale Konzepte und ihre Umsetzung

| Konzept | Stelle im Code |
|---|---|
| Reine Funktionen | alle Funktionen ausser in `Main.kt` (z. B. `addTask`, `completeTask`, `applyEdit`, `undo`, `render`) |
| Unveränderliche Daten | `Task` und `TodoList` sind `data class`, Änderungen nur über `.copy()` |
| Rekursion | `isComplete`, `markAllDone`, `updateTask`, `countProgress`, `renderTaskRecursive` |
| Pattern Matching | `when` über `sealed`-Typen in `applyEdit` und `applyCommand` (kein `else`) |
| map / filter / fold | `list.tasks.map { ... }` in `Model.kt`; `subtasks.all { isComplete(it) }` in `isComplete`; `tasks.fold(Progress(0, 0)) { ... }` in `countProgress` |
| Higher-Order Functions | `updateTask(task, name, transform)` nimmt eine Funktion entgegen, genutzt von `addSubtask` und `completeTask` |
| Isolierte Seiteneffekte | alle `println` und `readlnOrNull` ausschliesslich in `Main.kt` |

### Die Grundidee: Warum Undo/Redo hier fast gratis ist

Die Historie besteht aus drei unveränderlichen Listen: `past`, `present`, `future`.
Weil jeder `TodoList`-Zustand unveränderlich ist, ist ein Snapshot einfach der Wert selbst. Nichts muss kopiert oder freezed werden. Undo bedeutet nur: den letzten Zustand aus `past` zum neuen `present` machen und den aktuellen `present` für ein späteres Redo in die `future` schieben.

### Trennung von Geschäftslogik und Benutzeroberfläche

`parse` gibt bei ungültiger Eingabe `null` zurück und `render` gibt nur einen String zurück. Keine der beiden gibt etwas aus. Die Entscheidung, ob eine Fehlermeldung oder der Listenstatus ausgegeben wird, trifft ausschliesslich `Main.kt`. Dadurch ist der gesamte Kern deterministisch und ohne Mocking testbar.

## Tests

Alle reinen Funktionen sind mit `kotlin.test` abgedeckt:

- `ModelTest`: add, addSubtask, complete, automatische Eltern-Fertigstellung, `countProgress` als Fold über den Baum, und ein Test, der beweist, dass das ursprüngliche Objekt unverändert bleibt
- `CommandTest`: `applyEdit` für Add, AddSub, Complete
- `HistoryTest`: undo, redo, Leeren der Redo-`future` nach einer neuen Änderung, und undo bei leerer Historie
- `ParserTest`: alle Befehle inklusive Namen mit Leerzeichen und ungültiger Eingabe
- `RenderTest`: Einrückung, automatische Häkchen für Elternaufgaben, und die Fortschrittsanzeige

`Main.kt` wird bewusst nicht per Unit-Test geprüft, da es nur die isolierte IO-Schale ist.

## Bekannte Vereinfachungen

- Aufgabennamen werden als (weitgehend) eindeutig angenommen. `updateTask` würde bei doppelten Namen alle Treffer ändern.
- Bei `add <subtask> to <parent>` wird am letzten ` to ` getrennt, sodass der Name der übergeordneten Aufgabe intakt bleibt, selbst wenn der Text der Unteraufgabe das Wort „to" enthält.

## KI-Nutzung (Pflichtdokumentation)

Ich habe dieses Projekt selbst im Unterricht entwickelt und die funktionalen Konzepte eines nach dem anderen erarbeitet. Die KI habe ich als Assistenz eingesetzt — um jedes Konzept in idiomatisches Kotlin zu übersetzen, meinen Code zu reviewen, Fehler zu finden sowie für sprachliche und kommentarbezogene Bereinigung. Das Programm wurde bewusst **nicht** in einem einzigen „One-Shot" erzeugt; ich habe es Konzept für Konzept aufgebaut und kann jeden Teil des Codes erklären und verteidigen.

### Eingesetzte Werkzeuge
- **Claude Opus 4.8**

### Prompts 
1. „I want to make a to do list, how would you implement undo und redo in functional programming, which conecpts would be nice to implement?"
2. „Simplify the model logic in Model.kt and add German comments to increase understandability. Maintain recursion and immutability."
3. „Adjust the tests to the new function names"
4. "Give me a review to the model logic before I will continue with the implementation of that model"
5. "How would you parse the command line input of the user to actual commands for e.g. add functionality?"

### Abschliessende Review-Session (Claude Code, Opus 4.8)
In einer letzten Session habe ich Claude Code genutzt, um das Projekt zu reviewen und fertigzustellen. In dieser Session hat Claude:
- die Fold-basierte Fortschrittsanzeige (`countProgress` / `renderProgress`) samt Tests ergänzt,
- stdin angebunden, sodass die App auch über `gradle run` läuft (nicht nur aus der IDE),
