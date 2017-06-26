package hu.vissy.texttable.dataconverter;

public class BooleanDataConverter implements DataConverter<Boolean> {

    private String trueValue = "" + true;
    private String falseValue = "" + false;

    public BooleanDataConverter() {
    }

    public BooleanDataConverter(String trueValue, String falseValue) {
        super();
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }


    @Override
    public String convert(Boolean data) {
        if (data == null) {
            return null;
        }

        return data ? trueValue : falseValue;
    }

}
