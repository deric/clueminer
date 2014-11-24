package org.clueminer.clustering.api.evolution;

/**
 *
 * @author Tomas Barton
 * @param <I> individual
 */
public interface Population<I> {

    /**
     *
     * @return size of the population
     */
    int size();

    /**
     * Average fitness in whole population
     *
     * @return
     */
    double getAvgFitness();

    /**
     *
     * @return best solution
     */
    I getBestIndividual();

    /**
     *
     * @return whole population
     */
    I[] getIndividuals();

    /**
     * updates population
     *
     * @param individuals
     */
    void setIndividuals(I[] individuals);

    I getIndividual(int idx);

    void setIndividuals(int index, I individual);

    double getBestFitness();

    /**
     * Sort population by its fitness score
     */
    void sortByFitness();

}
