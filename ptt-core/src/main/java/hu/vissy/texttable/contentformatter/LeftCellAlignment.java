package hu.vissy.texttable.contentformatter;

public class LeftCellAlignment extends CellAlignment {


    public LeftCellAlignment() {
        super();
    }

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
