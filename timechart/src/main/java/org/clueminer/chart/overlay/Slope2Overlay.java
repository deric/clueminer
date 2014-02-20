package org.clueminer.chart.overlay;

import java.awt.BasicStroke;
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
public class Slope2Overlay extends AbstractOverlay implements Overlay {

    private static final String name = "slope2";
    protected OverlayProperties properties;
    private int dotWidth = 6;
    private int dotHeight = 6;
    private final static float dot1[] = {2.0f};
    private final static BasicStroke dotted = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dot1, 0.0f);

    public Slope2Overlay() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return "slope 2";
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
        double x1, x2, y1, y2, d = 0, ay1, ay2;
        BufferedImage minDot = createDot(Color.ORANGE);
        BufferedImage maxDot = createDot(Color.MAGENTA);
        if (dataset != null) {
            Timeseries<? extends ContinuousInstance> ts = (Timeseries<? extends ContinuousInstance>) dataset;
            TimePoint[] tp = ts.getTimePoints();
            double min, max;
            int minIdx = 0, maxIdx = 0;
            int window = 3;
            int j;
            for (ContinuousInstance inst : ts) {
                min = Double.MAX_VALUE;
                max = Double.MIN_VALUE;
                for (int i = 0; i < (inst.size() - 2 * window); i++) {
                    j = 0;
                    y1 = 0;
                    y2 = 0;
                    while (j < window) {
                        //if(inst.)
                        y1 += inst.value(i + j);
                        y2 += inst.value(i + j + window);
                        j++;
                    }
                    ay1 = y1 / (double) window;
                    ay2 = y2 / (double) window;

                    x1 = (tp[i].getPosition() - tp[i + window - 1].getPosition());
                    x2 = (tp[i + window].getPosition() - tp[i + 2 * window - 1].getPosition());
                    d = (ay2 - ay1) / (x2 - x1);
                    if (d < min) {
                        min = d;
                        //index should be even
                        minIdx = i + window / 2;
                    }
                    if (d > max) {
                        max = d;
                        maxIdx = i + window / 2;
                    }
                    System.out.println("d " + i + " = " + d);
                }
                //paint min and max

                drawDot(g, cf, minIdx, inst.value(minIdx), minDot);
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
        return new Slope2Overlay();
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
