package org.clueminer.evolution.attr;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
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
 * @param <I>
 * @param <E>
 * @param <C>
 */
public class WeightsIndividual<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends BaseIndividual<I, E, C> implements Individual<I, E, C> {

    private double fitness = 0;
    private static Random rand = new Random();
    private double[] weights;
    private Props props;
    private Clustering<E, C> clustering;

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
        this.props = parent.props.clone();
    }

    private void init() {
        weights = new double[evolution.attributesCount()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextDouble();
        }
        props = new Props();
        props.putInt("k", ((AttrEvolution) evolution).getK());
        countFitness();
    }

    @Override
    public Clustering<E, C> getClustering() {
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
    public Clustering<E, C> updateCustering() {
        Dataset<E> data = (Dataset<E>) evolution.getDataset().duplicate();
        E copy;
        Dataset<E> orig = evolution.getDataset();
        for (E inst : orig) {
            copy = data.builder().createCopyOf(inst, data);
            copy.setId(inst.getId());
            copy.setIndex(inst.getIndex());

            for (int i = 0; i < inst.size(); i++) {
                copy.set(i, inst.value(i) * weights[i]);
            }
            data.add(copy);
        }
        Clustering<E, C> result = algorithm.cluster(data, props);
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
    public List<I> cross(Individual i) {
        List<I> offsprings = new ArrayList<>();
        // we'll work with copies
        WeightsIndividual thisOne = (WeightsIndividual) this.deepCopy();
        WeightsIndividual secondOne = (WeightsIndividual) (I) i.deepCopy();
        int cross_id = rand.nextInt(evolution.attributesCount());
        System.arraycopy(((WeightsIndividual) i).weights, 0, thisOne.weights, 0, cross_id);
        System.arraycopy(((WeightsIndividual) i).weights, cross_id, secondOne.weights, cross_id, evolution.attributesCount() - cross_id);
        System.arraycopy(this.weights, 0, secondOne.weights, 0, cross_id);
        System.arraycopy(this.weights, cross_id, thisOne.weights, cross_id, evolution.attributesCount() - cross_id);
        offsprings.add((I) thisOne);
        offsprings.add((I) secondOne);
        return offsprings;
    }

    @Override
    public I deepCopy() {
        I newOne = (I) new WeightsIndividual(this);
        return newOne;
    }

    @Override
    public boolean isCompatible(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public I duplicate() {
        I duplicate = (I) new WeightsIndividual(evolution);
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
        return props;
    }

}
