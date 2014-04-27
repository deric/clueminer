package org.clueminer.scatter;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

public class ScatterPlot extends JPanel {

    private static final long serialVersionUID = -412699430625953887L;

    private final Shape shape = new Ellipse2D.Double(-3, -3, 6, 6);

    public ScatterPlot() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
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

                // Create a new xy-plot
                XYPlot plot = new XYPlot();

                int attrX = 0;
                int attrY = 1;

                for (Cluster<Instance> clust : clustering) {
                    DataTable data = new DataTable(Double.class, Double.class);
                    for (Instance inst : clust) {
                        data.add(inst.value(attrX), inst.value(attrY));
                    }

                    DataSeries ds = new DataSeries(clust.getName(), data);
                    plot.add(ds);

                    PointRenderer pointRenderer = plot.getPointRenderer(ds);
                    pointRenderer.setColor(clust.getColor());
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

                // Add plot to Swing component
                add(new InteractivePanel(plot), BorderLayout.CENTER);
                revalidate();
                validate();
                repaint();
            }
        });
    }

}
