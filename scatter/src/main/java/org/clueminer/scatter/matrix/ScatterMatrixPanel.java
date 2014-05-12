package org.clueminer.scatter.matrix;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class ScatterMatrixPanel extends JPanel {

    private static final long serialVersionUID = 4957672836007726620L;

    private final Shape shape = new Ellipse2D.Double(-3, -3, 6, 6);
    private static final Logger logger = Logger.getLogger(ScatterMatrixPanel.class.getName());

    public ScatterMatrixPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(800, 600));
    }

    public void setClustering(final Clustering<Cluster> clustering) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();

                DrawablePanel chart;
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.anchor = GridBagConstraints.NORTHWEST;
                c.weightx = 1.0;
                c.weighty = 1.0;

                if (clustering != null && clustering.size() > 0) {
                    Cluster first = clustering.get(0);
                    if (first.size() > 0) {
                        int attrCnt = first.attributeCount();

                        for (int i = 0; i < attrCnt; i++) {
                            for (int j = 0; j < i; j++) {
                                chart = clusteringPlot(clustering, i, j);
                                c.gridx = i;
                                c.gridy = j;
                                add(chart, c);
                            }
                        }
                    } else {
                        logger.log(Level.SEVERE, "empty cluster");
                    }
                }

                revalidate();
                validate();
                repaint();
            }
        });

    }

    private DrawablePanel clusteringPlot(final Clustering<Cluster> clustering, int attrX, int attrY) {
        // Create a new xy-plot
        XYPlot plot = new XYPlot();
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
