package hu.vissy.texttable.dataconverter;

/**
 * A boolean data converter.
 *
 * @author Balage
 *
 */
public class BooleanDataConverter extends TypedDataConverter<Boolean> {

    private String trueValue = "" + true;
    private String falseValue = "" + false;

    /**
     * Constructor for default behaviour: will convert boolean value to their
     * names.
     */
    public BooleanDataConverter() {
        super(Boolean.class);
    }

    /**
     * Constructor to specify custom strings for false and true values.
     *
     * @param trueValue
     *            The string to used for true values.
     * @param falseValue
     *            The string to used for false values.
     */
    public BooleanDataConverter(String trueValue, String falseValue) {
        super(Boolean.class);
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

    /**
     * @return The string to used for true values.
     */
    public String getTrueValue() {
        return trueValue;
    }

    /**
     * @return The string to used for false values.
     */
    public String getFalseValue() {
        return falseValue;
    }


}
