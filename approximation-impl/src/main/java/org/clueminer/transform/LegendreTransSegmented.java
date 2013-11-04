package org.clueminer.transform;

import org.clueminer.approximation.api.DataTransform;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
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

        //split dataset
        // number of segments
        int n = 2;

        splitIntoSegments(d, n);

        //protected var
        degree = 7;

        Dump.matrix(d.arrayCopy(), "input", 2);

        analyzeTimeseries(d, (Dataset<Instance>) output, ph);

        Dump.matrix(output.arrayCopy(), "output", 2);
    }

    protected Dataset<? extends Instance>[] splitIntoSegments(Timeseries<ContinuousInstance> source, int n) {
        int pos = source.attributeCount() / n;
        System.out.println("pos = " + pos);

        Timeseries<ContinuousInstance>[] res = new Timeseries[n];
        for (int i = 0; i < n; i++) {
            res[i] = (Timeseries<ContinuousInstance>) source.duplicate();
            for (int j = 0; j < res.length; j++) {
                Timeseries<ContinuousInstance> timeseries = res[j];

                /**
                 * TODO: finish dataset segmentation
                 */
            }
        }

        return null;
    }
}
