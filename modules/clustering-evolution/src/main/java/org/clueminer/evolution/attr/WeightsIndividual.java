package org.clueminer.evolution.attr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.PartitioningClustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.BaseIndividual;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class WeightsIndividual extends BaseIndividual<WeightsIndividual> implements Individual<WeightsIndividual> {

    private double fitness = 0;
    private static Random rand = new Random();
    private double[] weights;
    private Clustering<? extends Cluster> clustering;

    public WeightsIndividual(Evolution evolution) {
        this.evolution = (EvolutionSO) evolution;
        this.algorithm = evolution.getAlgorithm();
        init();
    }

    /**
     * Copying constructor
     *
     * @param parent
     */
    public WeightsIndividual(WeightsIndividual parent) {
        this.evolution = parent.evolution;
        this.algorithm = parent.algorithm;
        this.weights = new double[parent.weights.length];
        System.arraycopy(parent.weights, 0, weights, 0, parent.weights.length);
        this.fitness = parent.fitness;
    }

    private void init() {
        weights = new double[evolution.attributesCount()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextDouble();
        }
        countFitness();
    }

    @Override
    public Clustering<? extends Cluster> getClustering() {
        return clustering;
    }

    @Override
    public double countFitness() {
        clustering = updateCustering();
        fitness = evaluationTable(clustering).getScore(evolution.getEvaluator());
        return fitness;
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
        Dataset<Instance> data = (Dataset<Instance>) evolution.getDataset().duplicate();
        Instance copy;
        Dataset<? extends Instance> orig = evolution.getDataset();
        for (Instance inst : orig) {
            copy = data.builder().createCopyOf(inst, data);
            copy.setId(inst.getId());
            copy.setIndex(inst.getIndex());

            for (int i = 0; i < inst.size(); i++) {
                copy.set(i, inst.value(i) * weights[i]);
            }
            data.add(copy);
        }
        Clustering<? extends Cluster> result = ((PartitioningClustering) algorithm).partition(data);
        Props p = result.getParams();
        for (int i = 0; i < weights.length; i++) {
            p.put("w(" + data.getAttribute(i).getName() + ")", String.format("%1$,.2f", weights[i]));
        }
        return result;
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
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < evolution.getMutationProbability()) {
                weights[i] = rand.nextDouble();
            }
        }
    }

    @Override
    public List<WeightsIndividual> cross(Individual i) {
        List<WeightsIndividual> offsprings = new ArrayList<>();
        // we'll work with copies
        WeightsIndividual thisOne = this.deepCopy();
        WeightsIndividual secondOne = ((WeightsIndividual) i).deepCopy();
        int cross_id = rand.nextInt(evolution.attributesCount());
        System.arraycopy(((WeightsIndividual) i).weights, 0, thisOne.weights, 0, cross_id);
        System.arraycopy(((WeightsIndividual) i).weights, cross_id, secondOne.weights, cross_id, evolution.attributesCount() - cross_id);
        System.arraycopy(this.weights, 0, secondOne.weights, 0, cross_id);
        System.arraycopy(this.weights, cross_id, thisOne.weights, cross_id, evolution.attributesCount() - cross_id);
        offsprings.add(thisOne);
        offsprings.add(secondOne);
        return offsprings;
    }

    @Override
    public WeightsIndividual deepCopy() {
        WeightsIndividual newOne = new WeightsIndividual(this);
        return newOne;
    }

    @Override
    public boolean isCompatible(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WeightsIndividual duplicate() {
        WeightsIndividual duplicate = new WeightsIndividual(evolution);
        return duplicate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");

        for (int i = 0; i < weights.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(weights[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Props getProps() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
