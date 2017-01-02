/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.hts.fluorescence;

import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.utils.DatasetWriter;

/**
 *
 * @author Tomas Barton
 * @param <E>
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

    @Override
    public void setRowsCount(int rows) {
        this.rows = rows;
    }

    @Override
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

    public void setAttributes(TimePointAttribute[] tp) {
        this.timePoints = tp;
    }

    @Override
    public double[] getTimestampsArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
