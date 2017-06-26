package hu.vissy.texttable.dataconverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeDataConverter implements DataConverter<LocalTime> {

    DateTimeFormatter formatter;

    public TimeDataConverter() {
        this.formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    }


    public TimeDataConverter(DateTimeFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public String convert(LocalTime data) {
        if (data == null)
            return null;

        return formatter.format(data);
    }

}
