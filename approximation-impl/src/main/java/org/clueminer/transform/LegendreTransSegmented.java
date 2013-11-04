package org.clueminer.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.approximation.LegendreApproximator;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.exception.UnsupportedAttributeType;
import org.clueminer.types.TimePoint;
import org.clueminer.utils.Dump;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataTransform.class)
public class LegendreTransSegmented extends LegendreTransformation implements DataTransform {

    private static String name = "Legendre segmented";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;

        //split dataset
        // number of segments
        int n = 3;


        System.out.println("input: " + dataset.toString());

        analyzeTimeseries(d, (Dataset<Instance>) output, ph);
        System.out.println("output: " + output.toString());
    }

    public void analyzeTimeseries(Timeseries<ContinuousInstance> dataset, Dataset<Instance> output, ProgressHandle ph) {
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
            System.out.println("out attr = " + output.attributeCount());
            for (int i = 0; i < dataset.size(); i++) {
                item = dataset.instance(i);
                System.out.println("approximating cls: " + item.getClass().getName());
                System.out.println("[0] = " + item.value(0));
                Dump.array(item.arrayCopy(), "item");
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

}
