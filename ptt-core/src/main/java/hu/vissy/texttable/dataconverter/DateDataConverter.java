package hu.vissy.texttable.dataconverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@linkplain LocalDate} objects.
 *
 * @author Balage
 *
 */
public class DateDataConverter implements DataConverter<LocalDate> {

    DateTimeFormatter formatter;

    /**
     * Constructor for default behaviour. It uses the
     * {@linkplain DateTimeFormatter#ISO_LOCAL_DATE} format.
     */
    public DateDataConverter() {
        formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }


    /**
     * Constructor for custom date format.
     *
     * @param formatter
     *            The date formatter to use.
     */
    public DateDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public String convert(LocalDate data) {
        if (data == null) {
            return null;
        }

        return formatter.format(data);
    }

}
