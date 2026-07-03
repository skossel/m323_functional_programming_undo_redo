/**
 * Dieses File enthält Unit-Tests für die Historien-Verwaltung (History).
 * Geprüft wird insbesondere die korrekte Funktionalität von Undo- und Redo-Aktionen
 * sowie die Replay-Funktion zum Wiederholen von Edits.
 */
package todo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryTest {

    @Test
    fun applyEditMovesPresentIntoPast() {
        val h = applyCommand(History(), Add("A"))
        assertEquals(listOf(Task("A")), h.present.tasks)
        assertEquals(1, h.past.size)
        assertTrue(h.future.isEmpty())
    }

    @Test
    fun undoRestoresPreviousState() {
        var h = History()
        h = applyCommand(h, Add("A"))
        h = applyCommand(h, Add("B"))
        assertEquals(2, h.present.tasks.size)

        h = applyCommand(h, Undo)
        assertEquals(listOf(Task("A")), h.present.tasks)
    }

    @Test
    fun redoReappliesUndoneState() {
        var h = History()
        h = applyCommand(h, Add("A"))
        h = applyCommand(h, Undo)
        assertTrue(h.present.tasks.isEmpty())

        h = applyCommand(h, Redo)
        assertEquals(listOf(Task("A")), h.present.tasks)
    }

    @Test
    fun newEditClearsRedoFuture() {
        var h = History()
        h = applyCommand(h, Add("A"))
        h = applyCommand(h, Undo)
        h = applyCommand(h, Add("B"))
        assertTrue(h.future.isEmpty())
        assertEquals(listOf(Task("B")), h.present.tasks)
    }

    @Test
    fun undoOnEmptyHistoryIsNoOp() {
        val h = History()
        assertEquals(h, undo(h))
    }
}
