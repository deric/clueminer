package org.clueminer.dgram.vis;

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.aggl.HAC;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.struct.DendrogramData;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.fixtures.clustering.FakeClustering;
import org.clueminer.fixtures.clustering.FakeDatasets;
import org.clueminer.math.Matrix;
import org.clueminer.math.matrix.JMatrix;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class IconDemo extends JFrame {

    private static final long serialVersionUID = -8493251992060371012L;

    public IconDemo() {
        setLayout(new BorderLayout());
        Dataset<? extends Instance> dataset = FakeDatasets.irisDataset();
        Matrix input = new JMatrix(dataset.arrayCopy());

        Props params = new Props();
        AgglomerativeClustering algorithm = new HAC();
        HierarchicalResult rowsResult = algorithm.hierarchy(input, dataset, params);

        params.putBoolean(AgglParams.CLUSTER_ROWS, false);
        HierarchicalResult columnsResult = algorithm.hierarchy(input, dataset, params);

        DendrogramData dendroData = new DendrogramData(dataset, input, rowsResult, columnsResult);

        Clustering<? extends Cluster> clustering = FakeClustering.iris();
        clustering.lookupAdd(dendroData);

        Image image = DGramVis.generate(clustering, 300, 300, null);

        JLabel picLabel = new JLabel(new ImageIcon(image));

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
                    e.printStackTrace();
                }
            }
        });
    }

}
