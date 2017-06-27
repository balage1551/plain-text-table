package hu.vissy.texttable.dataconverter;

import java.time.Duration;

/**
 * A very simple duration formatter.
 * <p>
 * This implementation formats the duration in a hours:minutes:seconds format.
 * </p>
 * 
 * @author Balage
 *
 */
public class SimpleDurationDataConverter implements DataConverter<Duration> {

    public SimpleDurationDataConverter() {
    }


    @Override
    public String convert(Duration data) {
        if (data == null) {
            return null;
        }

        long s = data.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (int) ((s % 3600) / 60), (int) (s % 60));
    }

}
