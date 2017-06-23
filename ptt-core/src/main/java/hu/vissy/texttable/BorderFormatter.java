package hu.vissy.texttable;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class BorderFormatter {

    private static class LineSpec {
        private char edge;
        private char internal;
        private char body;
        private char padding;

        public LineSpec(char edge, char internal, char body, char padding) {
            super();
            this.edge = edge;
            this.internal = internal;
            this.body = body;
            this.padding = padding;
        }

        public char getEdge() {
            return edge;
        }

        public char getInternal() {
            return internal;
        }

        public char getBody() {
            return body;
        }

        public char getPadding() {
            return padding;
        }


    }

    public enum LineType {
        EDGE(false),
        HEADING(true),
        HEADING_LINE(false),
        HEADER(true),
        HEADER_LINE(false),
        DATA(true),
        INTERNAL_LINE(false);

        private boolean hasData;

        private LineType(boolean hasData) {
            this.hasData = hasData;
        }

        public boolean hasData() {
            return hasData;
        }
    }

    EnumMap<LineType, LineSpec> lineSpecifications = new EnumMap<>(LineType.class);

//    {
//        lineSpecifications.put(LineType.EDGE, new LineSpec('&', '%', '=', '÷'));
//        lineSpecifications.put(LineType.HEADING, new LineSpec('/', '?', '?', '.'));
//        lineSpecifications.put(LineType.HEADING_LINE, new LineSpec('/', '#', '=', '¤'));
//        lineSpecifications.put(LineType.HEADER, new LineSpec('(', '!', '?', '_'));
//        lineSpecifications.put(LineType.HEADER_LINE, new LineSpec(')', '*', '~', ':'));
//        lineSpecifications.put(LineType.DATA, new LineSpec('$', '|', '?', '°'));
//        lineSpecifications.put(LineType.INTERNAL_LINE, new LineSpec('|', '+', '-', '÷'));
//    }
    {
        lineSpecifications.put(LineType.EDGE, new LineSpec('#', '#', '=', '='));
        lineSpecifications.put(LineType.HEADING, new LineSpec('#', '|', '?', ' '));
        lineSpecifications.put(LineType.HEADING_LINE, new LineSpec('#', '+', '=', '='));
        lineSpecifications.put(LineType.HEADER, new LineSpec('#', '|', '?', ' '));
        lineSpecifications.put(LineType.HEADER_LINE, new LineSpec('#', '+', '=', '='));
        lineSpecifications.put(LineType.DATA, new LineSpec('#', '|', '?', ' '));
        lineSpecifications.put(LineType.INTERNAL_LINE, new LineSpec('|', '+', '-', '-'));
    }



    private int leftPaddingWidth = 1;
    private int rightPaddingWidth = 1;
    private boolean drawHorizontalEdge = true;
    private boolean drawVerticalEdge = true;
    private boolean drawVerticalSeparator = true;

    public String repeate(char c, int w) {
        return new String(new byte[w]).replace('\0', c);
    }

    public int calculateOneColumnWidth(List<Integer> widths) {
        return widths.stream().mapToInt(i -> i).sum() +
                (widths.size() - 1) * (leftPaddingWidth + rightPaddingWidth) +
                (drawVerticalSeparator ? widths.size() - 1 : 0);
    }

    public String drawLine(List<Integer> widths, LineType lineType, boolean skipInternal) {
        LineSpec spec = lineSpecifications.get(lineType);
        if (lineType == LineType.EDGE && !drawHorizontalEdge) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(spec.getEdge());
        return widths.stream().map(w -> {
            String l = repeate(spec.padding, leftPaddingWidth)
                    + repeate(spec.body, w)
                    + repeate(spec.padding, rightPaddingWidth);
            return l;
        }).collect(Collectors.joining(drawVerticalSeparator ? "" + (skipInternal ? spec.body : spec.internal) : "",
                (drawVerticalEdge ? "" + spec.edge : ""), (drawVerticalEdge ? "" + spec.edge : ""))) + "\n";
    }

    public String drawData(List<String> data, LineType lineType) {
        LineSpec spec = lineSpecifications.get(lineType);

        StringBuilder sb = new StringBuilder();
        sb.append(spec.getEdge());
        return data.stream().map(w -> {
            String l = repeate(spec.padding, leftPaddingWidth)
                    + w
                    + repeate(spec.padding, rightPaddingWidth);
            return l;
        }).collect(Collectors.joining(drawVerticalSeparator ? "" + spec.internal : "",
                (drawVerticalEdge ? "" + spec.edge : ""), (drawVerticalEdge ? "" + spec.edge : ""))) + "\n";

    }

}

