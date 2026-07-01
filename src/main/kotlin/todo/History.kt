package todo

data class History(
    val past: List<TodoList> = emptyList(),
    val present: TodoList = TodoList(),
    val future: List<TodoList> = emptyList()
)

fun applyEditToHistory(history: History, edit: Edit): History =
    History(
        past = history.past + history.present,
        present = applyEdit(history.present, edit),
        future = emptyList()
    )

fun undo(history: History): History =
    if (history.past.isEmpty()) history
    else History(
        past = history.past.dropLast(1),
        present = history.past.last(),
        future = listOf(history.present) + history.future
    )

fun redo(history: History): History =
    if (history.future.isEmpty()) history
    else History(
        past = history.past + history.present,
        present = history.future.first(),
        future = history.future.drop(1)
    )

fun applyCommand(history: History, command: Command): History = when (command) {
    is Edit -> applyEditToHistory(history, command)
    is Undo -> undo(history)
    is Redo -> redo(history)
}

fun replay(initial: TodoList, edits: List<Edit>): TodoList =
    edits.fold(initial) { state, edit -> applyEdit(state, edit) }
