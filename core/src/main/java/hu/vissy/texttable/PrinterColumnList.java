package hu.vissy.texttable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.vissy.texttable.ConfigurableTablePrinter.TableRow;
import hu.vissy.texttable.DynamicTableDefinition.Builder;

/**
 * The list of the printer columns. This helps the user to construct, manage and
 * alter the column definitions. Also this function populates the
 * {@linkplain TableRow}.
 *
 * @author balage
 *
 * @param <D>
 *            The context the colums
 */
public class PrinterColumnList<D> {

    // The heading line
    private String heading = null;

    // The list of the columns
    private List<AbstractPrinterColumn<D, ?, ?, ?>> columns = new ArrayList<>();
    private List<Object> states = new ArrayList<>();
    private boolean printBaseline = false;

    /**
     * The constructor to create a table without heading.
     */
    public PrinterColumnList() {
        super();
    }

    /**
     * Constructor to create with heading text.
     *
     * @param heading
     *            The heading text.
     */
    public PrinterColumnList(String heading) {
        super();
        this.heading = heading;
    }


    /**
     * Adds a column to the column list.
     *
     * @param column
     *            The column to add.
     * @return The object itself (fluent api)
     */
    @SafeVarargs
    public final PrinterColumnList<D> addColumns(AbstractPrinterColumn<D, ?, ?, ?>... columns) {
        for (AbstractPrinterColumn<D, ?, ?, ?> c : columns) {
            addColumn(c);
        }
        return this;
    }


    /**
     * Adds a column to the column list.
     *
     * @param column
     *            The column to add.
     * @return The object itself (fluent api)
     */
    public PrinterColumnList<D> addColumn(AbstractPrinterColumn<D, ?, ?, ?> column) {
        if (findByTitle(column.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Name is duplicated: " + column.getTitle());
        } else {
            columns.add(column);
        }
        return this;
    }

    public boolean isPrintBaseline() {
        return printBaseline;
    }

    public PrinterColumnList<D> withPrintBaseline(boolean printBaseline) {
        this.printBaseline = printBaseline;
        return this;
    }

    /**
     * Removes a column.
     * <p>
     * Requires the exact column instance that was added- Use the
     * {@linkplain #findByClass(Class)} or {@linkplain #findByTitle(String)}
     * functions to get the instance.
     * </p>
     *
     * @param column
     *            the column to remove.
     * @return true if the column was found and removed
     */
    public boolean removeColumn(AbstractPrinterColumn<D, ?, ?, ?> column) {
        boolean res = columns.contains(column);
        if (res) {
            columns.remove(column);
        }
        return res;
    }

    /**
     * Builds the table definition from the column list and other parameters.
     *
     * @return the table definition
     */
    public DynamicTableDefinition getTableDefinition() {
        Builder defBuilder = new DynamicTableDefinition.Builder();
        columns.forEach(c -> defBuilder.addColumn(c.getColumnDefinition()));
        defBuilder.withHeading(heading);
        defBuilder.withPrintBaseline(printBaseline);
        return defBuilder.build();
    }

    void initializeStates() {
        states.clear();
        columns.forEach(c -> states.add(c.createState()));
    }

    /**
     * Populates a table row with the data extracted from the context and
     * formatted by the column definition.
     *
     * @param row
     *            The row to populate. The row must match the column definition.
     * @param data
     *            The context to work on
     */
    void populateRow(ConfigurableTablePrinter<D>.TableRow row, D data) {
        for (int i = 0; i < columns.size(); i++) {
            row.add(columns.get(i).getData(data, states.get(i)));
        }
    }

    void populateBaseline(ConfigurableTablePrinter<D>.TableRow row) {
        for (int i = 0; i < columns.size(); i++) {
            row.add(columns.get(i).getBaselineData(states.get(i)));
        }
    }

    void finalizeStates() {
        states.clear();
    }

    /**
     * @return unmodifiable list of columns
     */
    public List<AbstractPrinterColumn<D, ?, ?, ?>> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    /**
     * @return the heading text. Null means there will be no heading.
     */
    public String getHeading() {
        return heading;
    }

    /**
     * @param heading
     *            The new heading text or null to remove heading.
     * @return The object itself (fluent api)
     */
    public PrinterColumnList<D> withHeading(String heading) {
        this.heading = heading;
        return this;
    }

    /**
     * Finds the columns with the type given.
     * <p>
     * A table could contain more columns of the same type, so this function
     * returns all matching columns.
     * </p>
     * <p>
     * Note that this function intentially uses
     * <code>getClass().equals(clazz)</code> instead of <code>instanceof</code>,
     * so only the exact matches are returned. Columns of inherited classes are
     * not returned.
     *
     * @param clazz
     *            The class to look for
     * @return The list of all the columns with the type
     */
    public List<AbstractPrinterColumn<D, ?, ?, ?>> findByClass(Class<? extends AbstractPrinterColumn<D, ?, ?, ?>> clazz) {
        return columns.stream().filter(c -> c.getClass().equals(clazz)).collect(Collectors.toList());
    }

    /**
     * Returns the column with the title.
     *
     * @param title
     *            The title to look for
     * @return The column definition if there is any match
     */
    public Optional<AbstractPrinterColumn<D, ?, ?, ?>> findByTitle(String title) {
        return columns.stream().filter(c -> c.getTitle().equals(title)).findAny();
    }


}
