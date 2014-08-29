package org.clueminer.chart.overlay;

import com.google.common.collect.Maps;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.base.AbstractOverlay;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
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
public class GrowOverlay extends AbstractOverlay implements Overlay {

    private static final String name = "grow";
    protected OverlayProperties properties;
    private int dotWidth = 6;
    private int dotHeight = 6;
    private final static float dash1[] = {10.0f};
    private final static float dot1[] = {2.0f};
    private final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    private final static BasicStroke dotted = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dot1, 0.0f);
    private int lineEndLen = 5;
    private TimePoint[] tp;
    //lower value of derivation will be considered as end of growing
    private double delta = -0.05;
    private int subSeq;
    private Polygon arrowHead;
    private AffineTransform tx = new AffineTransform();

    public GrowOverlay() {
        initComponents();
    }

    /**
     * create an AffineTransform and a triangle centered on (0,0) and pointing
     * downward somewhere outside Swing's paint loop
     */
    private void initComponents() {
        arrowHead = new Polygon();
        arrowHead.addPoint(0, 5);
        arrowHead.addPoint(-5, -5);
        arrowHead.addPoint(5, -5);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return "grow area";
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf, Rectangle bounds) {
        double x1, x2, y1, y2, dx;

        Map<Integer, GrowingPeriod> subsequence;

        if (dataset != null) {
            Timeseries<? extends ContinuousInstance> ts = (Timeseries<? extends ContinuousInstance>) dataset;
            tp = ts.getTimePoints();
            double max;
            int startId, endId;
            for (ContinuousInstance inst : ts) {
                x1 = tp[0].getPosition();
                y1 = inst.value(0);
                startId = -1;
                endId = -1;
                subsequence = Maps.newHashMap();
                subSeq = 0;
                for (int i = 1; i < inst.size(); i++) {
                    x2 = tp[i].getPosition();
                    y2 = inst.value(i);
                    dx = (y2 - y1) / (x2 - x1);
                    //check if derivative is positive -> ts is growing
                    //System.out.println("dx =" + dx + " start: " + startId + " end: " + endId);
                    if (startId == -1 && dx >= 0.0) {
                        //growing started one point before, unless first point
                        startId = i > 0 ? (i - 1) : i;
                    } else if (dx < delta && endId == -1) {
                        if (startId > -1) {
                            endId = i - 1;
                            addSubSequence(inst, startId, endId, subsequence);
                            startId = i;
                            endId = -1;
                        } else {
                            startId = i;
                        }
                    }
                    //move to next point
                    x1 = x2;
                    y1 = y2;
                }
                //growing all the time
                if (endId == -1) {
                    addSubSequence(inst, startId, inst.size() - 1, subsequence);
                }
                //find max subsequence
                max = Double.MIN_VALUE;
                int maxId = -1;
                double diff;
                for (Entry<Integer, GrowingPeriod> entry : subsequence.entrySet()) {
                    diff = entry.getValue().getX2() - entry.getValue().getX1();
                    if (diff > max) {
                        max = diff;
                        maxId = entry.getKey();
                    }
                }
                if (maxId > -1) {
                    GrowingPeriod grow = subsequence.get(maxId);

                    drawLine(g, cf, grow.x1, grow.x2, inst.value(0));

                    growFactor(g, cf, grow.x1, grow.x2, grow.y1, grow.y2);

                    //System.out.println("longest subseqence: " + max);
                } else {
                    throw new RuntimeException("no subseqence found");
                }
                //end of instance
            }
        }
    }

    private void growFactor(Graphics2D g2d, ChartConfig cf, double a1, double a2, double b1, double b2) {
        ChartData cd = cf.getChartData();
        //ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        //rect.grow(-2, -2);
        Range range = cf.getRange();
        double x1 = cd.getXFromRatio(a1, rect);
        double x2 = cd.getXFromRatio(a2, rect);
        double y1 = cd.getY(b1, rect, range);
        double y2 = cd.getY(b2, rect, range);
        double a = x2 - x1;
        double b = y2 - y1;
        double beta = Math.atan((b / a));
        //System.out.println("[" + x1 + ", " + y1 + "] [" + x2 + ", " + y2 + "]");
        //System.out.println("grow: a = " + a + ", b = " + b + " tan= " + Math.atan((b / a)) + ", arg = " + (b / a));

        double f = 0.5;
        double p1 = a * f;
        //      System.out.println("tangesn = " + Math.tan(beta));
        double p2 = a * Math.tan(beta);
        Line2D.Double line = new Line2D.Double(x1, y1, x1 + p1, y1 + p2);
        //    System.out.println("second: " + p1 + ", " + p2);
        g2d.setStroke(dotted);
        g2d.draw(line);

        /*   g2d.fill(arrowHead);

         g2d.translate(line.x2, line.y2);
         g2d.draw(arrowHead);
         g2d.translate(-line.x2, -line.y2);*/
        /* tx.setToIdentity();
         //double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
         Graphics2D g = (Graphics2D) g2d.create();
         double rot = Math.PI * beta;
         //tx.rotate(rot, line.x2, line.y2);
         //System.out.println("rotating by " + rot);
         tx.translate(line.x1, line.y1);
         g.setTransform(tx);
         g.fill(arrowHead);
         g.dispose();*/
//        AffineTransform at = AffineTransform.getRotateInstance(beta, 10, 10);
//         Shape rotatedArrow = at.createTransformedShape(arrowHead);
        //at.translate(line.x1, line.y1);
  /*      double rot = Math.PI * beta;
         tx.rotate(rot);
         System.out.println("rotating by " + rot);
         //     Shape s = tx.createTransformedShape(line);

         //       g2d.draw(rotatedArrow);
         //
         Graphics2D g = (Graphics2D) g2d.create();
         //g.draw(s);
         g.setTransform(tx);
         g.fill(arrowHead);
         //   g.draw(rotatedArrow);
         g.dispose();*/
    }

    private void addSubSequence(Instance inst, int startId, int endId, Map<Integer, GrowingPeriod> map) {
        double xs, xe;
        xs = tp[startId].getPosition();
        xe = tp[endId].getPosition();
        //System.out.println("found subseqence: " + xs + " - " + xe + " len: " + (xe - xs));
        int id = subSeq++;
        map.put(id, new GrowingPeriod(xs, xe, inst.value(startId), inst.value(endId)));
    }

    private void drawLine(Graphics2D g, ChartConfig cf, double start, double end, double yPos) {
        ChartData cd = cf.getChartData();
        //ChartProperties cp = cf.getChartProperties();
        Rectangle rect = cf.getChartPanel().getBounds();
        rect.grow(-2, -2);
        Range range = cf.getRange();
        double x1 = cd.getXFromRatio(start, rect);
        double x2 = cd.getXFromRatio(end, rect);
        double y = cd.getY(yPos, rect, range);
        g.setColor(Color.red);
        //line endings |----|
        g.draw(new Line2D.Double(x1, y + lineEndLen, x1, y - lineEndLen));
        g.draw(new Line2D.Double(x2, y + lineEndLen, x2, y - lineEndLen));

        //line itself
        g.setStroke(dashed);
        g.draw(new Line2D.Double(x1, y, x2, y));
    }

    private class GrowingPeriod {

        private double x1, y1, x2, y2;

        public GrowingPeriod(double x1, double x2, double y1, double y2) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
        }

        public double getX1() {
            return x1;
        }

        public double getY1() {
            return y1;
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

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
