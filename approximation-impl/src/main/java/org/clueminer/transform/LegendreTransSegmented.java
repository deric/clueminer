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

    /**
     * Analyze allows user to provide own data structure for storing results
     *
     * @param dataset
     * @param output result is saved into this variable
     * @param ph
     */
    @Override
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        analyze(dataset, output, ph, 2);
    }
    /**
     *
     * @param dataset
     * @param output
     * @param ph
     * @param n number of segments
     */
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph, int n) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        Timeseries<ContinuousInstance>[] segments;
        //protected var
        degree = 7;
        //items to finish
        ph.start(n * dataset.size());
        segments = splitIntoSegments(d, n);
        int seg = 0;
        for (Timeseries<ContinuousInstance> input : segments) {
            analyzeTimeseries(input, (Dataset<Instance>) output, ph, seg);
            //Dump.matrix(output.arrayCopy(), "output-" + seg, 2);
            seg++;
        }
    }

    protected Timeseries<ContinuousInstance>[] splitIntoSegments(Timeseries<ContinuousInstance> source, int n) {
        int inc = source.attributeCount() / n;

        Timeseries<ContinuousInstance>[] res = new Timeseries[n];
        int offset = 0;
        int attrCnt = source.attributeCount();
        int remain;
        int uppper;
        double p;
        double pInc;
        int m;
        for (int i = 0; i < n; i++) {
            res[i] = (Timeseries<ContinuousInstance>) source.duplicate();
            int pos = offset;
            //if remaining attributes won't fill next segment, just make longer one
            remain = attrCnt - offset;
            if (remain < 2 * inc) {
                inc = remain;
            }
            TimePoint[] tp = new TimePointAttribute[inc];
            // position in interval <0, 1>
            pInc = 1.0 / (double) (inc - 1);
            p = 0.0;
            m = 0;
            uppper = offset + inc;
            //create attributes
            while (pos < uppper) {
                tp[m] = new TimePointAttribute(m, pos, p);
                p += pInc;
                pos++;
                m++;
            }
            res[i].setTimePoints(tp);
            for (int j = 0; j < source.size(); j++) {
                for (int k = 0; k < res[i].attributeCount(); k++) {
                    res[i].setAttributeValue(k, j, source.getAttributeValue(offset + k, j));
                }
            }
            //Dump.matrix(res[i].arrayCopy(), "dataset-" + i, 2);
            offset += inc;
        }
        return res;
    }
}
