package org.clueminer.dgram.vis;

import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;

/**
 *
 * @author deric
 */
public class IconDemo extends JFrame {

    private static final long serialVersionUID = -8493251992060371012L;

    public IconDemo() {
        setLayout(new BorderLayout());

        Clustering<? extends Cluster> clustering = FakeClustering.iris();

        Visualization vis = new Visualization();
        Image image = vis.generate(clustering);

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
