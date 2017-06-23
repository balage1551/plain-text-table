package hu.vissy.texttable.contentformatter;

public class RightCellAlignment extends CellAlignment {


    public RightCellAlignment() {
        super();
    }

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
