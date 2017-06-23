package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.vissy.texttable.BorderFormatter.LineType;
import hu.vissy.texttable.column.ColumnDefinition;

public class TableFormatter<D> {


    private List<ColumnDefinition<D, ?, ?>> columns;
    private BorderFormatter borderFormatter;
    private String heading;
    private boolean showAggregation;


    public static class Builder<D> {
        private List<ColumnDefinition<D, ?, ?>> columns;
        private BorderFormatter borderFormatter = new BorderFormatter();
        private String heading = null;
        private boolean showAggregation = false;

        public Builder() {
            this.columns = new ArrayList<>();

        }

        public Builder<D> withColumn(ColumnDefinition<D, ?, ?> column) {
            column.setIndex(this.columns.size());
            this.columns.add(column);
            return this;
        }

        public Builder<D> withBorderFormatter(BorderFormatter borderFormatter) {
            this.borderFormatter = borderFormatter;
            return this;
        }

        public Builder<D> withHeading(String heading) {
            this.heading = heading;
            return this;
        }

        public Builder<D> withShowAggregation(boolean showAggregation) {
            this.showAggregation = showAggregation;
            return this;
        }

        public TableFormatter<D> build() {
            return new TableFormatter<>(this);
        }
    }

    private TableFormatter(Builder<D> builder) {
        this.showAggregation = builder.showAggregation;
        this.columns = builder.columns;
        this.borderFormatter = builder.borderFormatter;
        this.heading = builder.heading;
    }


    public String apply(List<D> data) {
        TableData<D> td = new TableData<>(columns, data, showAggregation);
        List<Integer> widths = td.getColumnWidths();
        for (int i = 0; i < columns.size(); i++) {
            widths.set(i, columns.get(i).getCellContentFormatter().boundWidth(widths.get(i)));
        }
        System.out.println(widths);

        StringBuilder sb = new StringBuilder();
        if (heading != null) {
            sb.append(borderFormatter.drawLine(widths, LineType.EDGE, true));
            sb.append(borderFormatter.drawData(
                    Collections.singletonList(String.format("%1$-" + borderFormatter.calculateOneColumnWidth(widths) + "s", heading)),
                    LineType.HEADING));
            sb.append(borderFormatter.drawLine(widths, LineType.HEADING_LINE, false));
        } else {
            sb.append(borderFormatter.drawLine(widths, LineType.EDGE, false));
        }

        sb.append(borderFormatter.drawData(columns.stream()
                .map(cd -> cd.getCellContentFormatter().formatCell(cd.getTitle(), widths.get(cd.getIndex())))
                .collect(Collectors.toList()), LineType.HEADER));
        sb.append(borderFormatter.drawLine(widths, LineType.HEADER_LINE, false));
        td.getRows().forEach(tr -> {
            if (td.isSeparator(tr)) {
                sb.append(borderFormatter.drawLine(widths, LineType.INTERNAL_LINE, false));
            } else {
                sb.append(borderFormatter.drawData(columns.stream()
                        .map(cd -> cd.getCellContentFormatter().formatCell(tr.getValue(cd.getIndex()), widths.get(cd.getIndex())))
                        .collect(Collectors.toList()), LineType.DATA));
            }
        });
        sb.append(borderFormatter.drawLine(widths, LineType.EDGE, false));

        System.out.println(sb);
        return null;
    }

}
