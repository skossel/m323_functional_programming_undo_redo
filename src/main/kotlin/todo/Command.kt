package todo

sealed interface Command

sealed interface Edit : Command
data class Add(val name: String) : Edit
data class AddSub(val parent: String, val name: String) : Edit
data class Complete(val name: String) : Edit

data object Undo : Command
data object Redo : Command

fun applyEdit(list: TodoList, edit: Edit): TodoList = when (edit) {
    is Add -> addTask(list, edit.name)
    is AddSub -> addSubtask(list, edit.parent, edit.name)
    is Complete -> completeTask(list, edit.name)
}
