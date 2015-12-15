package org.clueminer.dgram.vis;

import java.awt.Image;
import java.util.logging.Logger;
import org.clueminer.clustering.aggl.HC;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.api.dendrogram.OptimalTreeOrder;
import org.clueminer.clustering.order.MOLO;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;

/**
 *
 * Generates thumbnail of a heat-map
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class DGramVis<E extends Instance, C extends Cluster<E>> {

    private static final Logger log = Logger.getLogger(DGramVis.class.getName());
    private static final RequestProcessor RP = new RequestProcessor("Clustering");

    public Image generate(final Clustering<E, C> clustering, final int width, final int height, final DendrogramVisualizationListener listener) {
        final DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        if (mapping == null) {
            log.warning("missing mapping, can't generate preview");
            //generating clustering does not help
            //TODO return error image
            return loading();
        } else {
            //don't generate columns mapping
            /*
             if (!mapping.hasColumnsClustering()) {
             RP.post(new Runnable() {
             @Override
             public void run() {
             Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
             Props params = clustering.getParams();
             AgglomerativeClustering algorithm = new HC();

             Matrix input = Scaler.standartize(dataset.arrayCopy(), params.get("std", Scaler.NONE), params.getBoolean("log-scale", false));
             params.put(AgglParams.CLUSTERING_TYPE, false);
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
        }

        /**
         * TODO implement structure generation
         */
        /*       DgViewer viewer = new DgViewer();
         viewer.setDataset(null);
         viewer.setClustering(clustering);*/
    }

    private DendrogramMapping createMapping(Clustering<E, C> clustering) {
        Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
        Props params = clustering.getParams();
        AgglomerativeClustering algorithm = new HC();

        params.putBoolean(AgglParams.CLUSTERING_TYPE, true);
        HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);
        params.putBoolean(AgglParams.CLUSTERING_TYPE, false);
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
