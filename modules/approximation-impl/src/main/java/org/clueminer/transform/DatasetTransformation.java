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
package org.clueminer.transform;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.ApproximatorFactory;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.impl.AttrHashDataset;
import org.clueminer.math.Standardisation;
import org.clueminer.stats.NumericalStats;
import org.clueminer.std.StdScale;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;
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
public class DatasetTransformation<I extends Instance, O extends Instance> implements DataTransform<I, O> {

    private boolean save = false;
    public static final String NAME = "approx cubic-exp-poly9";
    private static final Logger LOG = LoggerFactory.getLogger(DatasetTransformation.class);

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Computes characteristic values for dataset, should be run each time
     * values changes
     *
     * @return
     */
    private void approximate(int i, double[] xAxis, ContinuousInstance input, Dataset<O> output, List<Approximator> approx) {
        HashMap<String, Double> coefficients;
        if (input.size() > 0) {
            InstanceBuilder<O> builder = output.builder();
            if (output.size() < i) {
                O instance = builder.create(output.attributeCount());
                instance.setName(input.getFullName());
                instance.setId(input.getId());
                instance.setAncestor(input);
                output.add(instance);
            }
            for (Approximator a : approx) {
                coefficients = new HashMap<>();
                a.estimate(xAxis, input, coefficients);
                for (Entry<String, Double> item : coefficients.entrySet()) {
                    output.setAttributeValue(item.getKey(), i, item.getValue());
                }
            }
        } else {
            LOG.warn("empty input on index {}", i);
        }

    }

    @Override
    public void analyze(Dataset<I> dataset, Dataset<O> output, ProgressHandle ph) {
        //this will cause casting exception if used in incorrect context
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        analyze(d, output, ph);
    }

    public void analyze(Timeseries<ContinuousInstance> dataset, Dataset<O> output, ProgressHandle ph) {
        int analyzeProgress = 0;
        ph.start(dataset.size());
        TimePoint[] timePoints = dataset.getTimePoints();
        //find max and min values in dataset
        LOG.info("starting analysis timepoints {}", timePoints.length);
        double[] xAxis = new double[timePoints.length];
        for (int i = 0; i < timePoints.length; i++) {
            xAxis[i] = timePoints[i].getPosition();
        }
        ContinuousInstance item;

        ApproximatorFactory am = ApproximatorFactory.getInstance();
        //create attribute for each parameter
        List<Approximator> approx = new ArrayList<>();
        //for(Approximator ap : am.getAll()){
        //    System.out.println(ap.getName());
        //}

        approx.add(am.getProvider("cubic"));
        approx.add(am.getProvider("exp"));
        approx.add(am.getProvider("exp-inv"));
        approx.add(am.getProvider("avg"));
        approx.add(am.getProvider("poly9"));
        //approx.add(am.getProvider("chebyshev-5"));
        AttributeBuilder builder = output.attributeBuilder();
        Attribute attr;
        for (Approximator a : approx) {
            String[] attrs = a.getParamNames();
            for (String attribute : attrs) {
                attr = builder.create(attribute, "NUMERIC");
                attr.registerStatistics(new NumericalStats(attr));
                attr.setDataset(output);
            }
        }
        for (int i = 0; i < dataset.size(); i++) {
            item = dataset.get(i);
            approximate(i, xAxis, item, output, approx);
            //output
            ph.progress(++analyzeProgress);
        }
        LOG.info("approximation finished");
        //save approximation to file
        String prefix = System.getProperty("user.home") /*
                 * FileUtils.LocalFolder()
                 */ + "/" + dataset.getName() + "approximation-" + String.valueOf(Math.random());
        //approximationRMSE(dataset, output, timePoints, prefix + ".csv", ";", "\n");
        if (save) {
            saveResults(dataset, prefix, output.arrayCopy());
        }
        ph.finish();
    }

    private void saveResults(Timeseries<ContinuousInstance> dataset, String prefix, double[][] params) {
        if (params[0].length > 0) {
            saveParamsToFile(dataset, params, prefix + "-params.csv", ";", "\n");
            Standardisation std = new StdScale();
            params = std.optimize(params, params.length, params[0].length);
            //Dump.matrix(params, "optimized", 3);
            saveParamsToFile(dataset, params, prefix + "-params-scaled.csv", ";", "\n");
        }
    }

    /**
     * Computes RMSE between approximated and real data
     *
     * @param dataset
     * @param params
     * @param timePoints
     * @param filename
     * @param separator
     * @param eol
     */
    public void approximationRMSE(Timeseries<ContinuousInstance> dataset, Dataset<Instance> params, TimePoint[] timePoints, String filename, String separator, String eol) {
        ApproximatorFactory am = ApproximatorFactory.getInstance();
        double rmse;
        HashMap<String, Double> coeff;
        double diff;
        Instance current;
        String sampleName;
        int i, j, pos;
        try {
            System.out.println("writing to " + filename);
            FileWriter writer = new FileWriter(filename);
            //header
            writer.append(separator);
            for (Approximator a : am.getAll()) {
                writer.append(a.getName()).append(separator);
            }
            writer.append(eol);
            //content
            for (i = 0; i < dataset.size(); i++) {
                current = dataset.instance(i);
                sampleName = current.getName();
                if (sampleName != null) {
                    pos = sampleName.indexOf("\n");
                    if (pos > 0) {
                        sampleName = sampleName.substring(0, pos);
                    } else {
                        sampleName = "test " + i;
                    }
                }
                writer.append(sampleName).append(separator);
                for (Approximator a : am.getAll()) {
                    rmse = 0;
                    //find indexes for given approximator and fetch them from
                    //params dataset
                    for (j = 0; j < timePoints.length; j++) {
                        diff = a.getFunctionValue(timePoints[j].getPosition(), params.instance(j).arrayCopy()) - current.value(j);
                        rmse += Math.pow(diff, 2);
                    }
                    rmse = Math.sqrt(rmse / timePoints.length);
                    writer.append(String.valueOf(rmse)).append(separator);
                }
                writer.append(eol);
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    public void saveParamsToFile(Dataset<? extends Instance> dataset, double[][] params, String filename, String separator, String eol) {
        Instance current;
        String sampleName;
        int i, j, pos;
        int m = params.length, n = params[0].length;
        double value;
        ApproximatorFactory am = ApproximatorFactory.getInstance();
        List<String> paramNames = am.getParamNames();
        try {
            System.out.println("writing to " + filename);
            FileWriter writer = new FileWriter(filename);
            //header
            writer.append(separator);
            for (String s : paramNames) {
                writer.append(s).append(separator);
            }
            writer.append(eol);
            //content
            for (i = 0; i < m; i++) {
                current = dataset.instance(i);
                sampleName = current.getName();
                if (sampleName != null) {
                    pos = sampleName.indexOf("\n");
                    if (pos > 0) {
                        sampleName = sampleName.substring(0, pos);
                    } else {
                        sampleName = "test " + i;
                    }
                    writer.append(sampleName).append(separator);
                }
                for (j = 0; j < paramNames.size(); j++) {
                    value = params[i][j];
                    writer.append(String.valueOf(value)).append(separator);
                }
                writer.append(eol);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    @Override
    public Dataset<O> createDefaultOutput(Dataset<I> input) {
        return new AttrHashDataset<>(input.size());
    }
}
