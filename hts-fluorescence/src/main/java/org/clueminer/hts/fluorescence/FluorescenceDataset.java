package org.clueminer.hts.fluorescence;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.utils.DatasetWriter;

/**
 *
 * @author Tomas Barton
 */
public class FluorescenceDataset<E extends FluorescenceInstance> extends TimeseriesDataset<E> implements Timeseries<E>, Dataset<E>, HtsPlate<E> {

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

    public void setRowsCount(int rows) {
        this.rows = rows;
    }

    public void setColumnsCount(int cols) {
        this.cols = cols;
    }
    
    @Override
    public FluorescenceDataset duplicate() {
        FluorescenceDataset copy = new FluorescenceDataset(this.rows, this.cols);
        copy.setName(this.getName());        
        copy.timePoints = this.timePoints;
        return copy;
    }

    public void toCsv(DatasetWriter writer) {
        String[] header = new String[attributeCount() + 2];
        header[0] = "Name";
        header[1] = "ID";
        int i = 2;
        for (TimePointAttribute ta : getTimePoints()) {
            header[i++] = String.valueOf(ta.getTimestamp());
        }
        writer.writeNext(header);

        for (FluorescenceInstance inst : this) {
            writer.writeNext(inst.toArray());
        }
    }
}
