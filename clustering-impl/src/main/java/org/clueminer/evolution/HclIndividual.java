package org.clueminer.evolution;

import org.clueminer.clustering.api.evolution.Individual;
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
public class HclIndividual extends AbstractIndividual<HclIndividual> {

    private double fitness = 0;
    private static Random rand = new Random();
    private double[] weights;
    private AlgorithmParameters params;
    private boolean debug = true;

    public HclIndividual(AttrEvolution evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        init();
    }

    private void init() {
        weights = new double[evolution.attributesCount()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextDouble();
        }
        params = new AlgorithmParameters();
        params.setProperty("name", "HCL");
        // alg type
        params.setProperty("alg-type", "cluster");
        // output class
        params.setProperty("output-class", "single-output");
        params.setProperty("distance-factor", String.valueOf(1));
        params.setProperty("hcl-distance-absolute", String.valueOf(true));

        String standard = "Min-Max";
        params.setProperty("std", standard);
        params.setProperty("fitted-params", String.valueOf(false));


        /**
         * 0 for ALC method, 1 for CLC or -1 otherwise
         */
        int linkage = 1;
        /* if (radioLinkageAverage.isSelected()) {
         linkage = 0;
         } else if (radioLinkageComplete.isSelected()) {
         linkage = 1;
         } else if (radioLinkageSingle.isSelected()) {
         linkage = -1;
         }*/
        params.setProperty("method-linkage", String.valueOf(linkage));

        params.setProperty("calculate-experiments", String.valueOf(false));

        params.setProperty("optimize-rows-ordering", String.valueOf(true));

        //Clustering by Samples
        params.setProperty("calculate-rows", String.valueOf(true));
        //data.addParam("calculate-genes", String.valueOf(false));
        params.setProperty("optimize-cols-ordering", String.valueOf(true));

        params.setProperty("optimize-sample-ordering", String.valueOf(true));

        params.setProperty("cutoff", "BIC score");
        params.setProperty("log-scale", String.valueOf(false));

    }

    @Override
    public void countFitness() {

        DistanceFactory df = DistanceFactory.getDefault();
        AbstractDistance func = df.getProvider("Euclidean");
        algorithm.setDistanceFunction(func);

        long start = System.currentTimeMillis();


        Matrix input = Scaler.standartize(evolution.getDataset().arrayCopy(), params.getString("std"), params.getBoolean("log-scale"));

        System.out.println("input matrix");
        input.print(5, 2);


        //   progress.setTitle("Clustering by rows");
        params.setProperty("calculate-rows", String.valueOf(true));
        HierarchicalResult rowsResult = algorithm.hierarchy(input, evolution.getDataset(), params);
        Dump.array(rowsResult.getMapping(), "row mapping: ");


        //   progress.setTitle("Clustering by columns");
        //  params.setProperty("calculate-rows", String.valueOf(false));
        //  HierarchicalResult columnsResult = algorithm.hierarchy(input, evolution.getDataset(), params);
        // validate(columnsResult);

        //System.out.println("params: " + params.toString());
       //printResult(rowsResult);

        if (debug) {
            Matrix proximity = rowsResult.getProximityMatrix();
            //    Matrix cprox = columnsResult.getProximityMatrix();
            System.out.println("row proximity matrix:");
            proximity.print(5, 2);
            System.out.println("columns proximity matrix:");
            //  cprox.print(5, 2);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("clustering took " + time + " ms");


      //  double cutoff = rowsResult.findCutoff();
      //  System.out.println("rows tree cutoff = " + cutoff);

        //   cutoff = columnsResult.findCutoff();
        //   System.out.println("columns tree cutoff = " + cutoff);

        String cutoffAlg = params.getString("cutoff");

       // if (!cutoffAlg.equals("-- naive --")) {
            ClusterEvaluator eval = ClusterEvaluatorFactory.getDefault().getProvider(cutoffAlg);
            NaiveCutoff strategy = new NaiveCutoff();
            rowsResult.findCutoff(strategy);
       // }// else we use a naive approach

        Clustering clust = rowsResult.getClustering();



        String linkage = null;
        switch (params.getInt("method-linkage")) {
            case -1:
                linkage = "single";
                break;
            case 1:
                linkage = "complete";
                break;
            case 0:
                linkage = "average";
                break;
        }

        double s;
        StringBuilder scores = new StringBuilder();
        StringBuilder evaluators = new StringBuilder();

        evaluators.append("Distance function").append("\t");
        scores.append(algorithm.getDistanceFunction().getName()).append("\t");

        evaluators.append("Standartization").append("\t");
        scores.append(params.getString("std")).append("\t");

        evaluators.append("Linkage").append("\t");
        scores.append(linkage).append("\t");

        evaluators.append("Number of clusters").append("\t");
        scores.append(rowsResult.getNumClusters()).append("\t");

        evaluators.append("Cutoff").append("\t");
        scores.append(cutoffAlg).append("\t");

        HierarchicalClusterEvaluator cophenetic = new CopheneticCorrelation();
        evaluators.append("CPCC").append("\t");
        scores.append(cophenetic.score(rowsResult)).append("\t");

        System.out.println(evaluators);
        System.out.println(scores);



        fitness = evolution.evaluator.score(clust, evolution.getDataset());
        System.out.println("fitness = " + fitness);
    }

    @Override
    public double getFitness() {
        return fitness;
    }


    @Override
    public void mutate() {
        for (int i = 0; i < weights.length; i++) {
            if (rand.nextDouble() < evolution.mutationProbability) {
                weights[i] += rand.nextDouble();
            }
        }
    }

  /*  public List<Individual> cross(WeightsIndividual i) {
        ArrayList<Individual> offsprings = new ArrayList<Individual>();
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

    public HclIndividual deepCopy() {
        WeightsIndividual newOne = new WeightsIndividual(evolution);
        newOne.weights = new double[this.weights.length];
        System.arraycopy(this.weights, 0, newOne.weights, 0, this.weights.length);
        newOne.fitness = this.fitness;
        return newOne;
    }*/


    @Override
    public Clustering<Cluster> getClustering() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<HclIndividual> cross(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCompatible(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HclIndividual duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HclIndividual deepCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
