package org.clueminer.clustering.evaluators;

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
import java.util.Collection;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;

/**
 *
 * @author Tomas Barton
 */
public class EvaluatorPlot<E extends Instance, C extends Cluster<E>> extends JPanel {

    private static final long serialVersionUID = 4355229276691601032L;
    private Collection<? extends Clustering> clusterings;
    private final Shape shape = new Ellipse2D.Double(-3, -3, 6, 6);
    private InternalEvaluator evaluatorX;
    private InternalEvaluator evaluatorY;

    public EvaluatorPlot() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setSize(new Dimension(800, 600));
    }

    private void clusteringChanged() {
        this.removeAll();

        if (clusterings != null && evaluatorX != null && evaluatorY != null) {
            add(clusteringPlot(clusterings),
                new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                       GridBagConstraints.NORTHWEST,
                                       GridBagConstraints.BOTH,
                                       new Insets(0, 0, 0, 0), 0, 0));
            revalidate();
            validate();
            repaint();
        }

    }

    private DrawablePanel clusteringPlot(Collection<? extends Clustering> clusterings) {
        // Create a new xy-plot
        XYPlot plot = new XYPlot();

        Color orig, trans;

        DataTable data = new DataTable(Double.class, Double.class);

        for (Clustering<E, C> clust : clusterings) {
            EvaluationTable table = evaluationTable(clust);
            data.add(table.getScore(evaluatorX), table.getScore(evaluatorY));
        }

        DataSeries ds = new DataSeries("pareto plot", data);
        plot.add(ds);

        PointRenderer pointRenderer = plot.getPointRenderer(ds);
        orig = Color.BLUE;
        //last param is transparency
        trans = new Color(orig.getRed(), orig.getGreen(), orig.getBlue(), 200);
        pointRenderer.setColor(trans);
        pointRenderer.setShape(shape);

        // Format plot
        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));
        //plot.getTitle().setText(clustering.getName());
        plot.setLegendVisible(false);

        // Format axes
        AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        axisRendererX.setLabel(evaluatorX.getName());
        AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        axisRendererY.setLabel(evaluatorY.getName());

        return new InteractivePanel(plot);
    }

    private EvaluationTable<E, C> evaluationTable(Clustering<E, C> clustering) {
        EvaluationTable<E, C> evalTable = clustering.getLookup().lookup(EvaluationTable.class);
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            evalTable = new HashEvaluationTable(clustering, clustering.getLookup().lookup(Dataset.class));
            clustering.lookupAdd(evalTable);
        }
        return evalTable;
    }

    public void setClusterings(Collection<? extends Clustering> clusterings) {
        this.clusterings = clusterings;
        clusteringChanged();
    }

    public void setEvaluatorX(String eval) {
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluatorX = ief.getProvider(eval);
        clusteringChanged();
    }

    public void setEvaluatorY(String eval) {
        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
        evaluatorY = ief.getProvider(eval);
        clusteringChanged();
    }
}
