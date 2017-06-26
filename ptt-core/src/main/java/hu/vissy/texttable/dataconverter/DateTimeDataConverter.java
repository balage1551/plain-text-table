package hu.vissy.texttable.dataconverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeDataConverter implements DataConverter<LocalDateTime> {

    DateTimeFormatter formatter;

    public DateTimeDataConverter() {
        this.formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }


    public DateTimeDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }



    @Override
    public String convert(LocalDateTime data) {
        if (data == null)
            return null;

        return formatter.format(data);
    }

}
