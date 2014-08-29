package org.clueminer.chart.data;

/**
 * Interface that can be implemented to listen for changes in data sources.
 *
 * @see DataSource
 */
public interface DataListener {

    /**
     * Method that is invoked when data has been added.
     * This method is invoked by objects that provide support for
     * {@code DataListener}s and should not be called manually.
     *
     * @param source Data source that has been changed.
     * @param events Optional event object describing the data values that
     *               have been added.
     */
    void dataAdded(DataSource source, DataChangeEvent... events);

    /**
     * Method that is invoked when data has been updated.
     * This method is invoked by objects that provide support for
     * {@code DataListener}s and should not be called manually.
     *
     * @param source Data source that has been changed.
     * @param events Optional event object describing the data values that
     *               have been updated.
     */
    void dataUpdated(DataSource source, DataChangeEvent... events);

    /**
     * Method that is invoked when data has been removed.
     * This method is invoked by objects that provide support for
     * {@code DataListener}s and should not be called manually.
     *
     * @param source Data source that has been changed.
     * @param events Optional event object describing the data values that
     *               have been removed.
     */
    void dataRemoved(DataSource source, DataChangeEvent... events);
}
