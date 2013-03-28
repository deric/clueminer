package org.clueminer.data;

/**
 * A DatasetView is a modification of an original dataset
 * 
 * @author Tomas Barton
 */
public interface DatasetView {

    /**
     * Returns this view unique identifier. The id is a positive integer. The
     * main view always has it's id equal to zero.
     *
     * @return the view identifier
     */
    public int getViewId();

    /**
     * Returns
     * <code>true</code> if this view is the main view. Each
     * <code>DatasetModel</code> has a single main view, which contains all
     * nodes and edges in the model.
     *
     * @return      <code>true</code> if this is the main view, <code>false</code>
     * otherwise.
     */
    public boolean isMainView();

    /**
     * Return the dataset model which belongs to this view
     *
     * @return
     */
    public DatasetModel getDatasetModel();
}
