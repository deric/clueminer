package org.clueminer.scatter;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

public class ScatterPlot extends JPanel {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = -412699430625953887L;

    private static final int SAMPLE_COUNT = 100000;
    /**
     * Instance to generate random data values.
     */
    private static final Random random = new Random();

    public ScatterPlot() {
        setPreferredSize(new Dimension(800, 600));
        // Generate 100,000 data points
        DataTable data = new DataTable(Double.class, Double.class);
        for (int i = 0; i <= SAMPLE_COUNT; i++) {
            data.add(random.nextGaussian() * 2.0, random.nextGaussian() * 2.0);
        }

        // Create a new xy-plot
        XYPlot plot = new XYPlot(data);

        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText("scatter");

        // Format points
        plot.getPointRenderer(data).setColor(Color.RED);

        // Add plot to Swing component
        add(new InteractivePanel(plot), BorderLayout.CENTER);
    }

    public void setClustering(Clustering<Cluster> clustering) {
        /*   removeAll();

         // Create a new xy-plot
         XYPlot plot = new XYPlot();

         for (Cluster<Instance> clust : clustering) {
         DataTable data = new DataTable(Double.class, Double.class);
         for (Instance inst : clust) {
         data.add(inst.value(2), inst.value(1));
         System.out.println(String.format("point " + inst.value(2) + ", " + inst.value(1)));
         }

         //DataSeries ds = new DataSeries(clust.getName(), data);
         plot.add(data);
         plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
         // Format points
         plot.getPointRenderer(data).setColor(clust.getColor());
         }

         // Format plot
         plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
         plot.getTitle().setText("Scatterplot of clustering");

         // Add plot to Swing component
         add(new InteractivePanel(plot), BorderLayout.CENTER);*/
    }

}
