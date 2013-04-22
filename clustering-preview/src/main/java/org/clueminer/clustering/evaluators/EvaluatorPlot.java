package org.clueminer.clustering.evaluators;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;
import org.clueminer.clustering.api.ClusterEvaluator;
import org.clueminer.clustering.api.ClusterEvaluatorFactory;
import org.clueminer.clustering.api.HierarchicalResult;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author Tomas Barton
 */
public class EvaluatorPlot extends JPanel {

    private static final long serialVersionUID = 4355229276691601032L;
    private HierarchicalResult clustering;
    private ClusterEvaluator evaluator;

    public EvaluatorPlot() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
    }

    private void clusteringChanged() {
        this.removeAll();
        
        double[] x = null;
        double[] y = null;
        
        if(clustering != null){
            Map<Integer, Double> scores = clustering.getScores(evaluator.getName());
            Iterator<Entry<Integer, Double>> it = scores.entrySet().iterator();
            x = new double[scores.size()];
            y = new double[scores.size()];
            int i = 0;
            while(it.hasNext()){
                Entry<Integer, Double> mapping = it.next();
                x[i] = mapping.getKey();
                y[i] = mapping.getValue();
                i++;
            }
            
        }

        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();

        // define the legend position
        plot.addLegend("SOUTH");

        // add a line plot to the PlotPanel
        String title = "unknown";
        if(evaluator != null){
            title = evaluator.getName();
        }
        plot.addLinePlot(title, x, y);

        add(plot);

    }

    public void setClustering(HierarchicalResult clustering) {
        System.out.println("setting clustreing "+clustering);
        this.clustering = clustering;
        clusteringChanged();
    }
    
    public void setEvaluator(String eval){
        evaluator = ClusterEvaluatorFactory.getDefault().getProvider(eval);
        System.out.println("evaluator is "+evaluator.getName());
        clusteringChanged();
    }   
}
