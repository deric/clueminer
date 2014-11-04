package org.clueminer.dataset.std;

import org.clueminer.dataset.api.DataStandardization;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.std.StdScale;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = DataStandardization.class)
public class StdMinMax extends StdScale implements DataStandardization {

    @Override
    public Dataset<? extends Instance> optimize(Dataset<? extends Instance> dataset) {
        double min, max;
        Dataset<? extends Instance> opt = dataset.duplicate();
        Instance orig;

        for (int j = 0; j < dataset.attributeCount(); j++) {
            min = dataset.getAttribute(j).statistics(AttrNumStats.MIN);
            max = dataset.getAttribute(j).statistics(AttrNumStats.MAX);
            for (int i = 0; i < dataset.size(); i++) {
                opt.set(i, j, scaleToRange(dataset.get(i, j), min, max, getTargetMin(), getTargetMax()));
                if (j == 0) {
                    orig = dataset.get(i);
                    opt.get(i).setClassValue(orig.classValue());
                    opt.get(i).setId(orig.getId());
                    opt.get(i).setName(orig.getName());
                    opt.get(i).setAncestor(orig);
                }
            }
        }

        return opt;
    }

}
