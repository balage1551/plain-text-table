package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.vissy.texttable.BorderFormatter.LineType;
import hu.vissy.texttable.column.ColumnDefinition;

public class TableFormatter<D> {


    public static class Builder<D> {
        private List<ColumnDefinition<D, ?, ?>> columns;
        private BorderFormatter borderFormatter = new BorderFormatter();
        private String heading = null;
        private boolean showAggregation = false;
        private boolean separateDataWithLines = false;

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

        public Builder<D> withSeparateDataWithLines(boolean separateDataWithLines) {
            this.separateDataWithLines = separateDataWithLines;
            return this;
        }

        public TableFormatter<D> build() {
            return new TableFormatter<>(this);
        }
    }

    private List<ColumnDefinition<D, ?, ?>> columns;
    private BorderFormatter borderFormatter;
    private String heading;
    private boolean showAggregation;
    private boolean separateDataWithLines;

    private TableFormatter(Builder<D> builder) {
        this.showAggregation = builder.showAggregation;
        this.columns = builder.columns;
        this.borderFormatter = builder.borderFormatter;
        this.heading = builder.heading;
        this.separateDataWithLines = builder.separateDataWithLines;
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
            sb.append(borderFormatter.drawLine(widths, LineType.TOP_EDGE, true));
            sb.append(borderFormatter.drawData(
                            Collections.singletonList(String.format("%1$-" + borderFormatter.calculateOneColumnWidth(widths) + "s", heading)),
                            LineType.HEADING));
            sb.append(borderFormatter.drawLine(widths, LineType.HEADING_LINE, false));
        } else {
            sb.append(borderFormatter.drawLine(widths, LineType.TOP_EDGE, false));
        }

        sb.append(borderFormatter.drawData(columns.stream()
                        .map(cd -> cd.getCellContentFormatter().formatCell(cd.getTitle(), widths.get(cd.getIndex())))
                        .collect(Collectors.toList()), LineType.HEADER));
        sb.append(borderFormatter.drawLine(widths, LineType.HEADER_LINE, false));

        boolean prevSep = true;
        for (TableRow tr : td.getRows()) {
            if (td.isSeparator(tr)) {
                sb.append(borderFormatter.drawLine(widths, LineType.SEPARATOR_LINE, false));
                prevSep = true;
            } else {
                if (separateDataWithLines && !prevSep) {
                    sb.append(borderFormatter.drawLine(widths, LineType.INTERNAL_LINE, false));
                }
                sb.append(borderFormatter.drawData(columns.stream()
                                .map(cd -> cd.getCellContentFormatter().formatCell(tr.getValue(cd.getIndex()), widths.get(cd.getIndex())))
                                .collect(Collectors.toList()), LineType.DATA));
                prevSep = false;
            }
        }
        if (showAggregation) {
            sb.append(borderFormatter.drawLine(widths, LineType.AGGREGATE_LINE, false));
            sb.append(borderFormatter.drawData(
                            columns.stream().map(cd -> cd.getCellContentFormatter().formatCell(
                                            td.getAggregateRow().getValue(cd.getIndex()),
                                            widths.get(cd.getIndex())))
                            .collect(Collectors.toList()),
                            LineType.AGGREGATE));

        }
        sb.append(borderFormatter.drawLine(widths, LineType.BOTTOM_EDGE, false));

        System.out.println(sb);
        return null;
    }

}
