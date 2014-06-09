package org.clueminer.clustering.evaluators;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.eval.utils.HashEvaluationTable;

/**
 *
 * @author Tomas Barton
 */
public class EvaluatorPlot extends JPanel {

    private static final long serialVersionUID = 4355229276691601032L;
    private Collection<? extends Clustering> clusterings;
    private final Shape shape = new Ellipse2D.Double(-3, -3, 6, 6);
    private ClusterEvaluator evaluatorX;
    private ClusterEvaluator evaluatorY;

    public EvaluatorPlot() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
    }

    private void clusteringChanged() {
        this.removeAll();

        //double[] x = null;
        //double[] y = null;
        if (clusterings != null) {
            add(clusteringPlot(clusterings));
            /* Map<Integer, Double> scores = clustering.getScores(evaluator.getName());
             Iterator<Entry<Integer, Double>> it = scores.entrySet().iterator();
             x = new double[scores.size()];
             y = new double[scores.size()];
             int i = 0;
             while (it.hasNext()) {
             Entry<Integer, Double> mapping = it.next();
             x[i] = mapping.getKey();
             y[i] = mapping.getValue();
             i++;
             }*/
        }
        /*
         // create your PlotPanel (you can use it as a JPanel)
         Plot2DPanel plot = new Plot2DPanel();

         // define the legend position
         plot.addLegend("SOUTH");

         // add a line plot to the PlotPanel
         String title = "unknown";
         if (evaluatorX != null) {
         title = evaluatorX.getName();
         }
         plot.addLinePlot(title, x, y);

         add(plot);
         */
    }

    private DrawablePanel clusteringPlot(Collection<? extends Clustering> clusterings) {
        // Create a new xy-plot
        XYPlot plot = new XYPlot();

        Color orig, trans;

        DataTable data = new DataTable(Double.class, Double.class);

        for (Clustering<? extends Cluster> clust : clusterings) {
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
        plot.setLegendVisible(true);

        // Format axes
        AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        axisRendererX.setLabel(evaluatorX.getName());
        AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        axisRendererY.setLabel(evaluatorY.getName());

        return new InteractivePanel(plot);
    }

    private EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering) {
        EvaluationTable evalTable = clustering.getLookup().lookup(EvaluationTable.class);
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
        evaluatorX = InternalEvaluatorFactory.getInstance().getProvider(eval);
        clusteringChanged();
    }

    public void setEvaluatorY(String eval) {
        evaluatorY = InternalEvaluatorFactory.getInstance().getProvider(eval);
        clusteringChanged();
    }
}
