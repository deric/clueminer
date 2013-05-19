package org.clueminer.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.AlgorithmParameters;

/**
 *
 * @author Tomas Barton
 */
public class WeightsIndividual extends AbstractIndividual<WeightsIndividual> implements Individual<WeightsIndividual> {

    private double fitness = 0;
    private static Random rand = new Random();
    private double[] weights;
    private AlgorithmParameters params;
    private Clustering<Cluster> clustering;

    public WeightsIndividual(Evolution evolution) {
        this.evolution = evolution;
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

    }

    @Override
    public Clustering<Cluster> getClustering() {
        return clustering;
    }

    @Override
    public void countFitness() {
        clustering = updateCustering();
        fitness = evolution.evaluator.score(clustering, evolution.getDataset());
    }

    /**
     * Some algorithms (like k-means) have random initialization, so we can't
     * reproduce the same results, therefore we have to keep the resulting
     * clustering
     *
     * @return clustering according to current parameters
     */
    private Clustering<Cluster> updateCustering() {
        Dataset<Instance> data = evolution.getDataset().duplicate();
        double[] values;
        Instance copy;
        for (Instance inst : evolution.getDataset()) {
            values = inst.arrayCopy();

            for (int i = 0; i < values.length; i++) {
                values[i] = values[i] * weights[i];
            }
            copy = data.builder().create(values);
            copy.setClassValue(inst.classValue());
            data.add(copy);
        }
        return algorithm.partition(data);
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public void mutate() {
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < evolution.mutationProbability) {
                weights[i] = rand.nextDouble();
            }
        }
    }

    @Override
    public List<WeightsIndividual> cross(Individual i) {
        List<WeightsIndividual> offsprings = new ArrayList<WeightsIndividual>();
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
}
