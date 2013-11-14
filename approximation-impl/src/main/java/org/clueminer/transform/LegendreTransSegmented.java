package org.clueminer.transform;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
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

    public LegendreTransSegmented() {
        //nothing to do
    }

    public LegendreTransSegmented(int degree) {
        super(degree);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        Timeseries<ContinuousInstance>[] segments;
        //protected var
        degree = 7;
        //split dataset
        // number of segments
        int n = 3;

        segments = splitIntoSegments(d, n);
        int i = 0;
        for (Timeseries<ContinuousInstance> input : segments) {
            System.out.println("input: " + input.toString());
            Dump.matrix(input.arrayCopy(), "input " + i, 2);
            i++;
        }

        Dump.matrix(d.arrayCopy(), "input", 2);

        analyzeTimeseries(d, (Dataset<Instance>) output, ph);

        Dump.matrix(output.arrayCopy(), "output", 2);
    }

    protected Timeseries<ContinuousInstance>[] splitIntoSegments(Timeseries<ContinuousInstance> source, int n) {
        int inc = source.attributeCount() / n;
        System.out.println("attr cnt = " + source.attributeCount());
        System.out.println("inc = " + inc);

        Timeseries<ContinuousInstance>[] res = new Timeseries[n];
        int offset = 0;
        for (int i = 0; i < n; i++) {
            res[i] = (Timeseries<ContinuousInstance>) source.duplicate();
            System.out.println("res class = " + res.getClass().getName());
            int pos = offset;
            TimePoint[] tp = new TimePointAttribute[inc];
            //create attributes
            while (pos < inc && pos < n) {
                tp[pos] = new TimePointAttribute(pos, pos, (offset - pos) / (double) pos);
                pos++;
            }
            res[i].setTimePoints(tp);
            System.out.println("seg dataset has: " + res[i].attributeCount() + " attributes. and z");
            for (int j = 0; j < source.size(); j++) {
                for (int k = 0; k < res[i].attributeCount(); k++) {
                    System.out.println("[" + k + ", " + j + "] = " + source.getAttributeValue(offset + k, j));
                    /**
                     * TODO fix underlying implementation so that we can set
                     * value on (almost) any index
                     */
                    res[i].setAttributeValue(k, j, source.getAttributeValue(offset + k, j));
                }
            }
            System.out.println("offset = " + offset);
            offset += inc;
        }

        return res;
    }
}
