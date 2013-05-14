package org.clueminer.evolution;

import java.util.ArrayList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class Evolution implements Runnable {

    private int populationSize = 100;
    private int generations = 100;
    private Dataset<Instance> dataset;
    private boolean isFinished = true;
    /**
     * Probability of mutation
     */
    protected double mutationProbability = 0.5;
    /**
     * Probability of crossover
     */
    protected double crossoverProbability = 0.5;

    public Evolution(Dataset<Instance> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long end;
        ArrayList<Individual> newInds = new ArrayList<Individual>();
        Population pop = new Population(this, populationSize);

        for (int i = 0; i < generations && !isFinished; i++) {
        }
        end = System.currentTimeMillis();
        System.out.println("evolution took " + (end - start) + " ms");
    }
}
