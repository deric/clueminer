package org.clueminer.clustering.api.evolution;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;

/**
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
    public Clustering<Cluster> getClustering();

    /**
     * Update fitness function value (counting fitness could be quite expensive)
     */
    public void countFitness();

    /**
     *
     * @return value of fitness function
     */
    public double getFitness();

    /**
     *
     * @return Clustering algorithm which is used in the Individual
     */
    public ClusteringAlgorithm getAlgorithm();

    /**
     * Force to initial use some algorithm (might be later changed by evolution
     * algorithm - depends on Individual implementation)
     *
     * @param algorithm
     */
    public void setAlgorithm(ClusteringAlgorithm algorithm);

    /**
     * With given probability (from Evolution class) should mutate each property
     * of the Individual
     *
     * @TODO implement this behaviour -- currently only weights are modified
     */
    public void mutate();

    /**
     * Crossover of genetic information
     *
     * @param other
     * @return
     */
    public List<E> cross(Individual other);

    /**
     * Crossover between completely different algorithms might to be very
     * efficient, to prevent strange mutants each Individual can decide whether
     * allow crossover with the other or not
     *
     * @param other
     * @return true when this Individual is compatible specie with the other
     */
    public boolean isCompatible(Individual other);

    /**
     * Duplicate might not share all properties like the original, but it's same
     * class. Used for initialization of population
     *
     * @return not a precise clone
     */
    public E duplicate();

    /**
     * Should make exact clone. Deep copy (unlike shallow copy) means that
     * changing property in child won't affect parent
     *
     * @return exact clone of Individual
     */
    public E deepCopy();
}
