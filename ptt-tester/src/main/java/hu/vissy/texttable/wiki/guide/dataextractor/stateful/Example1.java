package hu.vissy.texttable.wiki.guide.dataextractor.stateful;

import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.TableFormatter.Builder;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;

public class Example1 extends ExampleBase {

    protected static class StateCounter {
        public int count;
    }

//    protected class StatePrevPos {
//        public int prevPos;
//    }

    public static void main(String[] args) {
        Builder<BusinessObject> builder = new TableFormatter.Builder<>();

        addDefaultColumns(builder);

        builder.withColumn(new ColumnDefinition.StatefulBuilder<BusinessObject, StateCounter, Integer>()
                .withTitle("cratesOnBoard")
                .withDataExtractor(new StatefulDataExtractor<>((r, s) -> {
                    s.count += r.getCrates();
                    return s.count;
                }, StateCounter::new, (k, s) -> null))
                .build());

        TableFormatter<BusinessObject> formatter = builder.build();

        List<BusinessObject> data = createData();

        System.out.println(formatter.apply(data));
    }

}
