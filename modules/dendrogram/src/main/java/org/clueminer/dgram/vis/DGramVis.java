package org.clueminer.dgram.vis;

import java.awt.Image;
import java.util.logging.Logger;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.gui.Heatmap;
import org.clueminer.dgram.eval.MOLO;
import org.clueminer.math.Matrix;
import org.clueminer.std.Scaler;
import org.clueminer.utils.Props;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;

/**
 *
 * Generates thumbnail of a heat-map
 *
 * @author Tomas Barton
 */
public class DGramVis {

    private static final Logger log = Logger.getLogger(DGramVis.class.getName());
    private static final RequestProcessor RP = new RequestProcessor("Clustering");

    public static Image generate(final Clustering<? extends Cluster> clustering, final int width, final int height, final DendrogramVisualizationListener listener) {
        final DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        if (mapping == null) {
            log.warning("missing mapping, running clustering");
            RP.post(new Runnable() {
                @Override
                public void run() {
                    //add empty mapping
                    DendrogramMapping map = new DendrogramData();
                    clustering.lookupAdd(map);
                    createMapping(clustering);
                    generateImage(clustering, width, height, listener, map);
                }
            });
            return ImageUtilities.loadImage("org/clueminer/dendrogram/gui/spinner.gif", false);

        } else {
            if (!mapping.hasRowsClustering()) {
                //computing still in progress
                return ImageUtilities.loadImage("org/clueminer/dendrogram/gui/spinner.gif", false);
            }
            if (!mapping.hasColumnsClustering()) {
                RP.post(new Runnable() {
                    @Override
                    public void run() {
                        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
                        Props params = clustering.getParams();
                        AgglomerativeClustering algorithm = new HAC();

                        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get("std", Scaler.NONE), params.getBoolean("log-scale", false));
                        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
                        HierarchicalResult colsResult = algorithm.hierarchy(input, dataset, params);
                        mapping.setColsResult(colsResult);
                        mapping.setDataset(dataset);
                        generateImage(clustering, width, height, listener, mapping);
                    }
                });

            }
            return generateImage(clustering, width, height, listener, mapping);
        }

        /**
         * TODO implement structure generation
         */
        /*       DgViewer viewer = new DgViewer();
         viewer.setDataset(null);
         viewer.setClustering(clustering);*/
    }

    private static Image generateImage(final Clustering<? extends Cluster> clustering, final int width, final int height, final DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        Heatmap heatmap = new Heatmap();
        heatmap.setData(mapping);
        Image img = heatmap.generate(width, height);
        if (listener != null) {
            listener.clusteringFinished(clustering);
            listener.previewUpdated(img);
        }
        return img;
    }

    private static DendrogramMapping createMapping(Clustering<? extends Cluster> clustering) {
        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
        Props params = clustering.getParams();
        AgglomerativeClustering algorithm = new HAC();

        Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get("std", Scaler.NONE), params.getBoolean("log-scale", false));

        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(input, dataset, params);
        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult colsResult = algorithm.hierarchy(input, dataset, params);

        DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        mapping.setDataset(dataset);
        mapping.setMatrix(input);

        OptimalTreeOrder treeOrder = new MOLO();
        treeOrder.optimize(rowsResult, true);
        treeOrder.optimize(colsResult, true);

        mapping.setRowsResult(rowsResult);
        mapping.setColsResult(colsResult);
        clustering.lookupAdd(mapping);

        return mapping;
    }

}
