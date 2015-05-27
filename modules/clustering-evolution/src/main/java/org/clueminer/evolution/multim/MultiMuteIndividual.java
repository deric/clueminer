package org.clueminer.evolution.multim;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.BaseIndividual;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class MultiMuteIndividual extends BaseIndividual<MultiMuteIndividual> implements Individual<MultiMuteIndividual> {

    protected double fitness = 0;
    protected static Random rand = new Random();
    protected Clustering<? extends Cluster> clustering;
    protected Props genom;
    private static final Logger logger = Logger.getLogger(MultiMuteIndividual.class.getName());

    public MultiMuteIndividual() {

    }

    public MultiMuteIndividual(EvolutionSO evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        this.genom = evolution.getDefaultProps();
        init();
    }

    /**
     * Copying constructor
     *
     * @param parent
     */
    public MultiMuteIndividual(MultiMuteIndividual parent) {
        this.evolution = parent.evolution;
        this.algorithm = parent.algorithm;
        this.genom = parent.genom.copy();

        this.fitness = parent.fitness;
    }

    private void init() {
        genom.put(AgglParams.ALG, algorithm.getName());
        genom.putBoolean(AgglParams.LOG, logscale(rand));
        genom.put(AgglParams.STD, std(rand));
        genom.putBoolean(AgglParams.CLUSTER_ROWS, true);
        genom.put(AgglParams.CUTOFF_STRATEGY, "hill-climb inc");
        genom.put(AgglParams.CUTOFF_SCORE, evaluator().getName());
        do {
            genom.put(AgglParams.LINKAGE, linkage(rand));
        } while (!isValid());

        //genom.put(AgglParams.DIST, distance(rand));
        //first we might want to mutate etc, then count fitness
        //countFitness();
    }

    protected boolean logscale(Random rand) {
        return rand.nextBoolean();
    }

    protected String std(Random rand) {
        int size = ((MultiMuteEvolution) evolution).stds.size();
        int i = rand.nextInt(size);
        return ((MultiMuteEvolution) evolution).stds.get(i);
    }

    protected String linkage(Random rand) {
        int size = ((MultiMuteEvolution) evolution).linkage.size();
        int i = rand.nextInt(size);
        return ((MultiMuteEvolution) evolution).linkage.get(i).getName();
    }

    protected String distance(Random rand) {
        int size = ((MultiMuteEvolution) evolution).dist.size();
        int i = rand.nextInt(size);
        return ((MultiMuteEvolution) evolution).dist.get(i).getName();
    }

    public InternalEvaluator evaluator() {
        InternalEvaluatorFactory ief = InternalEvaluatorFactory.getInstance();
        List<InternalEvaluator> evals = ief.getAll();
        int size = evals.size();
        int i = rand.nextInt(size);
        return evals.get(i);
    }

    @Override
    public Clustering<? extends Cluster> getClustering() {
        return clustering;
    }

    @Override
    public double countFitness() {
        if (algorithm instanceof AgglomerativeClustering) {
            AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
            while (!isValid()) {
                genom.put(AgglParams.LINKAGE, linkage(rand));
            }
        }
        clustering = updateCustering();
        if (!isValid()) {
            return Double.NaN;
        }
        EvaluationTable et = evaluationTable(clustering);
        if (et == null) {
            throw new RuntimeException("missing eval table");
        }
        fitness = et.getScore(evolution.getEvaluator());
        return fitness;
    }

    /**
     * Clustering should be updated after each mutation
     *
     * @param eval
     * @return
     */
    public double countFitness(ClusterEvaluation eval) {
        if (clustering == null) {
            updateCustering();
            if (!isValid()) {
                return Double.NaN;
            }
        }
        EvaluationTable et = evaluationTable(clustering);
        if (et == null) {
            throw new RuntimeException("missing eval table");
        }
        return et.getScore(eval);
    }

    /**
     * Some algorithms (like k-means) have random initialization, so we can't
     * reproduce the same results, therefore we have to keep the resulting
     * clustering
     *
     * @return clustering according to current parameters
     */
    @Override
    public Clustering<? extends Cluster> updateCustering() {
        logger.log(Level.INFO, "starting clustering {0}", genom.toString());
        clustering = ((MultiMuteEvolution) evolution).exec.clusterRows(evolution.getDataset(), genom);
        ClusterEvaluation eval = evolution.getExternal();
        if (eval != null) {
            logger.log(Level.INFO, "finished clustering, supervised score ({0}): {1}", new Object[]{eval.getName(), countFitness(eval)});
        }
        return clustering;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    /**
     * For tests only
     *
     * @param fitness
     */
    protected void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public void mutate() {
        if (performMutation()) {
            genom.putBoolean(AgglParams.LOG, logscale(rand));
        }
        if (performMutation()) {
            genom.put(AgglParams.STD, std(rand));
        }
        if (performMutation()) {
            genom.put(AgglParams.LINKAGE, linkage(rand));
        }
        //mutating distance is complicated
        /*if (performMutation()) {
         genom.put(AgglParams.DIST, distance(rand));
         }*/
    }

    @Override
    public List<MultiMuteIndividual> cross(Individual i) {
        throw new UnsupportedOperationException("not supported yet");
    }

    private boolean performMutation() {
        return rand.nextDouble() < evolution.getMutationProbability();
    }

    @Override
    public MultiMuteIndividual deepCopy() {
        MultiMuteIndividual newOne = new MultiMuteIndividual(this);
        return newOne;
    }

    @Override
    public boolean isCompatible(Individual other) {
        return this.getClass() == other.getClass();
    }

    @Override
    public MultiMuteIndividual duplicate() {
        MultiMuteIndividual duplicate = new MultiMuteIndividual(evolution);
        return duplicate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        sb.append(genom.toString());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean isValid() {
        boolean ret = true;
        if (algorithm instanceof AgglomerativeClustering) {
            AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
            ret = ret && aggl.isLinkageSupported(genom.get(AgglParams.LINKAGE, "Complete Linkage"));
        }
        if (clustering != null) {
            if (clustering.size() < 2) {
                //we don't want solutions with 0 or 1 cluster
                return false;
            }

            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset != null) {
                if (clustering.instancesCount() != dataset.size()) {
                    return false;
                }
            }
        }

        return ret;
    }

    @Override
    public Props getProps() {
        return genom;
    }

    public String getGen(String key) {
        return genom.get(key);
    }

    public void setGen(String key, String value) {
        genom.put(key, value);
    }
}
