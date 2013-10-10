package org.clueminer.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.approximation.LegendreApproximator;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.AttrHashDataset;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataTransform.class)
public class LegendreTransformation implements DataTransform {

    private static String name = "ortho-polynomials (Legendre)";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        analyze(d, output, ph);
    }

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
            //create attribute for each parameter
            List<Approximator> approx = new ArrayList<Approximator>();
            approx.add(new LegendreApproximator(5));


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

        ph.finish();
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

                for (Iterator<Map.Entry<String, Double>> it = coefficients.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, Double> item = it.next();
                    output.setAttributeValue(item.getKey(), i, item.getValue());
                }
            }
        }
    }

    @Override
    public Dataset<? extends Instance> createDefaultOutput(Dataset<? extends Instance> input) {
        return new AttrHashDataset(input.size());
    }
}
