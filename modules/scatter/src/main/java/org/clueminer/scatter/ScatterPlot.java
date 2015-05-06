package org.clueminer.scatter;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author deric
 * @deprecated will be replaced by faster @{link ScatterPlot2}
 */
public class ScatterPlot extends JPanel {

    private static final long serialVersionUID = -412699430625953887L;

    private final Shape shape = new Ellipse2D.Double(-3, -3, 6, 6);

    public ScatterPlot() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(800, 600));
    }

    /**
     * Updating chart might take a while, therefore it's safer to preform update
     * in EDT
     *
     * @param clustering
     */
    public void setClustering(final Clustering<Cluster> clustering) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();

                add(clusteringPlot(clustering),
                        new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                GridBagConstraints.NORTHWEST,
                                GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                revalidate();
                validate();
                repaint();
            }
        });
    }

    public void setClusterings(final Clustering<Cluster> clusteringA, final Clustering<Cluster> clusteringB) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();

                GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0);

                // Add plot to Swing component
                add(clusteringPlot(clusteringA), c);
                c.gridx = 1;
                add(clusteringPlot(clusteringB), c);
                revalidate();
                validate();
                repaint();
            }
        });
    }

    private DrawablePanel clusteringPlot(final Clustering<Cluster> clustering) {
        // Create a new xy-plot
        XYPlot plot = new XYPlot();

        int attrX = 0;
        int attrY = 1;
        Color orig, trans;

        for (Cluster<Instance> clust : clustering) {
            DataTable data = new DataTable(Double.class, Double.class);
            for (Instance inst : clust) {
                data.add(inst.value(attrX), inst.value(attrY));
            }

            DataSeries ds = new DataSeries(clust.getName(), data);
            plot.add(ds);

            PointRenderer pointRenderer = plot.getPointRenderer(ds);
            orig = clust.getColor();
            //last param is transparency
            trans = new Color(orig.getRed(), orig.getGreen(), orig.getBlue(), 200);
            pointRenderer.setColor(trans);
            pointRenderer.setShape(shape);
        }

        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        plot.getTitle().setText(clustering.getName());
        plot.setLegendVisible(true);

        if (clustering.size() > 0) {
            Cluster c = clustering.get(0);
            // Format axes
            AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
            axisRendererX.setLabel(c.getAttribute(attrX).getName());
            AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
            axisRendererY.setLabel(c.getAttribute(attrY).getName());
        }
        return new InteractivePanel(plot);
    }

}
