package hu.vissy.texttable.contentformatter;

/**
 * An alignment which will add padding characters to the left to (in front of)
 * the value if necessary.
 *
 * @author Balage
 *
 */
public class RightCellAlignment extends CellAlignment {


    /**
     * Constructor with space as padding character.
     */
    public RightCellAlignment() {
        super();
    }

    /**
     * Constructor with custom padding character.
     *
     * @param paddingCharacter
     *            The character to used for padding.
     *
     */
    public RightCellAlignment(char paddingCharacter) {
        super(paddingCharacter);
    }

    @Override
    public String align(String data, int width) {
        if (data.length() > width) {
            return data;
        }
        int diff = width - data.length();
        return createPad(diff) + data;
    }


}
