package org.clueminer.dendrogram;

import java.util.List;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalClusterEvaluator;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dgram.DgViewer;
import org.clueminer.distance.api.AbstractDistance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.evaluation.hclust.CopheneticCorrelation;
import org.clueminer.hclust.HillClimbCutoff;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Dump;
import org.openide.util.NbPreferences;

/**
 *
 * @author Tomas Barton
 */
public class HclDendroPanel extends DendroPanel {

    private static final long serialVersionUID = -5113017275195427868L;
    private Preferences params;
    private boolean debug = true;

    public HclDendroPanel() {
        params = NbPreferences.forModule(HclDendroPanel.class);
        params.put("name", "HCL");
        // alg type
        params.put("alg-type", "cluster");
        // output class
        params.put("output-class", "single-output");
        params.putInt("distance-factor", 1);
        params.putBoolean("hcl-distance-absolute", true);

        String standard = "Maximum";
        params.put("std", standard);
        params.putBoolean("fitted-params", false);

        /**
         * 0 for ALC method, 1 for CLC or -1 otherwise
         */
        int linkage = -1;
        /* if (radioLinkageAverage.isSelected()) {
         linkage = 0;
         } else if (radioLinkageComplete.isSelected()) {
         linkage = 1;
         } else if (radioLinkageSingle.isSelected()) {
         linkage = -1;
         }*/
        params.putInt("method-linkage", linkage);

        params.putBoolean("calculate-experiments", true);

        params.putBoolean("optimize-rows-ordering", true);

        //Clustering by Samples
        params.putBoolean("calculate-rows", true);
        //data.addParam("calculate-genes", false);
        params.putBoolean("optimize-cols-ordering", true);

        params.putBoolean("optimize-sample-ordering", true);

        params.put("cutoff", "-- naive --");
        params.putBoolean("log-scale", false);

    }

    @Override
    public HierarchicalResult execute() {

        DistanceFactory df = DistanceFactory.getInstance();
        AbstractDistance func = df.getProvider("Euclidean");
        if (algorithm == null) {
            throw new RuntimeException("no algorithm was set");
        }
        algorithm.setDistanceFunction(func);

        long start = System.currentTimeMillis();

        Matrix input = Scaler.standartize(getDataset().arrayCopy(), params.get("std", "Maximum"), params.getBoolean("log-scale", false));
        if (debug) {
            System.out.println("input matrix");
            input.print(5, 2);
        }

        //   progress.setTitle("Clustering by rows");
        params.putBoolean("calculate-rows", true);
        HierarchicalResult rowsResult = algorithm.hierarchy(input, getDataset(), params);
        Dump.array(rowsResult.getMapping(), "row mapping: ");

        //   progress.setTitle("Clustering by columns");
        params.putBoolean("calculate-rows", false);
        HierarchicalResult columnsResult = algorithm.hierarchy(input, getDataset(), params);
        // validate(columnsResult);

        //System.out.println("params: " + params.toString());
        //printResult(rowsResult);
        if (debug) {
            Matrix proximity = rowsResult.getProximityMatrix();
            Matrix cprox = columnsResult.getProximityMatrix();
            System.out.println("row proximity matrix:");
            proximity.print(5, 2);
            System.out.println("columns proximity matrix:");
            cprox.print(5, 2);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("clustering took " + time + " ms");

        //   double cutoff = rowsResult.findCutoff();
        //   System.out.println("rows tree cutoff = " + cutoff);
        //    cutoff = columnsResult.findCutoff();
        //     System.out.println("columns tree cutoff = " + cutoff);
        DendrogramData dendroData = new DendrogramData(getDataset(), input, rowsResult, columnsResult);
        viewer.setDataset(dendroData);
        String cutoffAlg = params.get("cutoff", "-- naive --");
        Clustering clust;
        if (!cutoffAlg.equals("-- naive --")) {
            ClusterEvaluator eval = InternalEvaluatorFactory.getInstance().getProvider(cutoffAlg);
            HillClimbCutoff strategy = new HillClimbCutoff(eval);
            rowsResult.findCutoff(strategy);
        }// else we use a naive approach

        clust = dendroData.getRowsClustering();
        //associate dendroData with clustering
        clust.lookupAdd(dendroData);
        clust.setParams(params);
        System.out.println("result clust size " + clust.size());
        List<ClusterEvaluator> list = InternalEvaluatorFactory.getInstance().getAll();

        String linkage = null;
        switch (params.getInt("method-linkage", 1)) {
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
        scores.append(params.get("std", "Maximum")).append("\t");

        evaluators.append("Linkage").append("\t");
        scores.append(linkage).append("\t");

        evaluators.append("Number of clusters").append("\t");
        scores.append(rowsResult.getNumClusters()).append("\t");

        evaluators.append("Cutoff").append("\t");
        scores.append(cutoffAlg).append("\t");

        for (ClusterEvaluator c : list) {
            s = c.score(clust, getDataset());
            scores.append(s).append("\t");
            evaluators.append(c.getName()).append("\t");
        }

        HierarchicalClusterEvaluator cophenetic = new CopheneticCorrelation();
        evaluators.append("CPCC").append("\t");
        scores.append(cophenetic.score(rowsResult)).append("\t");

        System.out.println(evaluators);
        System.out.println(scores);
        repaint();
        return rowsResult;
    }

    @Override
    public void initViewer() {
        viewer = new DgViewer();
    }
}
