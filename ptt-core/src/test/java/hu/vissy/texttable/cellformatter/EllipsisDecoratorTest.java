package hu.vissy.texttable.cellformatter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import hu.vissy.texttable.contentformatter.EllipsisDecorator;
import hu.vissy.texttable.contentformatter.EllipsisDecorator.TextSegment;

public class EllipsisDecoratorTest {

    @Test
    public void whenUsingBuilderWithoutParams_theDefaultsAreSet() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder().build();
        assertEquals(ed.getKeptPart(), TextSegment.START);
        assertEquals(ed.getEllipsisSign(), "...");
        assertFalse(ed.isTrimToWord());
    }

    @Test
    public void whenUsingBuilderWithParams_theyAreSet() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .withEllipsisSign("---")
                .withKeptPart(TextSegment.CENTER)
                .withTrimToWord(true)
                .build();
        assertEquals(ed.getKeptPart(), TextSegment.CENTER);
        assertEquals(ed.getEllipsisSign(), "---");
        assertTrue(ed.isTrimToWord());
    }

    @Test
    public void whenTextIsNotOverflowing_noDecorationDone() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .build();
        assertEquals(ed.decorate("abc", 8), "abc");
        assertEquals(ed.decorate("abcdefgh", 8), "abcdefgh");
    }


    @Test
    public void whenSettingKeptPart_theyAreKeptAsDefined() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .withKeptPart(TextSegment.START)
                .build();
        assertEquals(ed.decorate("abcdefghij", 8), "abcde...");
        ed = new EllipsisDecorator.Builder()
                .withKeptPart(TextSegment.END)
                .build();
        assertEquals(ed.decorate("abcdefghij", 8), "...fghij");
        ed = new EllipsisDecorator.Builder()
                .withKeptPart(TextSegment.CENTER)
                .build();
        assertEquals(ed.decorate("abcdefghij", 8), "...ef...");
    }


    @Test
    public void whenSettingEllipsis_itIsUsed() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .withEllipsisSign("==")
                .build();
        assertEquals(ed.decorate("abcdefghij", 8), "abcdef==");
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void whenSettingEllipsisToNull_exceptionIsTrown() {
        new EllipsisDecorator.Builder()
                .withEllipsisSign(null)
                .build();
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void whenSettingEllipsisToEmpty_exceptionIsTrown() {
        new EllipsisDecorator.Builder()
                .withEllipsisSign("")
                .build();
    }

    @Test
    public void whenSettingEllipsisLongerThanWidth_itIsHandledCorrectly() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .withEllipsisSign("==========")
                .build();
        assertEquals(ed.decorate("abcdefghij", 8), "========");
    }

    @Test
    public void whenSettingEllipsisToFillAllSpace_itReallyDoes() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .withEllipsisSign("====")
                .withKeptPart(TextSegment.CENTER)
                .build();
        assertEquals(ed.decorate("abcdefghij", 8), "========");
    }

    @Test
    public void whenTrimmingToWord_itTrims() {
        EllipsisDecorator ed = new EllipsisDecorator.Builder()
                .withTrimToWord(true)
                .build();
        assertEquals(ed.decorate("ab cd ef gh", 8), "ab cd...");
        assertEquals(ed.decorate("ab cdefg hi", 8), "ab...");

        ed = new EllipsisDecorator.Builder()
                .withTrimToWord(true)
                .withKeptPart(TextSegment.END)
                .build();
        assertEquals(ed.decorate("ab cd ef gh", 8), "...ef gh");
        assertEquals(ed.decorate("ab cdefg hi", 8), "...hi");
    }

}
