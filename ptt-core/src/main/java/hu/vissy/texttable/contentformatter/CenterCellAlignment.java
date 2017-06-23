package hu.vissy.texttable.contentformatter;

public class CenterCellAlignment extends CellAlignment {


    public CenterCellAlignment() {
        super();
    }

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
