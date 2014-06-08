package org.clueminer.evolution;

import java.util.List;
import java.util.prefs.Preferences;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hclust.HillClimbCutoff;
import org.clueminer.math.Matrix;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.std.Scaler;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class NormalizationEvolution extends AbstractEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "Normalizations";
    private AgglomerativeClustering algorithm;

    public NormalizationEvolution() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        //TODO allow changing algorithm used
        algorithm = new HCL();
    }

    @Override
    public String getName() {
        return name;
    }

    private void prepare() {
        if (dataset == null) {
            throw new RuntimeException("missing data");
        }
    }

    @Override
    public void run() {
        prepare();

        Preferences params = NbPreferences.forModule(NormalizationEvolution.class);
        StandardisationFactory sf = StandardisationFactory.getInstance();
        List<String> standartizations = sf.getProviders();

        int stdMethods = standartizations.size();

        if (ph != null) {
            ph.start(stdMethods * 2);
            ph.progress("starting evolution...");
        }
        int i = 0;
        for (String std : standartizations) {
            //no log scale
            makeClusters(std, false, params, i);
            //with log scale
            makeClusters(std, true, params, i);
        }

        finish();
    }

    /**
     * Make clusters - not war
     *
     * @param std
     * @param logscale
     * @param params
     * @param i
     */
    protected void makeClusters(String std, boolean logscale, Preferences params, int i) {
        Clustering clustering;
        Matrix input = standartize(dataset, std, logscale);
        HierarchicalResult rowsResult = algorithm.hierarchy(input, dataset, params);
        HillClimbCutoff strategy = new HillClimbCutoff((ClusterEvaluator) evaluator);
        rowsResult.findCutoff(strategy);
        clustering = rowsResult.getClustering();
        DendrogramMapping mapping = new DendrogramData(dataset, input, rowsResult);
        clustering.lookupAdd(mapping);
        instanceContent.add(clustering);
        if (ph != null) {
            ph.progress(i++);
        }
    }

    public Matrix standartize(Dataset<? extends Instance> data, String method, boolean logScale) {
        return Scaler.standartize(data.arrayCopy(), method, logScale);
    }

    protected void finish() {
        if (ph != null) {
            ph.finish();
        }
    }

}
