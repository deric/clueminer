package org.clueminer.dendrogram;

import java.util.Map;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.DgViewer;
import org.clueminer.distance.api.Distance;
import org.clueminer.distance.api.DistanceFactory;
import org.clueminer.report.MemInfo;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class HacDendroPanel extends DendroPanel {

    private static final long serialVersionUID = 9048811568987758622L;

    private boolean debug = false;
    private DataProviderMap dataProvider;
    private Executor exec;

    public HacDendroPanel(Map<String, Dataset<? extends Instance>> data) {
        this(new DataProviderMap(data));
    }

    public HacDendroPanel(DataProviderMap provider) {
        dataProvider = provider;
        setDataset(dataProvider.first());
        options.setDatasets(dataProvider.getDatasetNames());
        exec = new ClusteringExecutorCached();
    }

    @Override
    public void initViewer() {
        viewer = new DgViewer();
    }

    @Override
    public DendrogramMapping execute() {
        Props params = getProperties();
        if (params == null) {
            params = new Props();
            params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        }
        return execute(params.copy());
    }

    public DendrogramMapping execute(Props params) {
        MemInfo memInfo = new MemInfo();

        DistanceFactory df = DistanceFactory.getInstance();
        Distance func = df.getProvider("Euclidean");
        if (algorithm == null) {
            throw new RuntimeException("no algorithm was set");
        }
        params.put("name", getAlgorithm().getName());
        algorithm.setDistanceFunction(func);

        exec.setAlgorithm(algorithm);
        DendrogramMapping dendroData;
        if (params.getObject(AgglParams.CLUSTERING_TYPE) == ClusteringType.ROWS_CLUSTERING) {
            HierarchicalResult res = exec.hclustRows(getDataset(), params);
            dendroData = new DendrogramData(getDataset(), res);
        } else {
            dendroData = exec.clusterAll(getDataset(), params);
        }

        memInfo.report();

        viewer.setDataset(dendroData);

        validate();
        revalidate();
        repaint();
        return dendroData;
    }

    @Override
    public void dataChanged(String datasetName) {
        setDataset(dataProvider.getDataset(datasetName));
        System.out.println("dataset changed to " + datasetName + ": " + System.identityHashCode(getDataset()));
        if (algorithm != null) {
            execute();
        }
    }

    @Override
    public String[] getDatasets() {
        return dataProvider.getDatasetNames();
    }

    @Override
    public void linkageChanged(String linkage) {
        Props params = getProperties().copy();
        params.put(AgglParams.LINKAGE, linkage);
        execute(params);
    }

    @Override
    public void cutoffChanged(String cutoff) {
        Props params = getProperties().copy();
        params.put(AgglParams.CUTOFF_STRATEGY, cutoff);
        execute(params);
    }

}
