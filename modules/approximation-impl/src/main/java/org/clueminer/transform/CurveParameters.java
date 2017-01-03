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
package org.clueminer.transform;

import java.util.ArrayList;
import java.util.List;
import org.clueminer.approximation.CurveApproximator;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.AttrHashDataset;
import org.clueminer.std.StdScale;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <O>
 */
@ServiceProvider(service = DataTransform.class)
public class CurveParameters<I extends Instance, O extends Instance> extends LegendreTransformation<I, O> implements DataTransform<I, O> {

    private static final String NAME = "curve parameters";
    private static final Logger LOG = LoggerFactory.getLogger(CurveParameters.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Dataset<O> createDefaultOutput(Dataset<I> input) {
        return new AttrHashDataset<>(input.size());
    }

    /**
     * Scale time points to interval -1.0 to 1.0
     *
     * @param dataset
     * @return array of numbers representing X axis
     */
    @Override
    protected double[] scaleTimePoints(Timeseries<ContinuousInstance> dataset) {
        TimePoint[] timePoints = dataset.getTimePoints();
        //find max and min values in dataset
        double[] xAxis = new double[timePoints.length];
        double min = timePoints[0].getPosition();
        double max = timePoints[timePoints.length - 1].getPosition();
        StdScale scale = new StdScale();

        for (int i = 0; i < timePoints.length; i++) {
            xAxis[i] = scale.scaleToRange(timePoints[i].getPosition(), min, max, 0.0, 1.0);
        }
        return xAxis;
    }

    @Override
    public void analyzeTimeseries(Timeseries<ContinuousInstance> dataset, Dataset<O> output, ProgressHandle ph, int segment) {
        //initial value of progress handle
        int analyzeProgress = segment * dataset.size();
        double[] xAxis = scaleTimePoints(dataset);
        ContinuousInstance item;
        int j = 0;
            //segment start

        //create attribute for each parameter
        List<Approximator> approx = new ArrayList<>();
        approx.add(new CurveApproximator());
        int offset = totalAttributes(approx) * segment;
        for (Approximator a : approx) {
            String[] attrs = a.getParamNames();
            for (String attribute : attrs) {
                if (segment > 0) {
                    attribute = segment + "_" + attribute;
                }
                output.setAttribute(offset + j, output.attributeBuilder().build(attribute, "NUMERIC"));
                j++;
            }
        }
        for (int i = 0; i < dataset.size(); i++) {
            item = dataset.instance(i);
            approximate(i, xAxis, item, output, approx, offset);
            //output
            ph.progress(++analyzeProgress);
        }
    }

}
