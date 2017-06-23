package hu.vissy.texttable.cellformatter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import hu.vissy.texttable.contentformatter.RightCellAlignment;

public class RightCellAlignmentTest {

    @Test
    public void whenShorter_padsCorrectly() {
        RightCellAlignment align = new RightCellAlignment();
        assertEquals(align.align("alma", 6), "  alma");
        assertEquals(align.align("alma", 5), " alma");
    }

    @Test
    public void whenLongerOrEqualLength_doesNothing() {
        RightCellAlignment align = new RightCellAlignment();
        assertEquals(align.align("alma", 3), "alma");
    }

    @Test
    public void whenSpecifyingCharacter_padsWithIt() {
        RightCellAlignment align = new RightCellAlignment('-');
        assertEquals(align.align("alma", 6), "--alma");
        assertEquals(align.align("alma", 5), "-alma");
    }

}
