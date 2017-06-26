package hu.vissy.texttable.dataconverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateDataConverter implements DataConverter<LocalDate> {

    DateTimeFormatter formatter;

    public DateDataConverter() {
        this.formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }


    public DateDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public String convert(LocalDate data) {
        if (data == null)
            return null;

        return formatter.format(data);
    }

}
