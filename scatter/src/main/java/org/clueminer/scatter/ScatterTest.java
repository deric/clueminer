package org.clueminer.scatter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.fixtures.clustering.FakeClustering;

/**
 *
 * @author deric
 */
public class ScatterTest extends JPanel {

    private ScatterPlot plot;

    public ScatterTest() {
        setPreferredSize(new Dimension(800, 600));
        initComponents();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ScatterTest().showInFrame();
            }
        });
    }

    public String getTitle() {
        return "Scatter test";
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 500));
        //frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        plot = new ScatterPlot();
        Clustering clusters = FakeClustering.iris();
        plot.setClustering(clusters);
        add(plot);
    }

}
