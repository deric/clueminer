package org.clueminer.chart.data;

import org.clueminer.dataset.api.Statistics;

/**
 * Interface for an immutable access to tabular data.
 *
 * @see MutableDataSource
 */
public interface DataSource extends Iterable<Comparable<?>> {

    /**
     * Returns the column with the specified index.
     *
     * @param col index of the column to return
     * @return the specified column of the data source
     */
    Column getColumn(int col);

    /**
     * Returns the data types of all columns.
     *
     * @return The data types of all column in the data source
     */
    Class<? extends Comparable<?>>[] getColumnTypes();

    /**
     * Returns the row with the specified index.
     *
     * @param row index of the row to return
     * @return the specified row of the data source
     */
    Row getRow(int row);

    /**
     * Returns the value with the specified row and column index.
     *
     * @param col index of the column to return
     * @param row index of the row to return
     * @return the specified value of the data cell
     */
    Comparable<?> get(int col, int row);

    /**
     * Retrieves a object instance that contains various statistical
     * information on the current data source.
     *
     * @return statistical information
     */
    Statistics getStatistics();

    /**
     * Returns the number of rows of the data source.
     *
     * @return number of rows in the data source.
     */
    int getRowCount();

    /**
     * Returns the number of columns of the data source.
     *
     * @return number of columns in the data source.
     */
    int getColumnCount();

    /**
     * Returns whether the column at the specified index contains numbers.
     *
     * @param columnIndex Index of the column to test.
     * @return {@code true} if the column is numeric, otherwise {@code false}.
     */
    boolean isColumnNumeric(int columnIndex);

    /**
     * Adds the specified {@code DataListener} to this data source.
     *
     * @param dataListener listener to be added.
     */
    void addDataListener(DataListener dataListener);

    /**
     * Removes the specified {@code DataListener} from this data source.
     *
     * @param dataListener listener to be removed.
     */
    void removeDataListener(DataListener dataListener);
}
