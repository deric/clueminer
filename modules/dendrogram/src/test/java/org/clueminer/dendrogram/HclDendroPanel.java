package org.clueminer.dendrogram;

import java.util.List;
import java.util.Map;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.DgViewer;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.distance.api.DistanceMeasure;
import org.clueminer.eval.hclust.HillClimbCutoff;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Dump;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class HclDendroPanel extends DendroPanel {

    private static final long serialVersionUID = -5113017275195427868L;
    private Props params;
    private boolean debug = false;
    private DataProvider dataProvider;

    public HclDendroPanel(Map<String, Dataset<? extends Instance>> data) {
        this(new DataProvider(data));
    }

    public HclDendroPanel(DataProvider provider) {
        dataProvider = provider;
        setDataset(dataProvider.first());
        options.setDatasets(dataProvider.getDatasetNames());
        params = new Props();
        params.put("name", "hierarchical clustering");
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
    public DendrogramData execute() {

        DistanceFactory df = DistanceFactory.getInstance();
        DistanceMeasure func = df.getProvider("Euclidean");
        if (algorithm == null) {
            throw new RuntimeException("no algorithm was set");
        }
        algorithm.setDistanceFunction(func);

        Matrix input = Scaler.standartize(getDataset().arrayCopy(), params.get("std", "Maximum"), params.getBoolean("log-scale", false));
        if (debug) {
            System.out.println("input matrix");
            input.print(5, 2);
        }

        long start = System.currentTimeMillis();

        //   progress.setTitle("Clustering by rows");
        params.putBoolean("calculate-rows", true);
        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(getDataset(), params);
        Dump.array(rowsResult.getMapping(), "row mapping: ");

        //   progress.setTitle("Clustering by columns");
        params.putBoolean("calculate-rows", false);
        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult columnsResult = algorithm.hierarchy(getDataset(), params);
        Dump.array(columnsResult.getMapping(), "col mapping: ");
        // validate(columnsResult);

        //System.out.println("params: " + params.toString());
        //printResult(rowsResult);
        long time = System.currentTimeMillis() - start;
        System.out.println(algorithm.getName() + " clustering took " + time + " ms");

        double cutoff = rowsResult.findCutoff();
        //   double cutoff = rowsResult.findCutoff();
        //   System.out.println("rows tree cutoff = " + cutoff);
        //    cutoff = columnsResult.findCutoff();
        //     System.out.println("columns tree cutoff = " + cutoff);
        DendrogramData dendroData = new DendrogramData(getDataset(), input, rowsResult, columnsResult);
        viewer.setDataset(dendroData);
        String cutoffAlg = params.get("cutoff", "-- naive --");
        Clustering clust;
        if (!cutoffAlg.equals("-- naive --")) {
            InternalEvaluator eval = InternalEvaluatorFactory.getInstance().getProvider(cutoffAlg);
            HillClimbCutoff strategy = new HillClimbCutoff(eval);
            rowsResult.findCutoff(strategy);
        }// else we use a naive approach

        if (debug) {
            dendroData.printMappedMatix(2);
        }

        clust = dendroData.getRowsClustering();
        //associate dendroData with clustering
        clust.lookupAdd(dendroData);
        clust.setParams(params);
        System.out.println("result clust size " + clust.size());
        List<InternalEvaluator> list = InternalEvaluatorFactory.getInstance().getAll();

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
        /*     StringBuilder scores = new StringBuilder();
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

         for (InternalEvaluator c : list) {
         s = c.score(clust, getDataset());
         scores.append(s).append("\t");
         evaluators.append(c.getName()).append("\t");
         }

         HierarchicalClusterEvaluator cophenetic = new CopheneticCorrelation();
         evaluators.append("CPCC").append("\t");
         scores.append(cophenetic.score(rowsResult)).append("\t");

         System.out.println(evaluators);
         System.out.println(scores);*/
        repaint();
        return dendroData;
    }

    @Override
    public void initViewer() {
        viewer = new DgViewer();
    }

    @Override
    public void dataChanged(String datasetName) {
        setDataset(dataProvider.getDataset(datasetName));
        System.out.println("dataset changed to " + datasetName + ": " + System.identityHashCode(this));
        if (algorithm != null) {
            execute();
        }
    }

    @Override
    public String[] getDatasets() {
        return dataProvider.getDatasetNames();
    }

    public void setDataProvider(DataProvider provider) {
        this.dataProvider = provider;
    }

    @Override
    public void linkageChanged(String linkage) {
        int res;
        switch (linkage) {
            case "Single Linkage":
                res = -1;
                break;
            case "Complete Linkage":
                res = 1;
                break;
            case "Average Linkage":
                res = 0;
                break;
            default:
                throw new RuntimeException("linkage " + linkage + " is not supported");
        }
        params.putInt("method-linkage", res);
        if (algorithm != null) {
            execute();
        }
    }
}
