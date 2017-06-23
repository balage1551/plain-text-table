package hu.vissy.texttable.cellformatter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import hu.vissy.texttable.contentformatter.CenterCellAlignment;

public class CenterCellAlignmentTest {

    @Test
    public void whenShorter_padsCorrectly() {
        CenterCellAlignment align = new CenterCellAlignment();
        assertEquals(align.align("alma", 6), " alma ");
        assertEquals(align.align("alma", 7), " alma  ");
    }

    @Test
    public void whenLongerOrEqualLength_doesNothing() {
        CenterCellAlignment align = new CenterCellAlignment();
        assertEquals(align.align("alma", 3), "alma");
    }

    @Test
    public void whenSpecifyingCharacter_padsWithIt() {
        CenterCellAlignment align = new CenterCellAlignment('-');
        assertEquals(align.align("alma", 6), "-alma-");
        assertEquals(align.align("alma", 7), "-alma--");
    }

}
