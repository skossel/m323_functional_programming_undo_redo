/**
 * Dieses File enthält Unit-Tests für die Render-Logik.
 * Es wird geprüft, ob die Todo-Liste korrekt in eine String-Repräsentation
 * für die Konsole umgewandelt wird, inklusive Einrückungen für Unteraufgaben.
 */
package todo

import kotlin.test.Test
import kotlin.test.assertEquals

class RenderTest {

    @Test
    fun rendersEmptyList() {
        assertEquals("(no tasks yet)", render(TodoList()))
    }

    @Test
    fun rendersOpenAndDoneTasks() {
        var list = addTask(TodoList(), "A")
        list = addTask(list, "B")
        list = completeTask(list, "A")
        assertEquals("[x] A\n[ ] B", render(list))
    }

    @Test
    fun indentsSubtasks() {
        var list = addTask(TodoList(), "Trip")
        list = addSubtask(list, "Trip", "Tickets")
        assertEquals("[ ] Trip\n  [ ] Tickets", render(list))
    }

    @Test
    fun parentShowsDoneWhenSubtasksDone() {
        var list = addTask(TodoList(), "Trip")
        list = addSubtask(list, "Trip", "Tickets")
        list = completeTask(list, "Tickets")
        assertEquals("[x] Trip\n  [x] Tickets", render(list))
    }

    @Test
    fun rendersProgressSummary() {
        var list = addTask(TodoList(), "A")
        list = addTask(list, "B")
        list = completeTask(list, "A")
        assertEquals("Progress: 1/2 completed", renderProgress(list))
    }
}
