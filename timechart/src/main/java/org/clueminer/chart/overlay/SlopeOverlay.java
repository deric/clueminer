package org.clueminer.chart.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.base.AbstractOverlay;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.events.DatasetEvent;
import org.clueminer.types.TimePoint;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Overlay.class)
public class SlopeOverlay extends AbstractOverlay implements Overlay {

    private static final String name = "slope";
    protected OverlayProperties properties;
    private int dotWidth = 6;
    private int dotHeight = 6;

    public SlopeOverlay() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return "slope";
    }

    private BufferedImage createDot(Color color) {
        BufferedImage bufferedImage = new BufferedImage(dotWidth, dotHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D buff = bufferedImage.createGraphics();
        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, dotWidth, dotHeight);
        buff.setPaint(color);
        buff.draw(ellipse);
        buff.fill(ellipse);
        buff.dispose();
        return bufferedImage;
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf, Rectangle bounds) {
        double x1, x2, y1, y2, d;
        BufferedImage minDot = createDot(Color.RED);
        BufferedImage maxDot = createDot(Color.BLUE);
        if (dataset != null) {
            Timeseries<? extends ContinuousInstance> ts = (Timeseries<? extends ContinuousInstance>) dataset;
            TimePoint[] tp = ts.getTimePoints();
            double min, max;
            double mx, mi;
            int minIdx = 0, maxIdx = 0;
            for (ContinuousInstance inst : ts) {
                min = Double.MAX_VALUE;
                max = Double.MIN_VALUE;
                x1 = tp[0].getPosition();
                y1 = inst.value(0);
                for (int i = 1; i < inst.size(); i++) {
                    x2 = tp[i].getPosition();
                    y2 = inst.value(i);
                    d = (y2 - y1) / (x2 - x1);
                    if (d < min) {
                        min = d;
                        minIdx = i;
                    }
                    if (d > max) {
                        max = d;
                        maxIdx = i;
                    }
                    //move to next point
                    x1 = x2;
                    y1 = y2;
                }
                //paint min and max

                System.out.println("std dev = " + inst.getStdDev());
                //drawDot(g, cf, minIdx, inst.value(minIdx), minDot);
                drawDot(g, cf, maxIdx, inst.value(maxIdx), maxDot);
            }
        }
    }

    private void drawDot(Graphics2D g, ChartConfig cf, int i, double value, BufferedImage img) {
        AffineTransform at = new AffineTransform();
        at.scale(1, 1);

        ChartData cd = cf.getChartData();
        //ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();
        double x = cd.getX(i, rect);
        double y = cd.getY(value, rect, range);

        //filling shapes seems to be very expensive operation
        //this is probably the fastest way how to draw
        at.setToIdentity();
        at.translate(x - 2, y - 2);
        g.drawImage(img, at, null);
    }

    @Override
    public LinkedHashMap getHTML(ChartConfig cf, int i) {
        LinkedHashMap<String, String> ht = new LinkedHashMap<String, String>();

        ht.put(getLabel(), " ");

        return ht;
    }

    @Override
    public void calculate() {
        //
    }

    @Override
    public AbstractOverlay newInstance() {
        return new SlopeOverlay();
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
    public void datasetChanged(DatasetEvent evt) {
        super.datasetChanged(evt);

    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        //
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        //
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        //
    }

}
