package hu.vissy.texttable.contentformatter;

/**
 * An alignment which will add padding characters both to the left to (in front
 * of) and to the right to (behind) the value if necessary. The padding
 * distributed evenly, with at most one more character added to the right when
 * the padding size is odd.
 *
 * @author Balage
 *
 */
public class CenterCellAlignment extends CellAlignment {


    /**
     * Constructor with space as padding character.
     */
    public CenterCellAlignment() {
        super();
    }

    /**
     * Constructor with custom padding character.
     *
     * @param paddingCharacter
     *            The character to used for padding.
     *
     */
    public CenterCellAlignment(char paddingCharacter) {
        super(paddingCharacter);
    }

    @Override
    public String align(String data, int width) {
        if (data.length() > width) {
            return data;
        }
        int leftPad = (width - data.length()) / 2;
        int rightPad = width - data.length() - leftPad;
        return createPad(leftPad) + data + createPad(rightPad);
    }


}
