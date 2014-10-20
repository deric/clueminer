package org.clueminer.transform;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.clueminer.std.StdScale;
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
    protected int degree;
    private static final Logger logger = Logger.getLogger(LegendreTransformation.class.getName());

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
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        logger.log(Level.INFO, "starting transformation {0}", name);
        ph.start(dataset.size());
        analyzeTimeseries(d, (Dataset<Instance>) output, ph, 0);
        logger.log(Level.INFO, "finished transformation {0}", name);
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

    public void analyzeTimeseries(Timeseries<ContinuousInstance> dataset, Dataset<Instance> output, ProgressHandle ph, int segment) {
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
    protected void approximate(int i, double[] xAxis, ContinuousInstance input, Dataset<Instance> output, List<Approximator> approx, int offset) {
        HashMap<String, Double> coefficients;
        int idx;
        if (input.size() > 0) {
            InstanceBuilder builder = output.builder();
            if (output.size() <= i) {
                Instance instance = builder.build(output.attributeCount());
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
    public Dataset<? extends Instance> createDefaultOutput(Dataset<? extends Instance> input) {
        //number of attributes is some default, could be expanded
        logger.log(Level.INFO, "input size: {0} attrs {1}", new Object[]{input.size(), input.attributeCount()});
        return new AttrHashDataset<>(input.size());
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
