package hu.vissy.texttable.contentformatter;

/**
 * Alignment of the column.
 * <p>
 * Longer values will be truncated, shorter values will be padded by spaces.
 * </p>
 *
 * @author balage
 *
 */
public abstract class CellAlignment {

    private char paddingCharacter = ' ';

    public CellAlignment() {
    }

    public CellAlignment(char paddingCharacter) {
        this.paddingCharacter = paddingCharacter;
    }

    public abstract String align(String data, int width);

    public char getPaddingCharacter() {
        return paddingCharacter;
    }

    protected String createPad(int size) {
        return new String(new char[size]).replace('\0', paddingCharacter);
    }

}