package org.clueminer.evolution.hac;

import java.util.List;
import org.clueminer.clustering.ClusteringExecutor;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.LinkageFactory;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.evolution.AbstractEvolution;
import org.clueminer.math.Matrix;
import org.clueminer.math.StandardisationFactory;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 * Not really evolution, pretty much enumeration of all possible settings of
 * hierarchical agglomerative clustering
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class HacEvolution extends AbstractEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "HAC";
    private final ClusteringExecutor exec;
    private int gen;
    private List<DistanceMeasure> dist;
    private List<ClusterLinkage> linkage;

    public HacEvolution() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        //TODO allow changing algorithm used
        exec = new ClusteringExecutor();
        gen = 0;
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
        StandardisationFactory sf = StandardisationFactory.getInstance();
        List<String> standartizations = sf.getProviders();
        DistanceFactory df = DistanceFactory.getInstance();
        dist = df.getAll();
        LinkageFactory lf = LinkageFactory.getInstance();
        linkage = lf.getAll();

        int stdMethods = standartizations.size();

        if (ph != null) {
            ph.start(stdMethods * 2 * dist.size() * linkage.size());
            ph.progress("starting evolution...");
        }
        int i = 0;
        for (String std : standartizations) {
            for (ClusterLinkage link : linkage) {
                //no log scale
                makeClusters(std, false, i, link);
                //with log scale
                makeClusters(std, true, i, link);
            }
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
    protected void makeClusters(String std, boolean logscale, int i, ClusterLinkage link) {
        Props params = new Props();
        Clustering<? extends Cluster> clustering;
        params.put(AgglParams.ALG, algorithm.getName());
        params.putBoolean(AgglParams.LOG, logscale);
        params.put(AgglParams.STD, std);
        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        params.put(AgglParams.CUTOFF_STRATEGY, "hill-climb cutoff");
        params.put(AgglParams.CUTOFF_SCORE, evaluator.getName());
        params.put(AgglParams.LINKAGE, link.getName());
        for (DistanceMeasure dm : dist) {
            params.put(AgglParams.DIST, dm.getName());
            clustering = exec.clusterRows(dataset, dm, params);
            individualCreated(clustering);
            if (ph != null) {
                ph.progress(i++);
            }
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

    protected void individualCreated(Clustering<? extends Cluster> clustering) {
        instanceContent.add(clustering);
        fireBestIndividual(gen++, new BaseIndividual(clustering), getEvaluator().score((Clustering<Cluster>) clustering, dataset));
    }

}
