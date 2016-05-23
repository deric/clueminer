package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.std.StdDev;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = DataStandardization.class)
public class StdDataDev<E extends Instance> extends StdDev implements DataStandardization<E> {

    @Override
    public Dataset<E> optimize(Dataset<E> dataset) {
        double avg, dev;
        Dataset<E> opt = (Dataset<E>) dataset.duplicate();
        Instance orig;

        for (int j = 0; j < dataset.attributeCount(); j++) {
            avg = dataset.getAttribute(j).statistics(AttrNumStats.AVG);
            //TODO: consider using STD_DEV (Bessel's correction)
            //dev = dataset.getAttribute(j).statistics(AttrNumStats.STD_DEV);
            dev = dataset.getAttribute(j).statistics(AttrNumStats.STD_X);
            for (int i = 0; i < dataset.size(); i++) {
                opt.set(i, j, (dataset.get(i, j) - avg) / dev);
                if (j == 0) {
                    orig = dataset.get(i);
                    opt.get(i).setClassValue(orig.classValue());
                    opt.get(i).setId(orig.getId());
                    opt.get(i).setName(orig.getName());
                    opt.get(i).setAncestor(orig);
                    opt.changedClass(i, orig.classValue(), null);
                }
            }
        }

        return opt;
    }

}
