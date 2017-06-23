package hu.vissy.texttable.contentformatter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import hu.vissy.texttable.contentformatter.CellContentFormatter;
import hu.vissy.texttable.contentformatter.CenterCellAlignment;
import hu.vissy.texttable.contentformatter.EllipsisDecorator;

public class CellContentFormatterTest {

    @Test
    public void whenNothingIsSpecified_defaultsAreUsed() {
        CellContentFormatter ctf = new CellContentFormatter.Builder().build();

        assertEquals(ctf.formatCell(null, 8), "        ");
        assertEquals(ctf.formatCell("abcd", 8), "abcd    ");
        assertEquals(ctf.formatCell("abcdefghij", 8), "abcde...");
    }

    @Test
    public void whenNullValueSpecified_itIsUsed() {
        CellContentFormatter ctf = new CellContentFormatter.Builder()
                .withNullValue("(null)")
                .build();

        assertEquals(ctf.formatCell(null, 8), "(null)  ");
    }

    @Test
    public void whenEllipsisDecoratorSpecified_itIsUsed() {
        CellContentFormatter ctf = new CellContentFormatter.Builder()
                .withEllipsesDecorator(new EllipsisDecorator.Builder().withEllipsisSign("->").build())
                .build();

        assertEquals(ctf.formatCell("abcdefghij", 8), "abcdef->");
    }

    @Test
    public void whenCellAlignmentSpecified_itIsUsed() {
        CellContentFormatter ctf = new CellContentFormatter.Builder()
                .withCellAlignment(new CenterCellAlignment('-'))
                .build();

        assertEquals(ctf.formatCell("abcd", 8), "--abcd--");
    }

    @Test
    public void whenUsingStaticFactoryMethods_itReturnsCorrectImplementation() {
        CellContentFormatter ctf = CellContentFormatter.leftAlignedCell();
        assertEquals(ctf.formatCell("abcd", 8), "abcd    ");
        ctf = CellContentFormatter.rightAlignedCell();
        assertEquals(ctf.formatCell("abcd", 8), "    abcd");
        ctf = CellContentFormatter.centeredCell();
        assertEquals(ctf.formatCell("abcd", 8), "  abcd  ");
    }


}
