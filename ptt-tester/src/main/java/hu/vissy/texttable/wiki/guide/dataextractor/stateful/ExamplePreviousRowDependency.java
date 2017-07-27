package hu.vissy.texttable.wiki.guide.dataextractor.stateful;

import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.TableFormatter.Builder;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;

public class ExamplePreviousRowDependency extends ExampleBase {

    protected static class StatePrev {
        public int prev;

        public StatePrev(int prev) {
            super();
            this.prev = prev;
        }
    }

    public static void main(String[] args) {
        Builder<BusinessObject> builder = new TableFormatter.Builder<>();

        addDefaultColumns(builder);

        builder.withColumn(new ColumnDefinition.StatefulBuilder<BusinessObject, StatePrev, Integer>()
                .withTitle("distance")
                .withDataExtractor(new StatefulDataExtractor<>((r, s) -> {
                    int val = Math.abs(s.prev - r.getPosition());
                    s.prev = r.getPosition();
                    return val;
                }, () -> new StatePrev(5), (k, s) -> null))
                .build());

        TableFormatter<BusinessObject> formatter = builder.build();

        List<BusinessObject> data = createData();

        System.out.println(formatter.apply(data));
    }

}
