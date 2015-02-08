package org.clueminer.evolution.singlem;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.aggl.HACLW;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Population;
import org.clueminer.evolution.attr.TournamentPopulation;
import org.clueminer.evolution.multim.MultiMuteEvolution;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Evolution.class)
public class SingleMuteEvolution extends MultiMuteEvolution implements Runnable, Evolution, Lookup.Provider {

    private static final String name = "single-mute";
    private static final Logger logger = Logger.getLogger(SingleMuteEvolution.class.getName());
    private boolean isFinished = false;
    private Population<? extends Individual> population;

    public SingleMuteEvolution() {
        //cache normalized datasets
        this.exec = new ClusteringExecutorCached();
        init();
    }

    public SingleMuteEvolution(Executor executor) {
        this.exec = executor;
        init();
    }

    private void init() {
        algorithm = new HACLW();
        instanceContent = new InstanceContent();
        lookup = new AbstractLookup(instanceContent);
        prepare();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SingleMuteIndividual createIndividual() {
        return new SingleMuteIndividual(this);
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "starting evolution {0}", this.getClass().getName());
        evolutionStarted(this);
        clean();
        int stdMethods = standartizations.size();
        System.out.println("evaluator: " + getEvaluator().getName());

        if (ph != null) {
            int workunits = getGenerations();
            logger.log(Level.INFO, "stds: {0}", stdMethods);
            logger.log(Level.INFO, "distances: {0}", dist.size());
            logger.log(Level.INFO, "linkages: {0}", linkage.size());
            ph.start(workunits);
            ph.progress("starting " + getName() + "evolution...");
        }

        time.a = System.currentTimeMillis();
        LinkedList<Individual> children = new LinkedList<>();
        population = new TournamentPopulation(this, populationSize, SingleMuteIndividual.class);
        avgFitness.a = population.getAvgFitness();
        Individual best = population.getBestIndividual();
        bestFitness.a = best.getFitness();
        //ArrayList<Individual> selected = new ArrayList<>(populationSize);

        for (int g = 0; g < generations && !isFinished; g++) {
            // clear collection for new individuals
            children.clear();
            double fitness;
            // apply mutate operator
            for (int i = 0; i < population.size(); i++) {
                Individual current = population.getIndividual(i).deepCopy();

                do {
                    do {
                        current.mutate();
                    } while (isItTabu(current.toString()));
                    fitness = current.countFitness();
                    System.out.println("curr| " + current.getClustering().size() + ": " + fitness);
                    System.out.println(Arrays.toString(current.getClustering().clusterSizes()));
                    if (!Double.isNaN(fitness)) {
                        // put mutated individual to the list of new individuals
                        children.add(current);
                        tabu.add(current.toString());
                        //update meta-database
                        fireIndividualCreated(current);
                    }
                } while (!current.isValid());
            }
            logger.log(Level.INFO, "gen: {0}, num children: {1}", new Object[]{g, children.size()});

            // sort them by fitness (thanks to Individual implements interface Comparable)
            Individual[] nextGen = children.toArray(new Individual[0]);
            if (maximizedFitness) {
                //natural ordering
                Arrays.sort(nextGen);
            } else {
                Arrays.sort(nextGen, Collections.reverseOrder());
            }

            int indsToCopy;
            if (nextGen.length > population.size()) {
                indsToCopy = population.size() / 2;
            } else {
                indsToCopy = nextGen.length / 2;
            }
            if (ph != null) {
                ph.progress(indsToCopy + " new individuals in population. generation: " + g);
            }
            if (indsToCopy > 0) {
                System.out.println("copying " + indsToCopy + " new inds: " + nextGen.length);
                //TODO: old population should be sorted as well? take only part of the new population?
                //replace worser part of population by new ones
                System.out.println("arraycopy: " + (populationSize - indsToCopy - 1) + ", p: " + (populationSize - 1));
                System.arraycopy(nextGen, 0, population.getIndividuals(), populationSize - indsToCopy - 1, indsToCopy);
            } else {
                logger.log(Level.WARNING, "no new individuals in generation = {0}", g);
                //    throw new RuntimeException("no new individuals");
            }
            //sort whole population
            if (maximizedFitness) {
                //natural ordering
                Arrays.sort(population.getIndividuals());
            } else {
                Arrays.sort(population.getIndividuals(), Collections.reverseOrder());
            }

            // print statistic
            // System.out.println("gen: " + g + "\t bestFit: " + pop.getBestIndividual().getFitness() + "\t avgFit: " + pop.getAvgFitness());
            Individual bestInd = population.getBestIndividual();
            Clustering<Cluster> clustering = bestInd.getClustering();
            instanceContent.add(clustering);
            fireBestIndividual(g, population);
            fireResultUpdate(population.getIndividuals());
            if (ph != null) {
                ph.progress(g);
            }
        }

        time.b = System.currentTimeMillis();

        population.sortByFitness();
        avgFitness.b = population.getAvgFitness();
        best = population.getBestIndividual();
        bestFitness.b = best.getFitness();

        fireFinalResult(generations, best, time, bestFitness, avgFitness);

        finish();
    }

}
