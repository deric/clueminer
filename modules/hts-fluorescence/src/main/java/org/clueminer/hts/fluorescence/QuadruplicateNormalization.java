/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataTransform.class)
public class QuadruplicateNormalization extends Normalization implements DataTransform {

    private static final String name = "David's normalization";
    private static final Logger logger = Logger.getLogger(QuadruplicateNormalization.class.getName());
    //background column
    private int backPos = 44;
    //top column
    private int topPos = 45;

    @Override
    public String getName() {
        return name;
    }

    public void setNormCols(int top, int back) {
        this.backPos = back;
        this.topPos = top;
    }

    @Override
    public void normalize(HtsPlate<HtsInstance> plate, HtsPlate<HtsInstance> normalized) {
        //Dataset normalized = plate.duplicate();
        //columns are numbered from 0
        int colCnt = plate.getColumnsCount();
        System.out.println("normalizing dataset....");
        System.out.println("col cnt: " + colCnt);
        System.out.println("attr : " + plate.attributeCount());
        double avg;
        double sum, posCtrl;
        Instance control1, control2;
        // time point for positive control
        int positiveTimepoint = 9;
        for (int i = 0; i < plate.getRowsCount() - 3; i += 4) {

            int pos;
            HtsInstance negativeControl = new FluorescenceInstance(plate, plate.attributeCount());
            // Instance negativeControl = new FluorescenceInstance(plate, colCnt);
            //System.out.println("row: " + i);
            //compute average instance
            posCtrl = 0.0;
            for (int j = 0; j < 4; j++) {
                pos = i + j;

                control2 = plate.instance(translatePosition(pos, topPos, colCnt));
                posCtrl += control2.value(positiveTimepoint);

            }
            posCtrl /= 4.0;

            for (int k = 0; k < plate.attributeCount(); k++) {
                sum = 0.0;

                for (int j = 0; j < 4; j++) {
                    pos = i + j;
                    control1 = plate.instance(translatePosition(pos, backPos, colCnt));
                    //  System.out.println("control: " + control1.getFullName() + " - " + control1.toString());
                    sum += control1.value(k);
                    //System.out.println("well " + control1.getName() + " = " + control1.value(k));
                }
                avg = sum / 4.0;
                negativeControl.set(k, avg);
            }

            logger.log(Level.INFO, "negative= {0}", negativeControl.toString());
            logger.log(Level.INFO, "positive= {0}", posCtrl);
            //normalize quadruplicate
            double value, divisor;

            for (int j = 0; j < 4; j++) {
                for (int m = 0; m < plate.getColumnsCount(); m++) {
                    pos = (i + j) * colCnt + m;

                    HtsInstance inst = plate.instance(pos);
                    //  System.out.println("well = "+inst.getName());
                    FluorescenceInstance out = new FluorescenceInstance((Timeseries<? extends ContinuousInstance>) normalized, plate.attributeCount());
                    for (int k = 0; k < plate.attributeCount(); k++) {
                        //substract background
                        divisor = posCtrl - negativeControl.value(k);

                        if (divisor == 0.0) {
                            value = 0.0;
                        } else {
                            value = ((inst.value(k) - negativeControl.value(k)) / divisor) * 100;
                        }
                        out.set(k, value);
                        out.setName(inst.getName());
                        out.setRow(inst.getRow());
                        out.setColumn(inst.getColumn());
                        out.setId(inst.getId());
                    }
                    normalized.add(out);
                }
            }
        }
    }

    @Override
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        normalize((HtsPlate<HtsInstance>) dataset, (HtsPlate<HtsInstance>) output);
    }

    @Override
    public Dataset<? extends Instance> createDefaultOutput(Dataset<? extends Instance> input) {
        return input.duplicate();
    }
}
