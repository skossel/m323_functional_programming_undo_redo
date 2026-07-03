/**
 * Dieses File enthält Unit-Tests für die Befehlsverarbeitung (Command).
 * Es wird geprüft, ob Edits wie Hinzufügen von Aufgaben oder Unteraufgaben
 * sowie das Abschließen von Aufgaben korrekt auf die Todo-Liste angewendet werden.
 */
package todo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandTest {

    @Test
    fun applyEditAddCreatesTask() {
        val result = applyEdit(TodoList(), Add("Learn"))
        assertEquals(listOf(Task("Learn")), result.tasks)
    }

    @Test
    fun applyEditCompleteMarksDone() {
        val list = applyEdit(TodoList(), Add("Learn"))
        val result = applyEdit(list, Complete("Learn"))
        assertTrue(isComplete(result.tasks.single()))
    }

    @Test
    fun applyEditAddSubNestsTask() {
        val list = applyEdit(TodoList(), Add("Project"))
        val result = applyEdit(list, AddSub("Project", "Research"))
        assertEquals("Research", result.tasks.single().subtasks.single().name)
    }
}
