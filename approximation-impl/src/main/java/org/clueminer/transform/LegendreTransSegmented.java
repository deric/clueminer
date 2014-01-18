package org.clueminer.transform;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.approximation.api.DataTransform;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.std.StdScale;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataTransform.class)
public class LegendreTransSegmented extends LegendreTransformation implements DataTransform {

    private static String name = "Legendre segmented";
    private static final Logger logger = Logger.getLogger(LegendreTransSegmented.class.getName());
    private int numSegments = 2;

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
     * @param output  result is saved into this variable
     * @param ph
     */
    @Override
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph) {
        // last two params: number of segments and degree of polynomials

        int workunits = numSegments * dataset.size();
        logger.log(Level.INFO, "work units = {0}", workunits);
        ph.start(workunits);
        analyze(dataset, output, ph, numSegments, degree);
        ph.finish();
    }

    /**
     * Make sure you allocate enough work units for the progress bar
     *
     * @param dataset
     * @param output
     * @param ph
     * @param n       number of segments
     * @param deg     max degree of Legendre polynomials
     */
    public void analyze(Dataset<? extends Instance> dataset, Dataset<? extends Instance> output, ProgressHandle ph, int n, int deg) {
        Timeseries<ContinuousInstance> d = (Timeseries<ContinuousInstance>) dataset;
        Timeseries<ContinuousInstance>[] segments;
        //protected var
        degree = deg;
        //items to finish

        segments = splitIntoSegments(d, n);
        int seg = 0;
        for (Timeseries<ContinuousInstance> input : segments) {
            analyzeTimeseries(input, (Dataset<Instance>) output, ph, seg);
            //Dump.matrix(output.arrayCopy(), "output-" + seg, 2);
            logger.log(Level.INFO, "segment {0}", seg);
            seg++;
        }
        logger.log(Level.INFO, "finished");
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
        double value, min, max;
        int m;
        StdScale scale = new StdScale();
        logger.log(Level.INFO, "splitting: {0} size= {1} attr cnt = {2}", new Object[]{source.getClass().getName(), source.size(), source.attributeCount()});
        for (int i = 0; i < n; i++) {
            //res[i] = (Timeseries<ContinuousInstance>) source.duplicate();
            res[i] = new TimeseriesDataset<ContinuousInstance>(source.size());
            res[i].setName(source.getName() + " segment " + i);
            int pos = offset;
            //if remaining attributes won't fill next segment, just make longer one
            remain = attrCnt - offset;
            if (remain < 2 * inc) {
                inc = remain;
            }
            TimePoint[] tp = new TimePointAttribute[inc];
            // position in interval <0, 1>
            pInc = 2.0 / (double) (inc - 1);
            p = -1.0;
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
            min = Double.MAX_VALUE;
            max = Double.MIN_VALUE;
            for (int j = 0; j < source.size(); j++) {
                for (int k = 0; k < res[i].attributeCount(); k++) {
                    value = source.getAttributeValue(offset + k, j);
                    if (value < min) {
                        min = value;
                    }
                    if (value > max) {
                        max = value;
                    }
                    res[i].setAttributeValue(k, j, value);
                    //System.out.println(res[i].instance(j).toString());
                }
            }
            //   Dump.matrix(res[i].arrayCopy(), "not scaled-" + i, 2);
            for (int j = 0; j < source.size(); j++) {
                for (int k = 0; k < res[i].attributeCount(); k++) {
                    value = source.getAttributeValue(offset + k, j);
                    value = scale.scaleToRange(value, min, max, -1.0, 1.0);
                    res[i].setAttributeValue(k, j, value);
                }
                res[i].instance(j).setAncestor(source.instance(j));
                res[i].instance(j).setName(source.instance(j).getName());
            }
            // Dump.matrix(res[i].arrayCopy(), "dataset-" + i, 2);
            offset += inc;
        }
        return res;
    }

    public int getNumSegments() {
        return numSegments;
    }

    public void setNumSegments(int numSegments) {
        this.numSegments = numSegments;
    }
}
