package hu.vissy.texttable.cellformatter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import hu.vissy.texttable.contentformatter.LeftCellAlignment;

public class LeftCellAlignmentTest {

    @Test
    public void whenShorter_padsCorrectly() {
        LeftCellAlignment align = new LeftCellAlignment();
        assertEquals(align.align("alma", 6), "alma  ");
        assertEquals(align.align("alma", 5), "alma ");
    }

    @Test
    public void whenLongerOrEqualLength_doesNothing() {
        LeftCellAlignment align = new LeftCellAlignment();
        assertEquals(align.align("alma", 3), "alma");
    }

    @Test
    public void whenSpecifyingCharacter_padsWithIt() {
        LeftCellAlignment align = new LeftCellAlignment('-');
        assertEquals(align.align("alma", 6), "alma--");
        assertEquals(align.align("alma", 5), "alma-");
    }

}
