/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.transform;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.clueminer.dataset.row.TimeRow;
import org.clueminer.fixtures.TimeseriesFixture;
import org.clueminer.io.csv.CSVReader;

/**
 *
 * @author deric
 */
public class TsTest {

    protected static final TimeseriesFixture TF = new TimeseriesFixture();
    private static Timeseries<ContinuousInstance> data01;

    public static Timeseries<ContinuousInstance> loadData01() throws IOException {
        if (data01 == null) {
            File f = TF.data01();

            TimeRow inst;
            int i;
            try (CSVReader csv = new CSVReader(new FileReader(f))) {
                inst = new TimeRow(Double.class, 15);
                i = 0;
                String[] row;
                double value;
                while ((row = csv.readNext()) != null) {
                    value = Double.valueOf(row[0]);
                    inst.put(value);
                    i++;
                }
            }

            data01 = generateDataset(1, i);
            data01.add(inst);
        }
        return data01;
    }

    protected static TimeseriesDataset generateDataset(int capacity, int attrCnt) {
        TimePointAttribute[] tp = new TimePointAttribute[attrCnt];
        for (int j = 0; j < tp.length; j++) {
            tp[j] = new TimePointAttribute(j, j, j);
        }

        return new TimeseriesDataset<>(capacity, tp);
    }

}
