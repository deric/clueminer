package org.clueminer.dgram.vis;

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.aggl.HCLW;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.clustering.struct.DendroMatrixData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class IconDemo<E extends Instance, C extends Cluster<E>> extends JFrame implements DendrogramVisualizationListener<E, C> {

    private static final long serialVersionUID = -8493251992060371012L;
    private final JLabel picLabel;

    public IconDemo() {
        setLayout(new BorderLayout());
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        Matrix input = new JMatrix(dataset.arrayCopy());

        Props params = new Props();
        AgglomerativeClustering algorithm = new HCLW();
        HierarchicalResult rowsResult = algorithm.hierarchy(dataset, params);

        params.put(AgglParams.CLUSTERING_TYPE, ClusteringType.COLUMNS_CLUSTERING);
        HierarchicalResult columnsResult = algorithm.hierarchy(dataset, params);

        DendroMatrixData dendroData = new DendroMatrixData(dataset, input, rowsResult, columnsResult);

        Clustering<E, C> clustering = FakeClustering.iris();
        clustering.lookupAdd(dendroData);

        DGramVis<E, C> dgram = new DGramVis();
        Image image = dgram.generate(clustering, 300, 300, this);
        picLabel = new JLabel(new ImageIcon(image));

        add(picLabel);

    }

    // this function will be run from the EDT
    private static void createAndShowGUI() throws Exception {
        IconDemo frame = new IconDemo();
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
