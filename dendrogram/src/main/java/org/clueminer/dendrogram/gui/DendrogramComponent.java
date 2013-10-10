package org.clueminer.dendrogram.gui;

import org.clueminer.dendrogram.DendrogramViewer;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import org.clueminer.clustering.algorithm.HCL;
import org.clueminer.clustering.api.*;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.clueminer.clustering.gui.ClusteringProperties;
import org.clueminer.clustering.gui.ClusteringToolbar;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.evaluation.hclust.CopheneticCorrelation;
import org.clueminer.hclust.HillClimbCutoff;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.AlgorithmParameters;
import org.clueminer.utils.Exportable;
import org.openide.util.Task;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ClusterAnalysis.class)
public class DendrogramComponent extends ClusterAnalysis {

    private static final long serialVersionUID = -5368065138749492905L;
    private DendrogramViewer viewer;
    private ClusteringAlgorithm algorithm;
    //original dataset
    private Dataset<? extends Instance> dataset;
    private ClusteringToolbar toolbar;
    private ClusteringProperties properities;
    private SettingsPanel panel;
    private boolean debug = false;
    private HierarchicalResult finalResult;

    public DendrogramComponent() {
        //default algorithm
        algorithm = new HCL();
        initComponents();


        /*   TopComponent comp = Lookup.getDefault().lookup(RTCAProjectFrame.class);
         if (comp != null) {
         System.out.println("top compoent Project frame should listen to cluster changes");
         viewer.addClusteringListener((ClusteringListener)comp);
         }else{
         System.out.println("failed to find ProjectFrame top component");
         }*/
    }

    @Override
    public String getName() {
        return "DendrogramComponent";
    }

    public DendrogramViewer getViewer() {
        return viewer;
    }

    private void initComponents() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints c = new GridBagConstraints();

        properities = new ClusteringProperties();
        toolbar = new ClusteringToolbar(this);
        viewer = new DendrogramViewer();
        panel = new SettingsPanel(viewer);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = c.weighty = 0.0; //no fill while resize
        gbl.setConstraints(toolbar, c);
        this.add(toolbar, c);

        c.gridx = 0;
        c.gridy = 1;
        gbl.setConstraints(panel, c);
        this.add(panel, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weightx = c.weighty = 8.0; //ratio for filling the frame space
        gbl.setConstraints(viewer, c);
        this.add(viewer, c);
        setVisible(true);
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    @Override
    public boolean hasDataset() {
        return dataset != null;
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public Matrix standartize(Dataset<? extends Instance> data, String method, boolean logScale) {
        return Scaler.standartize(data.arrayCopy(), method, logScale);
    }

    @Override
    public void execute(AlgorithmParameters params) {
        //      Listener listener = new Listener();
        // AlgorithmParameters params = data.getParams();
        // Experiment experiment = data.getExperiment();
        // input.print(5, 2);

        //AlgorithmFactory factory = framework.getAlgorithmFactory();

        //algorithm.addAlgorithmListener(new Listener());

        long start = System.currentTimeMillis();
        Dataset<? extends Instance> data;
        String datasetTransform = params.getString("dataset");
        System.out.println("using: " + datasetTransform);
        if (datasetTransform.equals("-- no transformation --")) {
            data = this.dataset;
            System.out.println("data: " + data.size());
        } else {
            data = dataset.getChild(datasetTransform);
            if (data == null) {
                throw new RuntimeException("dataset is not available yet");
            }
        }

        Matrix input = standartize(data, params.getString("std"), params.getBoolean("log-scale"));
        if (debug) {
            System.out.println("input matrix");
            input.print(5, 2);
        }

        //   progress.setTitle("Clustering by rows");
        params.setProperty("calculate-rows", String.valueOf(true));
        HierarchicalResult rowsResult = algorithm.hierarchy(input, data, params);


        //   progress.setTitle("Clustering by columns");
        params.setProperty("calculate-rows", String.valueOf(false));
        HierarchicalResult columnsResult = algorithm.hierarchy(input, data, params);
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


        /*    double cutoff = rowsResult.findCutoff();
         System.out.println("rows tree cutoff = " + cutoff);

         cutoff = columnsResult.findCutoff();
         System.out.println("columns tree cutoff = " + cutoff);*/

        DendrogramData dendroData = new DendrogramData(data, input, rowsResult, columnsResult);
        viewer.setDataset(dendroData);
        String cutoffAlg = params.getString("cutoff");
        Clustering clust;
        if (!cutoffAlg.equals("-- naive --")) {
            ClusterEvaluator eval = ClusterEvaluatorFactory.getDefault().getProvider(cutoffAlg);
            HillClimbCutoff strategy = new HillClimbCutoff(eval);
            rowsResult.findCutoff(strategy);
        }// else we use a naive approach

        clust = dendroData.getRowsClustering();
        System.out.println("result clust size " + clust.size());
        List<ClusterEvaluator> list = ClusterEvaluatorFactory.getDefault().getAll();

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

        for (ClusterEvaluator c : list) {
            s = c.score(clust, data);
            scores.append(s).append("\t");
            evaluators.append(c.getName()).append("\t");
        }

        HierarchicalClusterEvaluator cophenetic = new CopheneticCorrelation();
        evaluators.append("CPCC").append("\t");
        scores.append(cophenetic.score(rowsResult)).append("\t");

        System.out.println(evaluators);
        System.out.println(scores);
        finalResult = rowsResult;
        repaint();
    }

    @Override
    public Exportable getMainPanel() {
        return viewer;
    }

    public void updateLayout() {
    }

    /*
     * private Lookup.Result result = null;
     *
     * @Override public void componentOpened() { result =
     * Utilities.actionsGlobalContext().lookupResult(TabPanel.class);
     * result.addLookupListener (this); }
     *
     * @Override public void componentClosed() { result.removeLookupListener
     * (this); result = null; }
     *
     *
     * @Override public void resultChanged(LookupEvent le) {
     * System.out.println("lookup event "+le.toString()); }
     */
    @Override
    public void zoomIn() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void zoomOut() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ClusteringProperties getClusteringProperties() {
        return properities;
    }

    @Override
    public ClusteringAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(ClusteringAlgorithm alg) {
        this.algorithm = alg;
    }

    @Override
    public void addRowsTreeListener(TreeListener listener) {
        viewer.addRowsTreeListener(listener);
    }

    public void addClusteringListener(ClusteringListener listener) {
        viewer.addClusteringListener(listener);
    }

    public void removeClusteringListener(ClusteringListener listener) {
        viewer.removeClusteringListener(listener);
    }

    @Override
    public void taskFinished(Task task) {
        //notify other components
        viewer.fireResultUpdate(finalResult);
    }
}
