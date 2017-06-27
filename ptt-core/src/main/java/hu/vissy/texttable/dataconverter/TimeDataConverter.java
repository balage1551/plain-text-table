package hu.vissy.texttable.dataconverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@linkplain LocalTime} objects.
 *
 * @author Balage
 *
 */
public class TimeDataConverter implements DataConverter<LocalTime> {

    DateTimeFormatter formatter;

    /**
     * Constructor for default behaviour. It uses the
     * {@linkplain DateTimeFormatter#ISO_LOCAL_TIME} format.
     */
    public TimeDataConverter() {
        formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    }


    /**
     * Constructor for custom time format.
     *
     * @param formatter
     *            The time formatter to use.
     */
    public TimeDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public String convert(LocalTime data) {
        if (data == null) {
            return null;
        }

        return formatter.format(data);
    }

}
