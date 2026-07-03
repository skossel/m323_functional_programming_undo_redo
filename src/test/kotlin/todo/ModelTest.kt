/**
 * Dieses File enthält Unit-Tests für das Datenmodell (Task, TodoList).
 * Es stellt sicher, dass grundlegende Modell-Operationen wie das Markieren
 * von Aufgaben als erledigt oder das Aktualisieren von Aufgaben korrekt funktionieren.
 */
package todo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelTest {

    @Test
    fun addTaskAppendsTopLevelTask() {
        val result = addTask(TodoList(), "Shopping")
        assertEquals(listOf(Task("Shopping")), result.tasks)
    }

    @Test
    fun addSubtaskNestsUnderParent() {
        val list = addTask(TodoList(), "Trip")
        val result = addSubtask(list, "Trip", "Pack suitcase")
        assertEquals(listOf(Task("Pack suitcase")), result.tasks.single().subtasks)
    }

    @Test
    fun completingLeafMarksItComplete() {
        val list = addTask(TodoList(), "Wash dishes")
        val result = completeTask(list, "Wash dishes")
        assertTrue(isComplete(result.tasks.single()))
    }

    @Test
    fun parentAutoCompletesWhenAllSubtasksAreDone() {
        var list = addTask(TodoList(), "Trip")
        list = addSubtask(list, "Trip", "Suitcase")
        list = addSubtask(list, "Trip", "Tickets")
        assertFalse(isComplete(list.tasks.single()))

        list = completeTask(list, "Suitcase")
        list = completeTask(list, "Tickets")
        assertTrue(isComplete(list.tasks.single()))
    }

    @Test
    fun originalDataStaysUnchanged() {
        val original = addTask(TodoList(), "Original")
        completeTask(original, "Original")
        assertFalse(isComplete(original.tasks.single()))
    }
}
