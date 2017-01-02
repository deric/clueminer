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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clueminer.approximation.LegendreApproximator;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
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
public class LegendreTransformation<I extends Instance, O extends Instance> implements DataTransform<I, O> {

    private static String name = "ortho-polynomials (Legendre)";
    protected int degree;
    private static final Logger LOG = LoggerFactory.getLogger(LegendreTransformation.class);

    public LegendreTransformation() {
        degree = 5;
    }

    public LegendreTransformation(int degree) {
        this.degree = degree;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void analyze(Dataset<I> dataset, Dataset<O> output, ProgressHandle ph) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        LOG.info("starting transformation {}", name);
        ph.start(dataset.size());
        analyzeTimeseries(d, output, ph, 0);
        LOG.info("finished transformation {}", name);
        ph.finish();
    }

    /**
     * Scale time points to interval -1.0 to 1.0
     *
     * @param dataset
     * @return array of numbers representing X axis
     */
    protected double[] scaleTimePoints(Timeseries<ContinuousInstance> dataset) {
        TimePoint[] timePoints = dataset.getTimePoints();
        //find max and min values in dataset
        double[] xAxis = new double[timePoints.length];
        double min = timePoints[0].getPosition();
        double max = timePoints[timePoints.length - 1].getPosition();
        StdScale scale = new StdScale();

        for (int i = 0; i < timePoints.length; i++) {
            xAxis[i] = scale.scaleToRange(timePoints[i].getPosition(), min, max, -1.0, 1.0);
        }

        return xAxis;
    }

    public void analyzeTimeseries(Timeseries<ContinuousInstance> dataset, Dataset<O> output, ProgressHandle ph, int segment) {
        //initial value of progress handle
        int analyzeProgress = segment * dataset.size();
        double[] xAxis = scaleTimePoints(dataset);
        ContinuousInstance item;
        int j = 0;
            //segment start

        //create attribute for each parameter
        List<Approximator> approx = new ArrayList<>();
        approx.add(new LegendreApproximator(degree));
        int offset = totalAttributes(approx) * segment;
        AttributeBuilder builder = output.attributeBuilder();
        Attribute attr;
        for (Approximator a : approx) {
            String[] attrs = a.getParamNames();
            for (String attribute : attrs) {
                if (segment > 0) {
                    attribute = segment + "_" + attribute;
                }
                attr = builder.build(attribute, "NUMERIC");
                attr.setDataset(output);
                output.setAttribute(offset + j, attr);
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

    public int totalAttributes(List<Approximator> approx) {
        int cnt = 0;
        for (Approximator a : approx) {
            cnt += a.getNumCoefficients();
        }
        return cnt;
    }

    /**
     * Computes characteristic values for dataset, should be run each time
     * values changes
     *
     * @param i
     * @param xAxis
     * @param input
     * @param output
     * @param approx
     * @param offset
     */
    protected void approximate(int i, double[] xAxis, ContinuousInstance input, Dataset<O> output, List<Approximator> approx, int offset) {
        HashMap<String, Double> coefficients;
        int idx;
        if (input.size() > 0) {
            InstanceBuilder<O> builder = output.builder();
            if (output.size() <= i) {
                O instance = builder.build(output.attributeCount());
                instance.setName(input.getName());
                instance.setId(input.getId());
                instance.setAncestor(input);
                output.add(instance);
            }
            for (Approximator a : approx) {
                coefficients = new HashMap<>();
                a.estimate(xAxis, input, coefficients);
                idx = offset;
                for (Map.Entry<String, Double> item : coefficients.entrySet()) {
                    output.set(i, idx++, item.getValue());
                }
            }
        }
    }

    @Override
    public Dataset<O> createDefaultOutput(Dataset<I> input) {
        //number of attributes is some default, could be expanded
        LOG.info("input size: {} attrs {}", input.size(), input.attributeCount());
        return new AttrHashDataset<>(input.size());
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
