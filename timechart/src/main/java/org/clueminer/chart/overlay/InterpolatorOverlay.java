package org.clueminer.chart.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.base.AbstractOverlay;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.events.DatasetEvent;
import org.clueminer.interpolation.LinearInterpolator;
import org.clueminer.math.Interpolator;
import org.clueminer.math.InterpolatorFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Overlay.class)
public class InterpolatorOverlay extends AbstractOverlay {

    private static final long serialVersionUID = 7547339484239308881L;

    private final OverlayProperties properties;
    private Interpolator interpolator;

    public InterpolatorOverlay() {
        super();
        properties = new OverlayProperties();
        interpolator = new LinearInterpolator();
    }

    @Override
    public String getName() {
        return "interpolation";
    }

    @Override
    public String getLabel() {
        return getInterpolatorName();
    }

    @Override
    public AbstractOverlay newInstance() {
        return new InterpolatorOverlay();
    }

    @Override
    public LinkedHashMap getHTML(ChartConfig cf, int i) {
        LinkedHashMap<String, String> ht = new LinkedHashMap<String, String>();
        //double[] values = getValues(cf, i);

        ht.put(getLabel(), " ");

        return ht;
    }

    @Override
    public void paint(Graphics2D g, ChartConfig cf, Rectangle bounds) {
        Color color = properties.getColor();
        Stroke stroke = properties.getStroke();

        Timeseries<ContinuousInstance> visible = (Timeseries<ContinuousInstance>) cf.getChartData().getVisible();
        if (visible != null) {
            Range range = cf.getRange();

            Stroke old = g.getStroke();
            g.setPaint(color);
            if (stroke != null) {
                g.setStroke(stroke);
            }
            Point2D.Double point = null;
            double chartWidth = bounds.getWidth();

            int itemCnt = cf.getChartData().getTimePointsCnt();
            double x = 0, pos = 0;
            double inc = 0.1;
            interpolator.setX(visible.getTimePoints());
            for (int i = 0; i < visible.size(); i++) {
                int j = 0;
                ContinuousInstance instance = visible.instance(i);
                interpolator.setY(instance.arrayCopy());
                while (x < chartWidth && j < itemCnt) {
                    x = cf.getChartData().getXFromRatio(pos, bounds);

                    double appY = instance.valueAt(pos, interpolator);
                    double y = cf.getChartData().getY(appY, bounds, range);
                    //System.out.println("pos= " + pos + ", x= " + x + " y= " + y + " aspY= " + appY);
                    if (!Double.isNaN(y)) {
                        Point2D.Double p = new Point2D.Double(x, y);
                        if (point != null) {
                            g.draw(new Line2D.Double(point, p));
                        }
                        point = p;
                    }
                    pos += inc;
                    j++;
                }
            }
            g.setStroke(old);
        }
    }

    @Override
    public void calculate() {
    }

    @Override
    public Color[] getColors() {
        return new Color[]{properties.getColor()};
    }

    @Override
    public double[] getValues(ChartConfig cf) {
        return new double[10];
    }

    @Override
    public double[] getValues(ChartConfig cf, int i) {
        return new double[10];
    }

    @Override
    public boolean getMarkerVisibility() {
        return false;
    }

    @Override
    public AbstractNode getNode() {
        return new InterpolateNode(properties, this);
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

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public String[] getInterpolators() {
        return InterpolatorFactory.getInstance().getProvidersArray();
    }

    public String getInterpolatorName() {
        return interpolator.getName();
    }

    public void setInterpolatorName(String name) {
        System.out.println("setting interpolatror " + name);
        if (!name.equals(getInterpolatorName())) {
            Interpolator interp = InterpolatorFactory.getInstance().getProvider(name);

            if (interp != null) {
                this.interpolator = interp;
            }
        }
    }

}
