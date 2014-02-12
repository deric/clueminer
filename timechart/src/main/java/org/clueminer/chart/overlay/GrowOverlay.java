package org.clueminer.chart.overlay;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.Tables;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.HashMap;
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
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
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
    private final static BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    private int lineEndLen = 5;
    private TimePoint[] tp;
    //lower value of derivation will be considered as end of growing
    private double delta = -0.05;
    private int subSeq;

    public GrowOverlay() {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel() {
        return "grow area";
    }

    public static Table<Integer, Double, Double> newTable() {
        return Tables.newCustomTable(
                Maps.<Integer, Map<Double, Double>>newHashMap(),
                new Supplier<Map<Double, Double>>() {
                    @Override
                    public Map<Double, Double> get() {
                        return Maps.newHashMap();
                    }
                });
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf, Rectangle bounds) {
        double x1, x2, y1, y2, dx;

        Table<Integer, Double, Double> coodX;
        Table<Integer, Double, Double> coodY;

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
                coodX = newTable();
                coodY = newTable();
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
                            addSubSequence(inst, startId, endId, coodX, coodY);
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
                    addSubSequence(inst, startId, inst.size() - 1, coodX, coodY);
                }
                //find max subsequence
                max = Double.MIN_VALUE;
                int key = 0;
                double diff;
                for (Cell<Integer, Double, Double> entry : coodX.cellSet()) {
                    diff = entry.getValue() - entry.getColumnKey();
                    if (diff > max) {
                        max = diff;
                        key = entry.getRowKey();
                    }
                }
                Map<Double, Double> xVal = coodX.row(key);
                Double[] xcc = xVal.keySet().toArray(new Double[2]);
                System.out.println("xcc key = " + xcc[0] + " value = " + xcc[1]);
                System.out.println("row: " + xVal.toString());
                Double[] x = xVal.values().toArray(new Double[2]);
                System.out.println("key = " + x[0] + " value = " + x[1]);
                /*           Map<Double, Double> yVal = coodY.row(key);
                 Double[] y = yVal.values().toArray(new Double[2]);

                growFactor(x[0], x[1], y[0], y[1]);
                drawLine(g, cf, x[0], x[1], inst.value(0));
                System.out.println("longest subseqence: " + max);
*/
            }
        }
    }

    private void growFactor(double x1, double x2, double y1, double y2) {
        double a = x2 - x1;
        System.out.println("a = " + a);
        System.out.println("[" + x1 + ", " + y1 + "] [" + x2 + ", " + y2 + "]");
    }

    private void addSubSequence(Instance inst, int startId, int endId, Table<Integer, Double, Double> cX, Table<Integer, Double, Double> cY) {
        double xs, xe;
        xs = tp[startId].getPosition();
        xe = tp[endId].getPosition();
        System.out.println("found subseqence: " + xs + " - " + xe + " len: " + (xe - xs));
        int id = subSeq++;
        cX.put(id, xs, xe);
        cY.put(id, inst.value(startId), inst.value(endId));
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
