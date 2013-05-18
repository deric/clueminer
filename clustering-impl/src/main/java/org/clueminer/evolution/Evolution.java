package org.clueminer.evolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evaluation.external.JaccardIndex;
import org.clueminer.stats.AttrNumStats;
import org.clueminer.utils.CSVWriter;
import org.clueminer.utils.DatasetWriter;
import org.clueminer.utils.FileUtils;
import org.clueminer.utils.PointTypeIterator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class Evolution implements Runnable {

    private int populationSize = 100;
    private int generations;
    private Dataset<Instance> dataset;
    private boolean isFinished = true;
    private Random rand = new Random();
    private int debugLimit = 10;
    /**
     * Probability of mutation
     */
    protected double mutationProbability = 0.3;
    /**
     * Probability of crossover
     */
    protected double crossoverProbability = 0.3;
    /**
     * for start and final average fitness
     */
    private Pair<Double, Double> avgFitness;
    /**
     * for start and final best fitness in whole population
     */
    private Pair<Double, Double> bestFitness;
    /**
     * for star and final time
     */
    private Pair<Long, Long> time;
    protected ClusterEvaluation evaluator;
    protected ClusteringAlgorithm algorithm;
    private String benchmarkFolder;
    protected ClusterEvaluation jaccard = new JaccardIndex();

    public Evolution(Dataset<Instance> dataset, int generations) {
        this.dataset = dataset;
        isFinished = false;
        this.generations = generations;
        avgFitness = new Pair<Double, Double>();
        bestFitness = new Pair<Double, Double>();
        time = new Pair<Long, Long>();

        String home = System.getProperty("user.home") + File.separatorChar
                + NbBundle.getMessage(
                FileUtils.class,
                "FOLDER_Home");
        benchmarkFolder = home + File.separatorChar + "benchmark";

        String dataDir = getDataDir("evo");
        (new File(dataDir)).mkdir();
    }

    protected int attributesCount() {
        return dataset.attributeCount();
    }

    protected Dataset<Instance> getDataset() {
        return dataset;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long end;
        ArrayList<Individual> newInds = new ArrayList<Individual>();
        Population pop = new Population(this, populationSize);
        avgFitness.a = pop.getAvgFitness();
        Individual best = pop.getBestIndividual();
        bestFitness.a = best.getFitness();

        System.out.println(pop);

        for (int g = 0; g < generations && !isFinished; g++) {

            // clear collection for new individuals
            newInds.clear();

            // apply crossover operator
            for (int i = 0; i < pop.getIndividuals().length; i++) {
                if (rand.nextDouble() < getCrossoverProbability()) {
                    // take copy of current individual
                    Individual thisOne = pop.getIndividual(i);
                    // take another individual
                    List<? extends Individual> second = pop.selectIndividuals(1);
                    // do crossover
                    List<Individual> ancestors = thisOne.cross(second.get(0));
                    // put childrens to the list of new individuals
                    newInds.addAll(ancestors);                    
                }
            }

            // apply mutate operator
            for (int i = 0; i < pop.getIndividuals().length; i++) {
                Individual thisOne = pop.getIndividual(i).deepCopy();
                thisOne.mutate();
                // put mutated individual to the list of new individuals
                newInds.add(thisOne);
            }

            // count fitness of all changed individuals
            for (int i = 0; i < newInds.size(); i++) {
                newInds.get(i).countFitness();
            }

            // merge new and old individuals
            for (int i = newInds.size(); i < pop.individualsLength(); i++) {
                Individual tmpi = pop.getIndividual(i).deepCopy();
                tmpi.countFitness();
                newInds.add(tmpi);
            }

            // sort them by fitness (thanks to Individual implements interface Comparable)
            Individual[] newIndsArr = newInds.toArray(new Individual[0]);
            Arrays.sort(newIndsArr);

            // and take the better "half" (populationSize)
            System.arraycopy(newIndsArr, 0, pop.getIndividuals(), 0, pop.getIndividuals().length);

            // print statistic
            System.out.println("gen: " + g + "\t bestFit: " + pop.getBestIndividual().getFitness() + "\t avgFit: " + pop.getAvgFitness());
            // for very long evolutions print best individual each 1000 generations
            if (g % debugLimit == 0) {
                best = pop.getBestIndividual();
                System.out.println("best: " + best.toString());
                plotIndividual(g, 1, 2, getDataDir("evo"), best.getClustering());
            }

        }

        System.out.println("best: " + best.toString());
        end = System.currentTimeMillis();
        System.out.println("evolution took " + (end - start) + " ms");
    }

    private void plotIndividual(int n, int x, int y, String dataDir, Clustering<Cluster> clusters) {
        PrintWriter template = null;
        String strn = String.format("%02d", n);
        String dataFile = "data-" + strn + ".csv";
        String scriptFile = "plot-" + strn + ".gpt";

        try {
            PrintWriter writer = new PrintWriter(dataDir + File.separatorChar + dataFile, "UTF-8");
            CSVWriter csv = new CSVWriter(writer, ',');
            toCsv(csv, clusters, dataset);
            writer.close();

            template = new PrintWriter(dataDir + scriptFile, "UTF-8");
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
        template.write(plotTemplate(n, x, y, clusters, dataFile));
        template.close();
    }

    public void toCsv(DatasetWriter writer, Clustering<Cluster> clusters, Dataset<Instance> dataset) {
        String[] header = new String[dataset.attributeCount() + 1];
        header[dataset.attributeCount()] = "label";
        int i = 0;
        for (Attribute ta : dataset.getAttributes().values()) {
            header[i++] = String.valueOf(ta.getName());
        }
        writer.writeNext(header);
        for (Cluster<Instance> clust : clusters) {
            for (Instance inst : clust) {
                writer.writeLine(appendClass(inst, clust.getName()));
            }
        }
    }

    private StringBuilder appendClass(Instance inst, String klass) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < inst.size(); i++) {
            if (i > 0) {
                res.append(',');
            }
            res.append(inst.value(i));
        }
        return res.append(',').append(klass);
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public ClusterEvaluation getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(ClusterEvaluation evaluator) {
        this.evaluator = evaluator;
    }

    private String plotTemplate(int k, int x, int y, Clustering<Cluster> clustering, String dataFile) {
        Cluster<Instance> first = clustering.get(0);
        double fitness = evaluator.score(clustering, getDataset());
        int attrCnt = first.attributeCount();
        int labelPos = attrCnt + 1;
        //attributes are numbered from zero, gnuplot columns from 1
        double max = first.getAttribute(x - 1).statistics(AttrNumStats.MAX);
        double min = first.getAttribute(x - 1).statistics(AttrNumStats.MIN);
        String xrange = "[" + min + ":" + max + "]";
        max = first.getAttribute(y - 1).statistics(AttrNumStats.MAX);
        min = first.getAttribute(y - 1).statistics(AttrNumStats.MIN);
        String yrange = "[" + min + ":" + max + "]";

        double jacc = jaccard.score(clustering, dataset);

        String res = "set datafile separator \",\"\n"
                + "set key outside bottom horizontal box\n"
                + "set title \"generation = " + k + ", fitness = " + fitness + ", jacc = " + jacc + "\"\n"
                + "set xlabel \"" + first.getAttribute(x - 1).getName() + "\" font \"Times,7\"\n"
                + "set ylabel \"" + first.getAttribute(y - 1).getName() + "\" font \"Times,7\"\n"
             //   + "set xtics 0,0.5 nomirror\n"
             //   + "set ytics 0,0.5 nomirror\n"
                + "set mytics 2\n"
                + "set mx2tics 2\n"
              //  + "set xrange " + xrange + "\n"
              //  + "set yrange " + yrange + "\n"
                + "set grid\n"
                + "set pointsize 0.5\n";
        int i = 0;
        int last = clustering.size() - 1;
        PointTypeIterator pti = new PointTypeIterator();
        for (Cluster clust : clustering) {
            if (i == 0) {
                res += "plot ";
            }
            res += "\"< awk -F\\\",\\\" '{if($" + labelPos + " == \\\"" + clust.getName() + "\\\") print}' " + dataFile + "\" u " + x + ":" + y + " t \"" + clust.getName() + "\" w p pt " + pti.next();
            if (i != last) {
                res += ", \\\n";
            } else {
                res += "\n";
            }

            i++;
        }
        return res;
    }

    private String getDataDir(String dir) {
        return createFolder(dir) + "data" + File.separatorChar;
    }

    private String createFolder(String name) {
        String dir = benchmarkFolder + File.separatorChar + name + File.separatorChar;
        boolean success = (new File(dir)).mkdir();
        if (success) {
            System.out.println("Directory: " + dir + " created");
        }
        return dir;
    }
}
