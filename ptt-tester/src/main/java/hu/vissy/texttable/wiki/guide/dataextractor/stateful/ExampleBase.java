package hu.vissy.texttable.wiki.guide.dataextractor.stateful;

import java.util.ArrayList;
import java.util.List;

import hu.vissy.texttable.TableFormatter.Builder;
import hu.vissy.texttable.column.ColumnDefinition;

public class ExampleBase {

    protected static List<BusinessObject> createData() {
        List<BusinessObject> res = new ArrayList<>();
        res.add(new BusinessObject(5, 2));
        res.add(new BusinessObject(-3, 7));
        res.add(new BusinessObject(2, 3));
        res.add(new BusinessObject(-4, 2));
        return res;
    }

    protected static void addDefaultColumns(Builder<BusinessObject> builder) {
        builder.withColumn(ColumnDefinition.<BusinessObject, Integer> createSimpleStateless("Crates", (d) -> d.getCrates()));
        builder.withColumn(ColumnDefinition.<BusinessObject, Integer> createSimpleStateless("Pos", (d) -> d.getPosition()));
    }

}
