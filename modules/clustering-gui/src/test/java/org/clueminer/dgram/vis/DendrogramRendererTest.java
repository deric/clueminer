/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dgram.vis;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.struct.DendroMatrixData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class DendrogramRendererTest<E extends Instance, C extends Cluster<E>> extends JFrame implements DendrogramVisualizationListener<E, C> {

    private static final long serialVersionUID = -8493251992060371012L;
    private JLabel picLabel;

    public DendrogramRendererTest() throws TimeoutException {
        setLayout(new BorderLayout());
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        Matrix input = new JMatrix(dataset.arrayCopy());

        Props params = new Props();
        AgglomerativeClustering algorithm = new HCLW();
        HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);

        params.put(AlgParams.CLUSTERING_TYPE, ClusteringType.COLUMNS_CLUSTERING);
        HierarchicalResult columnsResult = algorithm.hierarchy(dataset, params);

        DendroMatrixData dendroData = new DendroMatrixData(dataset, input, rowsResult, columnsResult);

        Clustering<E, C> clustering = FakeClustering.iris();
        clustering.lookupAdd(dendroData);

        Props prop = new Props();
        prop.put(PropType.VISUAL, "img_width", 600);
        prop.put(PropType.VISUAL, "img_height", 600);
        prop.put(PropType.VISUAL, "visualization", "Dendrogram");
        final DendrogramMapping mapping = clustering.getLookup().lookup(DendrogramMapping.class);
        Future<? extends Image> future = ImageFactory.getInstance().generateImage(clustering, prop, this, mapping);
        Image image;
        try {
            image = future.get(3, TimeUnit.SECONDS);
            picLabel = new JLabel(new ImageIcon(image));
            add(picLabel);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        DendrogramRendererTest frame = new DendrogramRendererTest();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    System.err.println(e);
                    Exceptions.printStackTrace(e);
                }
            }
        });
    }

    @Override
    public void previewUpdated(final Image preview) {
        System.out.println("updating preview");
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                picLabel.setIcon(new ImageIcon(preview));
                validate();
                revalidate();
                repaint();
            }
        });
    }

    @Override
    public void clusteringFinished(Clustering<E, C> clustering) {
        //nothing to do
    }

}
