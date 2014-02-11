package org.clueminer.chart.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.clueminer.approximation.api.Approximator;
import org.clueminer.approximation.api.ApproximatorFactory;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.base.AbstractOverlay;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.AttrHashDataset;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.events.DatasetEvent;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Overlay.class)
public class ExponentialOverlay extends AbstractOverlay implements Overlay {

    private static final long serialVersionUID = 157348685817993889L;
    private final OverlayProperties properties;
    private Dataset<? extends Instance> approxData;
    private final Approximator approximator;
    private double[] xAxis;

    public ExponentialOverlay() {
        super();
        properties = new OverlayProperties();
        ApproximatorFactory af = ApproximatorFactory.getInstance();
        approximator = af.getProvider("exp");
    }

    @Override
    public String getName() {
        return "Exponential overlay";
    }

    @Override
    public String getLabel() {
        return "Exp";
    }

    @Override
    public Overlay newInstance() {
        return new ExponentialOverlay();
    }

    @Override
    public LinkedHashMap getHTML(ChartConfig cf, int i) {
        LinkedHashMap<String, String> ht = new LinkedHashMap<String, String>();

        ht.put(getLabel(), " ");

        return ht;
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf, Rectangle bounds) {
        Color color = properties.getColor();
        Stroke stroke = properties.getStroke();

        if (dataset != null && !dataset.isEmpty()) {
            Range range = cf.getRange();

            Stroke old = g.getStroke();
            g.setPaint(color);
            if (stroke != null) {
                g.setStroke(stroke);
            }
            Point2D.Double point = null;

            int itemCnt = dataset.attributeCount();
            double x, pos = 0;
            double inc = 0.01;
            double[] params;
            //params[0] =

            for (int i = 0; i < dataset.size(); i++) {

                if (approxData.hasIndex(i)) {
                    params = fetchParams(i);
                } else {
                    params = computeParams(i);
                }

                int j = 0;
                while (j < itemCnt) {
                    x = cf.getChartData().getXFromRatio(pos, bounds);
                    // double appY = a * Math.exp((-t*pos))+c;
                    double appY = approximator.getFunctionValue(pos, params);
                    double y = cf.getChartData().getY(appY, bounds, range);
                    System.out.println("pos= " + pos + ", x= " + x + " y= " + y + " aspY= " + appY);

                    Point2D.Double p = new Point2D.Double(x, y);
                    if (point != null) {
                        g.draw(new Line2D.Double(point, p));
                    }
                    point = p;
                    pos += inc;
                    j++;
                }
            }
            g.setStroke(old);
        }
        g.dispose();
    }

    private double[] computeParams(int i) {
        ContinuousInstance input = (ContinuousInstance) dataset.get(i);
        HashMap<String, Double> coefficients = new HashMap<String, Double>();
        approximator.estimate(xAxis, input, coefficients);
        double[] coef = new double[coefficients.size()];
        int j = 0;
        for (Map.Entry<String, Double> item : coefficients.entrySet()) {
            approxData.setAttributeValue(item.getKey(), i, item.getValue());
            coef[j++] = item.getValue();
        }
        return coef;
    }

    private double[] fetchParams(int idx) {
        double[] params = new double[3];

        params[0] = dataset.getAttributeValue("exp-a", idx);
        params[1] = dataset.getAttributeValue("exp-t", idx);
        params[2] = dataset.getAttributeValue("exp-c", idx);
        return params;
    }

    @Override
    public void calculate() {
    }

    @Override
    public Color[] getColors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getValues(ChartConfig cf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] getValues(ChartConfig cf, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getMarkerVisibility() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractNode getNode() {
        return new OverlayNode(properties);
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        //TODO: deal with different types of datasets
        TimeseriesDataset<? extends Instance> ts = (TimeseriesDataset<? extends Instance>) dataset;
        xAxis = ts.getTimePointsArray();
        approxData = new AttrHashDataset<Instance>(dataset.size());
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        approxData = null;
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        //maybe invalidated dataset
    }

}
