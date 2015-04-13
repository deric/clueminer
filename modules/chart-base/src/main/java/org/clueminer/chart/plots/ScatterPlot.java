package org.clueminer.chart.plots;

import org.clueminer.chart.base.AbstractPlot;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class ScatterPlot extends AbstractPlot {

    protected Dataset<? extends Instance> dataset;


    void setDataset(Dataset<? extends Instance> data) {
        this.dataset = data;
    }

}
