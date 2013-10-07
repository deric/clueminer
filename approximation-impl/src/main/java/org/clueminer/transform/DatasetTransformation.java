package org.clueminer.transform;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.ApproximatorFactory;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.math.Standardisation;
import org.clueminer.std.StdScale;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataTransform.class)
public class DatasetTransformation implements DataTransform {

    private boolean save = false;
    private static String name = "approx cubic-exp-poly9";
    

    @Override
    public String getName() {
        return name;
    }

    /**
     * Computes characteristic values for dataset, should be run each time
     * values changes
     *
     * @return
     */
    private void approximate(int i, double[] xAxis, ContinuousInstance input, Dataset<Instance> output, List<Approximator> approx) throws UnsupportedAttributeType {
        HashMap<String, Double> coefficients;
        if (input.size() > 0) {
            InstanceBuilder builder = output.builder();
            if (output.size() <= i) {
                Instance instance = builder.create(output.attributeCount());
                instance.setName(input.getFullName());
                instance.setId(input.getId());
                instance.setAncestor(input);
                output.add(instance);
            }
            for (Approximator a : approx) {
                coefficients = new HashMap<String, Double>();
                a.estimate(xAxis, input, coefficients);

                for (Iterator<Entry<String, Double>> it = coefficients.entrySet().iterator(); it.hasNext();) {
                    Entry<String, Double> item = it.next();
                    output.setAttributeValue(item.getKey(), i, item.getValue());
                }
            }
        }
    }

    @Override
    public void analyze(Timeseries<ContinuousInstance> dataset, Dataset<Instance> output, ProgressHandle ph) {
        int analyzeProgress = 0;
        ph.start(dataset.size());
        TimePoint[] timePoints = dataset.getTimePoints();
        //find max and min values in dataset
        System.out.println("starting analysis timepoints " + timePoints.length);
        double[] xAxis = new double[timePoints.length];
        for (int i = 0; i < timePoints.length; i++) {
            xAxis[i] = timePoints[i].getPosition();
        }
        ContinuousInstance item;
        try {
            int j = 0;
            ApproximatorFactory am = ApproximatorFactory.getDefault();
            //create attribute for each parameter
            List<Approximator> approx = new ArrayList<Approximator>();
            //for(Approximator ap : am.getAll()){
            //    System.out.println(ap.getName());
            //}

            approx.add(am.getProvider("cubic"));
            approx.add(am.getProvider("exp"));
            approx.add(am.getProvider("exp-inv"));
            approx.add(am.getProvider("avg"));
            approx.add(am.getProvider("poly9"));
            //approx.add(am.getProvider("chebyshev-5"));
            for (Approximator a : approx) {
                String[] attrs = a.getParamNames();
                for (String attribute : attrs) {
                    output.setAttribute(j++, output.attributeBuilder().create(attribute, "NUMERIC"));
                }
            }
            for (int i = 0; i < dataset.size(); i++) {
                item = dataset.instance(i);
                approximate(i, xAxis, item, output, approx);
                //output
                ph.progress(++analyzeProgress);
            }

        } catch (UnsupportedAttributeType ex) {
            Logger.getLogger(DatasetTransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("approximation finished");
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
     */
    public void approximationRMSE(Timeseries<ContinuousInstance> dataset, Dataset<Instance> params, TimePoint[] timePoints, String filename, String separator, String eol) {
        ApproximatorFactory am = ApproximatorFactory.getDefault();
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
            e.printStackTrace();
        }
    }

    public void saveParamsToFile(Dataset<? extends Instance> dataset, double[][] params, String filename, String separator, String eol) {
        Instance current;
        String sampleName;
        int i, j, pos;
        int m = params.length, n = params[0].length;
        double value;
        ApproximatorFactory am = ApproximatorFactory.getDefault();
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
            e.printStackTrace();
        }
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }
}
