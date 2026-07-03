/**
 * Dieses File ist der Haupteinstiegspunkt der Todo-Anwendung.
 * Es enthält die main-Funktion und die interaktive Schleife (loop),
 * welche Benutzereingaben liest, verarbeitet und das Ergebnis auf der Konsole ausgibt.
 */
package todo

private val HELP_TEXT = """
    --- Todo App with Undo/Redo ---
    Commands:
      add <task>                    Add task
      add <subtask> to <parent>      Add subtask
      complete <task>               Complete task
      undo                          Undo last step
      redo                          Redo last step
      quit                          Quit
    ------------------------------
""".trimIndent()

fun main() {
    println(HELP_TEXT)
    loop(History())
}

private tailrec fun loop(history: History) {
    print("> ")
    val input = readlnOrNull()?.trim()
    
    if (input == null || input == "quit") {
        println("Program terminated. Goodbye!")
        return
    }

    val command = parse(input)
    val newHistory = if (command == null) {
        println("Unknown command! Please use add, complete, undo, redo or quit.")
        history
    } else {
        val updated = applyCommand(history, command)
        println("\nCurrent Todo list:")
        println(render(updated.present))
        if (updated.present.tasks.isNotEmpty()) println(renderProgress(updated.present))
        println()
        updated
    }

    loop(newHistory)
}
