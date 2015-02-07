package org.clueminer.evolution.api;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.utils.Props;

/**
 * @param <E>
 * @TODO eventually move to the API package, when mature enough
 *
 * @author Tomas Barton
 */
public interface Individual<E extends Individual> extends Comparable<Individual> {

    /**
     * From encoded information about clustering should generate real
     * assignments to clusters
     *
     * @return final clustering
     */
    Clustering<? extends Cluster> getClustering();

    /**
     * Update fitness function value (might take a while counting fitness could
     * be quite expensive)
     *
     * @return computed fitness value
     */
    double countFitness();

    /**
     *
     * @return value of fitness function
     */
    double getFitness();

    /**
     *
     * @return Clustering algorithm which is used in the Individual
     */
    ClusteringAlgorithm getAlgorithm();

    /**
     * Force to initial use some algorithm (might be later changed by evolution
     * algorithm - depends on Individual implementation)
     *
     * @param algorithm
     */
    void setAlgorithm(ClusteringAlgorithm algorithm);

    /**
     * With given probability (from Evolution class) should mutate each property
     * of the Individual
     *
     * @TODO implement this behaviour -- currently only weights are modified
     */
    void mutate();

    /**
     * Crossover of genetic information
     *
     * @param other
     * @return
     */
    List<E> cross(Individual other);

    /**
     * Crossover between completely different algorithms might to be very
     * efficient, to prevent strange mutants each Individual can decide whether
     * allow crossover with the other or not
     *
     * @param other
     * @return true when this Individual is compatible specie with the other
     */
    boolean isCompatible(Individual other);

    /**
     * Duplicate might not share all properties like the original, but it's same
     * class. Used for initialization of population
     *
     * @return not a precise clone
     */
    E duplicate();

    /**
     * Should make exact clone. Deep copy (unlike shallow copy) means that
     * changing property in child won't affect parent
     *
     * @return exact clone of Individual
     */
    E deepCopy();

    /**
     * Some mutations might bring us to invalid state
     *
     * @return true when fitness could be counted
     */
    boolean isValid();

    /**
     * Algorithm properties
     *
     * @return
     */
    Props getProps();

    /**
     *
     * @param clustering
     * @return
     */
    EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering);
}
