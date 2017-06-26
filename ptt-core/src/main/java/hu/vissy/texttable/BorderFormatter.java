package hu.vissy.texttable;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

public class BorderFormatter {

    private static class LineSpec {
        private char leftEdge;
        private char internal;
        private char body;
        private char rightEdge;
        private char padding;

        public LineSpec(char leftEdge, char internal, char padding, char body, char rightEdge) {
            super();
            this.leftEdge = leftEdge;
            this.internal = internal;
            this.body = body;
            this.rightEdge = rightEdge;
            this.padding = padding;
        }

        public LineSpec(char leftEdge, char internal, char body, char rightEdge) {
            this(leftEdge, internal, body, body, rightEdge);
        }

        public LineSpec(char edge, char internal, char body) {
            this(edge, internal, body, body, edge);
        }

        public LineSpec(char edge, char body) {
            this(edge, edge, body, body, edge);
        }

        public char getLeftEdge() {
            return leftEdge;
        }
        public char getInternal() {
            return internal;
        }
        public char getBody() {
            return body;
        }

        public char getRightEdge() {
            return rightEdge;
        }
        public char getPadding() {
            return padding;
        }

    }

    public enum LineType {
        TOP_EDGE, BOTTOM_EDGE, HEADING, HEADING_LINE, HEADER, HEADER_LINE, DATA, INTERNAL_LINE, SEPARATOR_LINE, AGGREGATE_LINE, AGGREGATE,;
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
    // {
    // lineSpecifications.put(LineType.EDGE, new LineSpec('#', '#', '=', '='));
    // lineSpecifications.put(LineType.HEADING, new LineSpec('#', '|', '?', '
    // '));
    // lineSpecifications.put(LineType.HEADING_LINE, new LineSpec('#', '+', '=',
    // '='));
    // lineSpecifications.put(LineType.HEADER, new LineSpec('#', '|', '?', '
    // '));
    // lineSpecifications.put(LineType.HEADER_LINE, new LineSpec('#', '+', '=',
    // '='));
    // lineSpecifications.put(LineType.DATA, new LineSpec('#', '|', '?', ' '));
    // lineSpecifications.put(LineType.INTERNAL_LINE, new LineSpec('#', '+',
    // '-', '-'));
    // }

    {
        lineSpecifications.put(LineType.TOP_EDGE, new LineSpec('╔', '╤', '═', '╗'));
        lineSpecifications.put(LineType.HEADING, new LineSpec('║', '│', ' '));
        lineSpecifications.put(LineType.HEADING_LINE, new LineSpec('╠', '╤', '═', '╣'));
        lineSpecifications.put(LineType.HEADER, new LineSpec('║', '│', ' '));
        lineSpecifications.put(LineType.HEADER_LINE, new LineSpec('╠', '╪', '═', '╣'));
        lineSpecifications.put(LineType.DATA, new LineSpec('║', '│', ' '));
        lineSpecifications.put(LineType.SEPARATOR_LINE, new LineSpec('╠', '╪', '═', '╣'));
        lineSpecifications.put(LineType.INTERNAL_LINE, new LineSpec('╟', '┼', '─', '╢'));
        lineSpecifications.put(LineType.AGGREGATE_LINE, new LineSpec('╠', '╪', '═', '╣'));
        lineSpecifications.put(LineType.AGGREGATE, new LineSpec('║', '│', ' '));
        lineSpecifications.put(LineType.BOTTOM_EDGE, new LineSpec('╚', '╧', '═', '╝'));
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
        if ((lineType == LineType.TOP_EDGE || lineType == LineType.BOTTOM_EDGE)
                        && !drawHorizontalEdge)
            return "";

        StringBuilder sb = new StringBuilder();
        return widths.stream().map(w -> {
            String l = repeate(spec.padding, leftPaddingWidth)
                            + repeate(spec.body, w)
                            + repeate(spec.padding, rightPaddingWidth);
            return l;
        }).collect(Collectors.joining(drawVerticalSeparator ? "" + (skipInternal ? spec.body : spec.internal) : "",
                        (drawVerticalEdge ? "" + spec.leftEdge : ""),
                        (drawVerticalEdge ? "" + spec.rightEdge : ""))) + "\n";
    }

    public String drawData(List<String> data, LineType lineType) {
        LineSpec spec = lineSpecifications.get(lineType);

        StringBuilder sb = new StringBuilder();
        return data.stream().map(w -> {
            String l = repeate(spec.padding, leftPaddingWidth)
                            + w
                            + repeate(spec.padding, rightPaddingWidth);
            return l;
        }).collect(Collectors.joining(drawVerticalSeparator ? "" + spec.internal : "",
                        (drawVerticalEdge ? "" + spec.leftEdge : ""),
                        (drawVerticalEdge ? "" + spec.rightEdge : ""))) + "\n";

    }

}

