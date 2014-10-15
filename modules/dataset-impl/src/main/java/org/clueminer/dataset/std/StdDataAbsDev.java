package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.std.StdAbsDev;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataStandardization.class)
public class StdDataAbsDev extends StdAbsDev implements DataStandardization {

    @Override
    public Dataset<? extends Instance> optimize(Dataset<? extends Instance> dataset) {
        double avg, dev;
        Dataset<? extends Instance> opt = dataset.duplicate();

        for (int j = 0; j < dataset.attributeCount(); j++) {
            avg = dataset.getAttribute(j).statistics(AttrNumStats.AVG);
            dev = dataset.getAttribute(j).statistics(AttrNumStats.ABS_DEV);
            for (int i = 0; i < dataset.size(); i++) {
                opt.set(i, j, (dataset.get(i, j) - avg) / dev);
                if (j == 0) {
                    opt.get(i).setClassValue(dataset.get(i).classValue());
                }
            }
        }

        return opt;
    }

}
