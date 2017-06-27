package hu.vissy.texttable.contentformatter;

/**
 * An alignment which will add padding characters to the right of the value if
 * necessary.
 *
 * @author Balage
 *
 */
public class LeftCellAlignment extends CellAlignment {


    /**
     * Constructor with space as padding character.
     */
    public LeftCellAlignment() {
        super();
    }

    /**
     * Constructor with custom padding character.
     * 
     * @param paddingCharacter
     *            The character to used for padding.
     * 
     */
    public LeftCellAlignment(char paddingCharacter) {
        super(paddingCharacter);
    }

    @Override
    public String align(String data, int width) {
        if (data.length() > width) {
            return data;
        }
        int diff = width - data.length();
        return data + createPad(diff);
    }


}
