/**
 * Dieses File enthält Unit-Tests für den Parser.
 * Es wird verifiziert, ob Benutzereingaben in Textform korrekt in die
 * entsprechenden Command-Objekte übersetzt werden.
 */
package todo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParserTest {

    @Test
    fun parsesAdd() {
        assertEquals(Add("Shopping"), parse("add Shopping"))
    }

    @Test
    fun parsesAddWithSpacesInName() {
        assertEquals(Add("Pack suitcase"), parse("add Pack suitcase"))
    }

    @Test
    fun parsesAddSubtask() {
        assertEquals(AddSub("Trip", "Tickets"), parse("add Tickets to Trip"))
    }

    @Test
    fun parsesComplete() {
        assertEquals(Complete("Wash dishes"), parse("complete Wash dishes"))
    }

    @Test
    fun parsesUndoAndRedo() {
        assertEquals(Undo, parse("undo"))
        assertEquals(Redo, parse("redo"))
    }

    @Test
    fun trimsSurroundingWhitespace() {
        assertEquals(Add("X"), parse("   add X   "))
    }

    @Test
    fun returnsNullForUnknownInput() {
        assertNull(parse("foobar"))
        assertNull(parse("add"))
        assertNull(parse(""))
    }
}
