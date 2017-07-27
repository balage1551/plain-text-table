package hu.vissy.texttable.wiki.guide.dataextractor.stateful;

import java.util.List;

import hu.vissy.texttable.TableFormatter;
import hu.vissy.texttable.TableFormatter.Builder;
import hu.vissy.texttable.column.ColumnDefinition;
import hu.vissy.texttable.dataextractor.StatefulDataExtractor;

public class ExampleAggregation extends ExampleBase {

    protected static class StateCounter {
        public int count;
    }

//    protected class StatePrevPos {
//        public int prevPos;
//    }

    public static void main(String[] args) {
        Builder<BusinessObject> builder = new TableFormatter.Builder<>();

        addDefaultColumns(builder);


        builder.withShowAggregation(true)
                .withColumn(new ColumnDefinition.StatefulBuilder<BusinessObject, StateCounter, Integer>()
                        .withTitle("creates moved")
                        .withDataExtractor(new StatefulDataExtractor<>((r, s) -> {
                            int val = Math.abs(r.getCrates());
                            s.count += val;
                            return val;
                        }, StateCounter::new, (k, s) -> s.count))
                        .build());

        TableFormatter<BusinessObject> formatter = builder.build();

        List<BusinessObject> data = createData();

        System.out.println(formatter.apply(data));
    }

}
