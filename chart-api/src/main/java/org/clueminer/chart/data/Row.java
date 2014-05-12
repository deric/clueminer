package org.clueminer.chart.data;

/**
 * <p>
 * Class for easily accessing a row of a data source.</p>
 *
 * <p>
 * Example:</p>
 * <pre>
 * Row row = new Row(data, 2);
 * Number value = row.get(3);
 * </pre>
 *
 * @see DataSource
 */
public abstract class Row extends DataAccessor {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = 2725146484866525573L;

    /**
     * Initializes a new instances with the specified data source and
     * row index.
     *
     * @param source Data source.
     * @param row    Row index.
     */
    public Row(DataSource source, int row) {
        super(source, row);
    }

    @Override
    public Comparable<?> get(int col) {
        DataSource source = getSource();
        if (source == null) {
            return null;
        }
        return source.get(col, getIndex());
    }

    @Override
    public int size() {
        return getSource().getColumnCount();
    }

    /**
     * Returns whether the column at the specified index contains numbers.
     *
     * @param columnIndex Index of the column to test.
     * @return {@code true} if the column is numeric, otherwise {@code false}.
     */
    public boolean isColumnNumeric(int columnIndex) {
        return getSource().isColumnNumeric(columnIndex);
    }
}
