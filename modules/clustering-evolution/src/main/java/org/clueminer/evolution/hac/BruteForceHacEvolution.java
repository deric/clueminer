package org.clueminer.evolution.hac;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.CutoffStrategy;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.factory.CutoffStrategyFactory;
import org.clueminer.clustering.api.factory.LinkageFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.evolution.BaseEvolution;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
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
public class BruteForceHacEvolution extends BaseEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "Brute-force HAC";
    private final Executor exec;
    private int gen;
    private List<DistanceMeasure> dist;
    private List<ClusterLinkage> linkage;
    private List<CutoffStrategy> cutoff;
    private static final Logger logger = Logger.getLogger(BruteForceHacEvolution.class.getName());
    private int cnt;
    private final FakePopulation population = new FakePopulation();

    public BruteForceHacEvolution() {
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        //TODO allow changing algorithm used
        exec = new ClusteringExecutorCached();
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
        evolutionStarted(this);
        prepare();
        StandardisationFactory sf = StandardisationFactory.getInstance();
        List<String> standartizations = sf.getProviders();
        DistanceFactory df = DistanceFactory.getInstance();
        dist = df.getAll();
        LinkageFactory lf = LinkageFactory.getInstance();
        linkage = lf.getAll();
        CutoffStrategyFactory cf = CutoffStrategyFactory.getInstance();
        cutoff = cf.getAll();

        int stdMethods = standartizations.size();

        if (ph != null) {
            int workunits = stdMethods * 2 * dist.size() * linkage.size() * cutoff.size();
            logger.log(Level.INFO, "stds: {0}", stdMethods);
            logger.log(Level.INFO, "distances: {0}", dist.size());
            logger.log(Level.INFO, "linkages: {0}", linkage.size());
            logger.log(Level.INFO, "evolution workunits: {0}", workunits);
            ph.start(workunits);
        }
        cnt = 0;
        for (ClusterLinkage link : linkage) {
            ClusteringAlgorithm alg = getAlgorithm();
            if (alg instanceof AgglomerativeClustering) {
                if (!((AgglomerativeClustering) alg).isLinkageSupported(link.getName())) {
                    //skip unsupported linkages
                    continue;
                }
            }
            for (String std : standartizations) {
                //no log scale
                makeClusters(std, false, link);
                //with log scale
                makeClusters(std, true, link);
            }
        }
        finish();
    }

    /**
     * Make clusters - not war
     *
     * @param std
     * @param logscale
     * @param link
     */
    protected void makeClusters(String std, boolean logscale, ClusterLinkage link) {
        Props params = new Props();
        Clustering<? extends Cluster> clustering;
        params.put(AgglParams.ALG, exec.getAlgorithm().getName());
        params.putBoolean(AgglParams.LOG, logscale);
        params.put(AgglParams.STD, std);
        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        params.put(AgglParams.CUTOFF_SCORE, evaluator.getName());
        params.put(AgglParams.LINKAGE, link.getName());

        for (CutoffStrategy cut : cutoff) {
            params.put(AgglParams.CUTOFF_STRATEGY, cut.getName());
            for (DistanceMeasure dm : dist) {
                params.put(AgglParams.DIST, dm.getName());
                clustering = exec.clusterRows(dataset, params);
                //make sure the clustering is valid
                if (clustering.instancesCount() == dataset.size()) {
                    clustering.setName("#" + cnt);
                    individualCreated(clustering);
                }
                if (ph != null) {
                    ph.progress(cnt++);
                }
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
        if (uniqueClusterings.contains(clustering)) {
            Clustering other = (Clustering) uniqueClusterings.get(clustering);
            Props p = other.getParams();
            int occur = p.getInt(NUM_OCCUR, 1);
            p.putInt(NUM_OCCUR, occur + 1);
        } else {
            uniqueClusterings.add(clustering);
            instanceContent.add(clustering);
            SimpleIndividual current = new SimpleIndividual(clustering);
            current.countFitness();
            population.setCurrent(current);
            //update meta-database
            fireIndividualCreated(current);
            fireBestIndividual(gen++, population);
        }
    }

    @Override
    public Individual createIndividual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
