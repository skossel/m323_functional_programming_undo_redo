/**
 * Dieses File ist für das Parsen der Benutzereingaben zuständig.
 * Es wandelt die als String eingegebenen Befehle in entsprechende Command-Objekte um,
 * die dann von der Anwendungslogik verarbeitet werden können.
 */
package todo

fun parse(input: String): Command? {
    val line = input.trim()
    return when {
        line == "undo" -> Undo
        line == "redo" -> Redo
        line.startsWith("add ") -> parseAdd(line.removePrefix("add ").trim())
        line.startsWith("complete ") -> {
            val name = line.removePrefix("complete ").trim()
            if (name.isEmpty()) null else Complete(name)
        }
        else -> null
    }
}

private fun parseAdd(rest: String): Command? {
    if (rest.isEmpty()) return null
    
    val marker = " to "
    val index = rest.lastIndexOf(marker)
    
    return if (index >= 0) {
        val subtaskName = rest.substring(0, index).trim()
        val parentName = rest.substring(index + marker.length).trim()
        if (subtaskName.isEmpty() || parentName.isEmpty()) null 
        else AddSub(parentName, subtaskName)
    } else {
        Add(rest)
    }
}
