package org.clueminer.hts.fluorescence;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.hts.api.HtsPlate;

/**
 *
 * @author Tomas Barton
 */
public class FluorescenceDataset extends TimeseriesDataset<FluorescenceInstance> implements Timeseries<FluorescenceInstance>, Dataset<FluorescenceInstance>, HtsPlate<FluorescenceInstance> {

    private static final long serialVersionUID = -3437746341858198780L;
    private int rows;
    private int cols;

    public FluorescenceDataset(int size) {
        super(size);
    }

    public FluorescenceDataset(int rows, int cols) {
        super(rows * cols);
        this.rows = rows;
        this.cols = cols;

    }

    @Override
    public int getRowsCount() {
        return rows;
    }

    @Override
    public int getColumnsCount() {
        return cols;
    }
}
