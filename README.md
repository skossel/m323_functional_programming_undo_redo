# Undo/Redo Todo (Module m323, Functional Programming)

A small CLI Todo application in Kotlin. You can create tasks and subtasks, check them off, and undo or redo every action. The project is kept small so that the functional concepts are clearly visible and easy to explain.

## Features

- `add <task>` creates a top-level task
- `add <subtask> to <parent>` nests a subtask under a parent
- `complete <task>` completes a task including its entire subtree
- `undo` undoes the last change
- `redo` reapplies a previously undone change
- `quit` terminates the program
- After each action, the current state of the list is displayed
- A parent is automatically marked as done once all its subtasks are completed (recursively over any depth)

## Running

Requirement: a JDK (tested with JDK 21, JDK 17+ is sufficient).

Easiest in IntelliJ IDEA: Open the project, Gradle imports automatically. Then run `Main.kt`. Tests are located in the `src/test` directory.

Via command line with Gradle:

```
gradle run      # starts the app
gradle test     # runs all unit tests
```

(If a Gradle wrapper is present, use `./gradlew run` or `./gradlew test` instead.)

## Example Session

```
> add Trip
[ ] Trip
> add Tickets to Trip
[ ] Trip
  [ ] Tickets
> add Hotel to Trip
[ ] Trip
  [ ] Tickets
  [ ] Hotel
> complete Tickets
[ ] Trip
  [x] Tickets
  [ ] Hotel
> complete Hotel
[x] Trip
  [x] Tickets
  [x] Hotel
> undo
[ ] Trip
  [x] Tickets
  [ ] Hotel
> redo
[x] Trip
  [x] Tickets
  [x] Hotel
```

## Project Structure

The entire code is divided into a pure core and a thin impure shell.

| File | Role | Pure? |
|---|---|---|
| `Model.kt` | Data model (`Task`, `TodoList`) and logic (add, complete, isComplete) | yes |
| `Command.kt` | `sealed` command hierarchy and `applyEdit` | yes |
| `History.kt` | undo/redo via immutable snapshot lists | yes |
| `Parser.kt` | String to Command | yes |
| `Render.kt` | TodoList to text tree | yes |
| `Main.kt` | Console loop, only place with IO | no |

## Functional Concepts and their Implementation

| Concept | Location in Code |
|---|---|
| Pure Functions | all functions except in `Main.kt` (e.g., `addTask`, `completeTask`, `applyEdit`, `undo`, `render`) |
| Immutable Data | `Task` and `TodoList` are `data class`, changes only via `.copy()`, never mutation |
| Recursion | `isComplete`, `markAllDoneRecursive`, `updateTaskRecursive`, `renderTaskRecursive`, and the `loop` in Main (tailrec) |
| Pattern Matching | exhaustive `when` over `sealed` types in `applyEdit` and `applyCommand` (no `else`) |
| map / filter | `list.tasks.map { ... }` and `subtasks.map(::markAllDone)` in `Model.kt`; `subtasks.all { isComplete(it) }` in `isComplete` |
| Higher-Order Functions | `updateTaskRecursive(task, name, transform)` takes a function, used by `addSubtask` and `completeTask` |
| Isolated Side-Effects | all `println` and `readlnOrNull` strictly in `Main.kt` |

### The Core Idea: Why Undo/Redo is almost free here

The history consists of three immutable lists: `past`, `present`, `future`.
Because every `TodoList` state is immutable, a snapshot is simply the value itself. Nothing needs to be copied or frozen. Undo just means: making the last state from `past` the new `present` and pushing the current `present` into the `future` for a later redo. In a mutable world, you would have to take a deep copy at each step; otherwise, all snapshots would point to the same modified object.

### Separation of Business Logic and User Interface

`parse` returns `null` for invalid input and `render` only returns a string. Neither of them prints anything. The decision to output an error message or the list status is made solely by `Main.kt`. As a result, the entire core is deterministic and testable without mocking.

## Tests

All pure functions are covered by `kotlin.test`:

- `ModelTest`: add, addSubtask, complete, automatic parent completion, and a test proving that the original object remains unchanged
- `CommandTest`: `applyEdit` for Add, AddSub, Complete
- `HistoryTest`: undo, redo, clearing the redo `future` after a new edit, and undo on empty history
- `ParserTest`: all commands including names with spaces and invalid input
- `RenderTest`: indentation and auto-checkmarks for parents

`Main.kt` is deliberately not unit-tested as it is only the isolated IO shell.

## Known Simplifications

- Task names are assumed to be (mostly) unique. `updateTaskRecursive` would change all matches if names are duplicated.
- In `add <subtask> to <parent>`, the split happens at the last ` to `, so the parent name remains intact even if the subtask text contains the word "to".

## AI Usage (Mandatory Documentation)

Models used: 
- Claude 3 Opus (initial creation)
- Junie (JetBrains AI) (refactoring for simplification and translation)

The program was **not** generated in a single "one shot". First, a basic structure was developed step-by-step with Claude. Since this was too complex for the user, a targeted refactoring was performed by **Junie**.

### Refactoring Prompts (Junie)
1. "Please write the simplest possible code that is functional and easy to understand. Important: all criteria must be fully met."
2. "Simplify the model logic in Model.kt and add German comments to increase understandability. Maintain recursion and immutability." (Note: later removed comments)
3. "Adjust the tests to the new function names and the German localization."
4. "no comments in the code, the code must be written understandably without comments"
5. "everything in English"

### Original Prompts (Claude)
1. Planning and topic selection: Recommendation for a suitable project.
2. Data model: Creation of immutable classes and recursive functions.
3. Commands: Modeling the command hierarchy.
4. Undo/Redo: Implementation via snapshot lists.
5. Parser and Render: Implementation of input and output logic.
6. Main: Implementation of the IO shell.
7. Tests: Creation of unit tests.
