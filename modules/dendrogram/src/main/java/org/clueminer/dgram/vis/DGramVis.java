package org.clueminer.dgram.vis;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dendrogram.gui.Heatmap;
import org.clueminer.clustering.order.MOLO;
import org.clueminer.dgram.eval.SilhouettePlot;
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
            log.warning("missing mapping, can't generate preview");
            //generating clustering does not help
            /* log.warning("missing mapping, running clustering");
             RP.post(new Runnable() {
             @Override
             public void run() {
             //add empty mapping
             DendrogramMapping map = new DendrogramData2();
             clustering.lookupAdd(map);
             createMapping(clustering);
             generateImage(clustering, width, height, listener, map);
             }
             });*/
            return loading();

        } else {
            if (!mapping.hasRowsClustering()) {
                //computing still in progress
                return loading();
            }

            //don't generate columns mapping
            /*
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

             }*/
            //add task to queue
            ImageFactory.getInstance().generateImage(clustering, width, height, listener, mapping);

            return loading();
            //return generateImage(clustering, width, height, listener, mapping);
        }

        /**
         * TODO implement structure generation
         */
        /*       DgViewer viewer = new DgViewer();
         viewer.setDataset(null);
         viewer.setClustering(clustering);*/
    }

    /**
     *
     * @param clustering
     * @param width
     * @param height
     * @param listener
     * @param mapping
     * @return
     * @deprecated
     */
    public static Image generateImage(final Clustering<? extends Cluster> clustering, final int width, final int height, final DendrogramVisualizationListener listener, DendrogramMapping mapping) {
        Heatmap heatmap = new Heatmap();
        SilhouettePlot silhoulette = new SilhouettePlot(true);

        heatmap.setData(mapping);
        silhoulette.setClustering(clustering);

        int silWidth = (int) (0.3 * width);
        int dendroWidth = width - silWidth;
        Image img = heatmap.generate(dendroWidth, height);

        Image imgSil = silhoulette.generate(silWidth, height);
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.drawImage(imgSil, dendroWidth, 0, null);

        if (listener != null) {
            listener.clusteringFinished(clustering);
            listener.previewUpdated(combined);
        }
        return combined;
    }

    private static DendrogramMapping createMapping(Clustering<? extends Cluster> clustering) {
        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
        Props params = clustering.getParams();
        AgglomerativeClustering algorithm = new HAC();

        params.putBoolean(AgglParams.CLUSTER_ROWS, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);
        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult colsResult = algorithm.hierarchy(dataset, params);

        DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        mapping.setDataset(dataset);

        OptimalTreeOrder treeOrder = new MOLO();
        treeOrder.optimize(rowsResult, true);
        treeOrder.optimize(colsResult, true);
        mapping.setRowsResult(rowsResult);
        mapping.setColsResult(colsResult);
        clustering.lookupAdd(mapping);

        return mapping;
    }

    /**
     * Loading icon, should be displayed when we are computing real image
     *
     * @return
     */
    public static Image loading() {
        return ImageUtilities.loadImage("org/clueminer/dendrogram/gui/spinner.gif", false);
    }

}
