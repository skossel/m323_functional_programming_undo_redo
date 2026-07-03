/**
 * Dieses File definiert das Datenmodell für die Todo-Anwendung.
 * Es enthält die Klassen Task und TodoList sowie grundlegende Funktionen
 * zur Manipulation der Liste, wie das Hinzufügen von (Unter-)Aufgaben.
 */
package todo

data class Task(
    val name: String,
    val done: Boolean = false,
    val subtasks: List<Task> = emptyList(),
)

data class TodoList(
    val tasks: List<Task> = emptyList(),
)

fun isComplete(task: Task): Boolean =
    if (task.subtasks.isEmpty()) task.done
    else task.subtasks.all { isComplete(it) }

fun markAllDone(task: Task): Task =
    task.copy(done = true, subtasks = task.subtasks.map(::markAllDone))

fun updateTask(task: Task, name: String, transform: (Task) -> Task): Task =
    if (task.name == name) transform(task)
    else task.copy(subtasks = task.subtasks.map { updateTask(it, name, transform) })

fun addTask(list: TodoList, name: String): TodoList =
    list.copy(tasks = list.tasks + Task(name))

fun addSubtask(list: TodoList, parent: String, name: String): TodoList =
    list.copy(tasks = list.tasks.map { updateTask(it, parent) { p -> p.copy(subtasks = p.subtasks + Task(name)) } })

fun completeTask(list: TodoList, name: String): TodoList =
    list.copy(tasks = list.tasks.map { updateTask(it, name, ::markAllDone) })

data class Progress(val done: Int, val total: Int)

fun countProgress(tasks: List<Task>): Progress =
    tasks.fold(Progress(0, 0)) { acc, task ->
        val sub = countProgress(task.subtasks)
        Progress(
            done = acc.done + sub.done + if (isComplete(task)) 1 else 0,
            total = acc.total + sub.total + 1,
        )
    }
