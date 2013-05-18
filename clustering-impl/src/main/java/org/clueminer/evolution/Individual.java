package org.clueminer.evolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterEvaluatorFactory;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.evaluation.hclust.CopheneticCorrelation;
import org.clueminer.hclust.HillClimbCutoff;
import org.clueminer.hclust.NaiveCutoff;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.AlgorithmParameters;
import org.clueminer.utils.Dump;

/**
 *
 * @author Tomas Barton
 */
public class Individual implements Comparable<Individual> {

    private ClusteringAlgorithm algorithm;
    private Evolution evolution;
    private double fitness = 0;
    private static Random rand = new Random();
    private double[] weights;
    private AlgorithmParameters params;
    private boolean debug = true;

    public Individual(Evolution evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        init();
    }

    private void init() {
        weights = new double[evolution.attributesCount()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextDouble();
        }

    }

    public Clustering<Cluster> getClustering() {
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

    public void countFitness() {
        Clustering<Cluster> clusters = getClustering();

        fitness = evolution.evaluator.score(clusters, evolution.getDataset());
        System.out.println("fitness = " + fitness);
    }

    public double getFitness() {
        return fitness;
    }

    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void mutate() {
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < evolution.mutationProbability) {
                weights[i] = rand.nextDouble();
            }
        }
    }

    public List<Individual> cross(Individual i) {
        ArrayList<Individual> offsprings = new ArrayList<Individual>();
        // we'll work with copies
        Individual thisOne = this.deepCopy();
        Individual secondOne = ((Individual) i).deepCopy();
        int cross_id = rand.nextInt(evolution.attributesCount());
        System.arraycopy(((Individual) i).weights, 0, thisOne.weights, 0, cross_id);
        System.arraycopy(((Individual) i).weights, cross_id, secondOne.weights, cross_id, evolution.attributesCount() - cross_id);
        System.arraycopy(this.weights, 0, secondOne.weights, 0, cross_id);
        System.arraycopy(this.weights, cross_id, thisOne.weights, cross_id, evolution.attributesCount() - cross_id);
        offsprings.add(thisOne);
        offsprings.add(secondOne);
        return offsprings;
    }

    public Individual deepCopy() {
        Individual newOne = new Individual(evolution);
        newOne.weights = new double[this.weights.length];
        System.arraycopy(this.weights, 0, newOne.weights, 0, this.weights.length);
        newOne.fitness = this.fitness;
        return newOne;
    }

    @Override
    public int compareTo(Individual another) {
        if (this.getFitness() > another.getFitness()) {
            return -1;
        }
        if (this.getFitness() < another.getFitness()) {
            return 1;
        }
        return 0;
    }

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
