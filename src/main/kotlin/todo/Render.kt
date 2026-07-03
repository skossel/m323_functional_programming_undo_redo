/**
 * Dieses File kümmert sich um die visuelle Darstellung der Todo-Liste.
 * Es enthält Funktionen, um die TodoList und deren Tasks in eine
 * übersichtliche String-Repräsentation für die Konsole zu formatieren.
 */
package todo

fun render(list: TodoList): String =
    if (list.tasks.isEmpty()) "(no tasks yet)"
    else list.tasks.joinToString("\n") { renderTaskRecursive(it, 0) }

private fun renderTaskRecursive(task: Task, depth: Int): String {
    val indent = "  ".repeat(depth)
    val checkbox = if (isComplete(task)) "[x]" else "[ ]"
    val line = "$indent$checkbox ${task.name}"
    
    val renderedSubtasks = task.subtasks.map { renderTaskRecursive(it, depth + 1) }
    
    return (listOf(line) + renderedSubtasks).joinToString("\n")
}
