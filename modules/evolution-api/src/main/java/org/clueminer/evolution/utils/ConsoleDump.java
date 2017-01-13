/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.evolution.utils;

import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionListener;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Pair;
import org.clueminer.evolution.api.Population;

/**
 * Basic printer of evolutionary process.
 *
 * @author Tomas Barton
 */
public class ConsoleDump implements EvolutionListener {

    private boolean onlyFinal = false;
    private int nTh = -1;

    /**
     * Simple evolution debug to STDOUT.
     */
    public ConsoleDump() {

    }

    public ConsoleDump(boolean onlyFinal) {
        this.onlyFinal = onlyFinal;
    }

    /**
     * Print result of n-th generation
     *
     * @param nth
     */
    public ConsoleDump(int nth) {
        this.nTh = nth;
    }

    @Override
    public void bestInGeneration(int generationNum, Population<? extends Individual> population, double external) {
        if (!onlyFinal) {
            if (nTh > -1 && (generationNum % nTh == 0)) {
                Clustering<Instance, Cluster<Instance>> clusters = population.getBestIndividual().getClustering();
                System.out.println("============== generation: " + generationNum);
                System.out.println("external = " + external);
                System.out.println("avgFit = " + population.getAvgFitness());
                System.out.println("clustering: " + clusters.toString());
                System.out.println("==============");
            }
        }
    }

    @Override
    public void finalResult(Evolution evol, int generations, Individual best,
            Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        long evoTime = (long) ((time.b - time.a) / 1000.0);
        System.out.println("++++++++++++++");
        System.out.println("Evolution has finished after " + evoTime + " s...");
        System.out.println("avgFit(G:0)= " + avgFitness.a + " avgFit(G:" + (generations - 1)
                + ")= " + avgFitness.b + " -> " + percent(avgFitness.b / avgFitness.a) + " %");
        System.out.println("bstFit(G:0)= " + bestFitness.a + " bstFit(G:" + (generations - 1)
                + ")= " + bestFitness.b + " -> " + percent(bestFitness.b / bestFitness.a) + " %");
        System.out.println("bestIndividual= " + best);
        System.out.println("external criterion = " + external);
        System.out.println("==============");
    }

    private String percent(double val) {
        return String.format("%.2f", val * 100);
    }

    @Override
    public void started(Evolution evolution) {
    }

    @Override
    public void resultUpdate(Individual[] result) {
        //not much to do
    }
}
