package hu.vissy.texttable.contentformatter;

/**
 * Base class for cell alignment.
 *
 * @author balage
 *
 */
public abstract class CellAlignment {

    private char paddingCharacter = ' ';

    /**
     * Constructor with space as padding character.
     */
    protected CellAlignment() {
    }

    /**
     * Constructor with custom padding character.
     *
     * @param paddingCharacter
     *            The character to used for padding.
     *
     */
    protected CellAlignment(char paddingCharacter) {
        this.paddingCharacter = paddingCharacter;
    }

    /**
     * Aligns the data to the given width. If the data is longer than the
     * specified width, there will be no change made, the data is returned.
     *
     * @param data
     *            The data to align.
     * @param width
     *            The width to align to.
     * @return The aligned (padded) string.
     */
    public abstract String align(String data, int width);

    /**
     * @return The character used for padding.
     */
    public char getPaddingCharacter() {
        return paddingCharacter;
    }

    /**
     * Creates a string containing <code>size</code> padding character.
     * 
     * @param size
     *            The number of characters to concatenate.
     * @return A string containing <code>size</code> padding character.
     */
    protected String createPad(int size) {
        return new String(new char[size]).replace('\0', paddingCharacter);
    }

}