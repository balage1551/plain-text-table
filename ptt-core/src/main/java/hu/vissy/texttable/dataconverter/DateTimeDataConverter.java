package hu.vissy.texttable.dataconverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@linkplain LocalDateTime} objects.
 *
 * @author Balage
 *
 */
public class DateTimeDataConverter implements DataConverter<LocalDateTime> {

    DateTimeFormatter formatter;

    /**
     * Constructor for default behaviour. It uses the
     * {@linkplain DateTimeFormatter#ISO_LOCAL_DATE_TIME} format.
     */
    public DateTimeDataConverter() {
        formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }


    /**
     * Constructor for custom date and time format.
     *
     * @param formatter
     *            The date/time formatter to use.
     */
    public DateTimeDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }



    @Override
    public String convert(LocalDateTime data) {
        if (data == null) {
            return null;
        }

        return formatter.format(data);
    }

}
