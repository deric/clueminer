package org.clueminer.evolution.multim;

import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.evolution.BaseIndividual;
import org.clueminer.evolution.api.Evolution;
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

    public MultiMuteIndividual() {

    }

    public MultiMuteIndividual(Evolution evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        this.genom = new Props();
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
        genom.put(AgglParams.CUTOFF_SCORE, evolution.getEvaluator().getName());
        do {
            genom.put(AgglParams.LINKAGE, linkage(rand));
        } while (!isValid());
        genom.put(AgglParams.DIST, distance(rand));
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

    @Override
    public Clustering<? extends Cluster> getClustering() {
        return clustering;
    }

    @Override
    public double countFitness() {
        clustering = updateCustering();
        EvaluationTable et = evaluationTable(clustering);
        if (et == null) {
            throw new RuntimeException("missing eval table");
        }
        fitness = et.getScore(evolution.getEvaluator());
        return fitness;
    }

    /**
     * Some algorithms (like k-means) have random initialization, so we can't
     * reproduce the same results, therefore we have to keep the resulting
     * clustering
     *
     * @return clustering according to current parameters
     */
    private Clustering<? extends Cluster> updateCustering() {
        clustering = ((MultiMuteEvolution) evolution).exec.clusterRows(evolution.getDataset(), genom);
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
        if (performMutation()) {
            genom.put(AgglParams.DIST, distance(rand));
        }
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
            ret = ret && aggl.isLinkageSupported(genom.get(AgglParams.LINKAGE));
        }
        if (clustering != null && clustering.size() < 2) {
            //we don't want solutions with 0 or 1 cluster
            return false;
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
