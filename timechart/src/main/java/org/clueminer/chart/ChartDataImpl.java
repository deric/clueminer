package org.clueminer.chart;

import org.clueminer.chart.api.ChartListener;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.api.Range;
import org.clueminer.chart.api.ChartConfig;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.clueminer.algorithm.BinarySearch;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.chart.api.Annotation;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dataset.plugin.TimeseriesDataset;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.clueminer.math.Interpolator;
import org.clueminer.std.StdScale;
import org.clueminer.timeseries.chart.NormalizationEvent;
import org.clueminer.timeseries.chart.NormalizationListener;
import org.clueminer.types.TimePoint;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public class ChartDataImpl implements Serializable, ChartListener, ChartData {

    private static final long serialVersionUID = 4940050530914199965L;
    public static final int MIN_ITEMS = 40;
    public static final int MAX_ITEMS = 1000;
    public static Insets axisOffset = new Insets(5, 5, 5, 5);
    public static Insets dataOffset = new Insets(2, 20, 60, 55);
    private Chart chart = null;
    private TimeseriesDataset<? super ContinuousInstance> visible = new TimeseriesDataset<ContinuousInstance>(100);
    private Range visibleRange = null;
    private List<Overlay> savedOverlays = new ArrayList<Overlay>();
    private List<Integer> annotationsCount = new ArrayList<Integer>();
    private List<Annotation> annotations = new ArrayList<Annotation>();
    //index which we start to render
    private int start = 0;
    //last rendered index
    private int finish = -1;
    private long lastTimePoint = 0;
    private double maxY = 1;
    private double minY = 0;
    //first visible item, distance on axis X
    private double startX = 0;
    private double finishX = 100;
    //last value on axis
    private double lastX = 0;
    private double zoom = 1.0;
    private int cntTimePoints = 0;
    private String name = "untitled";
    private boolean displayedLastPoint = false;

    public ChartDataImpl() {
    }

    public ChartDataImpl(Timeseries<? extends ContinuousInstance> dataset) {
        setVisible(dataset);
        updateTimePoints();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int size() {
        return visible.size();
    }

    public final void updateTimePoints() {
        cntTimePoints = visible.attributeCount();
        // first point is considered to be 0
        TimePoint tp = (TimePoint) visible.getAttribute(cntTimePoints - 1);
        if (tp.getTimestamp() == 0) {
            throw new RuntimeException("time is not set");
        }
        StdScale scale = new StdScale();

        TimePointAttribute[] tps = visible.getTimePoints();
        double pos;
        double begin = tps[0].getTimestamp();
        double end = tps[tps.length - 1].getTimestamp();
        for (TimePointAttribute attr : tps) {
            // scale to interval <0, 1>
            pos = scale.scaleToRange(attr.getTimestamp(), begin, end, 0.0, 1.0);
            attr.setPosition(pos);
        }
    }

    public double getPositionAt(int index) {
        if (index < 0 || index > cntTimePoints) {
            throw new ArrayIndexOutOfBoundsException("index " + index + " is not contained in timepoints");
        }
        return getTimePoint(index).getPosition();
    }

    public void setStart(int first, Rectangle bounds) {
        if (first < 0 || first >= cntTimePoints) {
            this.start = 0;
        } else {
            this.start = first;
        }
        setStartX(bounds);
    }

    public void setStartX(Rectangle rect) {
        startX = ((getTimePoint(start).getPosition() * zoom * rect.getWidth()));
        /*
         * double fin = finishX - startX; setFinish(findAbsoluteIndex(fin,
         * rect), rect);
         */
    }

    @Override
    public void setFinish(int index, Rectangle rect) {
        if (index >= cntTimePoints) {
            this.finish = cntTimePoints - 1;
            finishX = lastX;
            displayedLastPoint = true;
        } else {
            this.finish = index;
            finishX = getTimePoint(index).getPosition() * zoom * rect.getWidth();
            displayedLastPoint = false;
        }
    }

    public boolean getDisplayedLast() {
        return displayedLastPoint;
    }

    @Override
    public int getTimePointsCnt() {
        return cntTimePoints;
    }

    /**
     * Last time point index
     *
     * @return
     */
    @Override
    public int getLast() {
        return cntTimePoints - 1;
    }

    public TimePointAttribute getLastTimePoint() {
        if (visible == null) {
            return null;
        }
        return (TimePointAttribute) visible.getAttribute(cntTimePoints - 1);
    }

    public TimePointAttribute getFirstTimePoint() {
        if (visible == null) {
            return null;
        }
        return (TimePointAttribute) visible.getAttribute(0);
    }

    public boolean isSampleSetNull() {
        if (visible == null) {
            return true;
        }
        return visible.isEmpty();
    }

    @Override
    public Timeseries<? extends ContinuousInstance> getVisible() {
        return visible;
    }

    private void setVisible(ContinuousInstance item) {
        checkMax(item.getMax());
        checkMin(item.getMin());
        visible.add(item);
    }

    public void clearVisible() {
        visible = new TimeseriesDataset<ContinuousInstance>(1);
    }

    @Override
    public void setDataset(Dataset<? extends Instance> dataset) {
        clearVisible();
        setVisible((Timeseries<? extends ContinuousInstance>) dataset);
    }

    public final void setVisible(Timeseries<? extends ContinuousInstance> dataset) {
        if (visible == null || visible.isEmpty()) {
            visible = (TimeseriesDataset<? super ContinuousInstance>) dataset;
            setMax(visible.getMax());
            setMin(visible.getMin());
        } else {
            if (visible.equals(dataset)) {
                return; //data are already in set (it would be endless loop)
            }
            //one by one... here they come
            for (ContinuousInstance item : dataset) {
                setVisible(item);
            }
        }
        updateTimePoints();
    }

    public TimePoint getTimePoint(int idx) {
        return (TimePoint) visible.getAttribute(idx);
    }

    @Override
    public long getTimeAt(int index) {
        if (index < 0 || index > cntTimePoints) {
            return 0;
//            throw new ArrayIndexOutOfBoundsException("index not present " + index);
        }
        return getTimePoint(index).getTimestamp();
    }

    public long getFirstTime() {
        return getTimeAt(0);
    }

    /**
     * Maximum value on X axis
     *
     * @return
     */
    public double getLastX() {
        return this.lastX;
    }

    /**
     * Normalize all instances to given DataCell's value
     *
     * @param idx
     */
    public void normalize(int idx) {
        for (ContinuousInstance inst : visible) {
            inst.normalize(idx);
            checkMax(inst.getMax());
            checkMin(inst.getMin());
        }
        fireDataNormalized();
    }

    /**
     * Reduce amount of data in dataset
     *
     * @param begin - first index included in new dataset
     * @param end   - last index included in new dataset
     * @param ph
     */
    public void crop(int begin, int end, ProgressHandle ph) {
        cntTimePoints = 0;
        start = 0;
        startX = 0;
        visible.crop(begin, end, ph);
        cntTimePoints = visible.attributeCount();
        fireDatasetCropped(new DatasetEvent(this, (Dataset) visible));
    }

    public void setVisibleRange(Range r) {
        visibleRange = r;
    }

    @Override
    public void checkMax(double value) {
        if (value > this.maxY) {
            maxY = value;
        }
    }

    @Override
    public void checkMin(double value) {
        if (value < this.minY) {
            minY = value;
        }
    }

    @Override
    public void setMin(double min) {
        this.minY = min;
    }

    @Override
    public void setMax(double max) {
        this.maxY = max;
    }

    public Range getVisibleRange() {
        return visibleRange;
    }

    @Override
    public boolean isVisibleNull() {
        return visible == null;
    }

    /**
     * Get last rendered item in graph
     *
     * @return
     */
    @Override
    public int getFinish() {
        return finish;
    }

    @Override
    public Chart getChart() {
        return chart;
    }

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public boolean isChartNull() {
        return chart == null;
    }

    /**
     *
     * @return list of indexes that should be marked in chart
     */
    @Override
    public List<Integer> getDateValues() {
        List<Integer> list = new ArrayList<Integer>();
        if (!isVisibleNull()) {
            /*
             * Calendar cal = Calendar.getInstance();
             * cal.setFirstDayOfWeek(Calendar.MONDAY);
             * cal.setTimeInMillis(this.getFirstTime());
             */

            /*
             * if (!getInterval().isIntraDay()) { int month =
             * cal.get(Calendar.MONTH); for (int i = 0; i <
             * getVisible().getItemsCount(); i++) {
             * cal.setTimeInMillis(getVisible().getTimeAt(i)); if (month !=
             * cal.get(Calendar.MONTH)) { list.add(new Float(i)); month =
             * cal.get(Calendar.MONTH); } } } else {
             */
            for (int i = 0; i < visible.attributeCount(); i++) {
                if (i % 10 == 0) {
                    list.add(i);
                }
            }
            //}
        }
        return list;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    /**
     * From this point we start to render points in graph
     *
     * @return number which is substracted from absolute coordinate
     */
    public double getStartX() {
        return startX;
    }

    @Override
    public List<Float> getIndexValues(Rectangle rect, Range range) {
        List<Float> values = new ArrayList<Float>();
        double diff = (range.getUpperBound() - range.getLowerBound());
        if (diff > 10) {
            int step = (int) (diff / 10) + 1;
            double low = Math.ceil(range.getUpperBound() - (diff / 10) * 9);

            for (double i = low; i <= range.getUpperBound(); i += step) {
                values.add(new Float(i));
            }
        } else {
            double step = diff / 10;
            if (step == 0.0) {
                return values;
            }
            double uppper = range.getUpperBound();
            double lower = range.getLowerBound();
            for (double i = lower; i <= uppper; i += step) {
                values.add(new Float(i));
            }
        }
        return values;
    }

    /**
     * Count position on axis X in chart
     *
     * @param index of timePoint
     * @param rect  area to which we render graph
     * @return
     */
    @Override
    public double getX(int index, Rectangle rect) {
        return getXFromRatio(getPositionAt(index), rect, zoom);
    }

    public double getX(int index, Rectangle rect, double zoomFact) {
        return getXFromRatio(getPositionAt(index), rect, zoomFact);
    }

    public Point2D getPoint(double x, double y, Range range, Rectangle rect) {
        return new Point2D.Double(absoluteX(x, rect, zoom), getY(y, rect, range));
    }

    @Override
    public Point2D getPoint(int i, double value1, Range range, Rectangle bounds, boolean isLog) {
        if (isLog) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        return getPoint(maxY, maxY, range, bounds);
    }

    /**
     * Counts X coordinate in chart, with scrollbar transition
     *
     * @param time sice start of experiment
     * @param rect
     * @return X coordinate in graph, where [0;0] is in left top corner
     */
    public double xFromTime(long time, Rectangle rect, double zoomFact) {
        return absoluteX(time, rect, zoomFact) - startX;
    }

    /**
     * Simplified counting with precomputed position
     *
     * @param ratio is position in visible graph
     * @param rect
     * @return X coordinate in graph, where [0;0] is in left top corner
     */
    public double getXFromRatio(double ratio, Rectangle rect, double zoomFact) {
        return (absoluteX(ratio, rect, zoomFact) - startX);
    }

    @Override
    public double getXFromRatio(double ratio, Rectangle rect) {
        return getXFromRatio(ratio, rect, zoom);
    }

    @Override
    public double getXFromTime(long time, Rectangle bounds) {
        return (absoluteX(time, bounds, zoom) - startX);
    }

    /**
     * Absolute position on axis X, does not count with scrolling over graph
     *
     * @param time     - time since start of experiment in miliseconds
     * @param rect     - dimension of visible area
     * @param zoomFact
     * @return
     */
    private double absoluteX(long time, Rectangle rect, double zoomFact) {
        return ((((double) time / (double) lastTimePoint) * zoomFact * rect.getWidth()));
    }

    /**
     * Absolute position on axis X, does not count with scrolling over graph
     *
     * @param ratio    - precomputed ration between time / lastTimePoint < 0;1 >
     * @param rect
     * @param zoomFact
     * @return
     */
    private double absoluteX(double ratio, Rectangle rect, double zoomFact) {
        return ((ratio * zoomFact * rect.getWidth()));
    }

    @Override
    public double getY(double value, Rectangle rect, Range range) {
        return (rect.getMinY() + (range.getUpperBound() - value) / (range.getUpperBound() - range.getLowerBound()) * rect.getHeight());
    }

    @Override
    public double getY(double value, Rectangle bounds, Range range, boolean axisLogarithmicFlag) {
        if (axisLogarithmicFlag) {
            throw new UnsupportedOperationException("not supported yet");
        }
        return getY(value, bounds, range);
    }

    public int getIndex(int x, Rectangle rect) {
        return getIndex(x, 1, rect);
    }

    public int getIndex(int x, int y, Rectangle rect) {
        return getIndex(new Point(x, y), rect);
    }

    public double revertXtoRatio(double position, Rectangle rect, double z) {
        return (position / (z * rect.getWidth()));
    }

    /**
     * Return TimePoint index of point closest to clicked area
     *
     * @param p
     * @param rect
     * @return index
     */
    @Override
    public int getIndex(Point p, Rectangle rect) {
        // % position in graph inside visible area
        return findIndex(p.x, rect);
    }

    /**
     * Converts position in graph to time points index, is restricted by
     * currently rendered interval
     *
     * @param x
     * @param rect
     * @return
     */
    @Override
    public int findIndex(double x, Rectangle rect) {
        double coordX = revertXtoRatio(x + getStartX(), rect, zoom);
        return BinarySearch.search(visible.getTimePoints(), start, finish, coordX);
    }

    /**
     * *
     * Computes position on axis Y for
     *
     * @param sampleId
     * @param x
     * @param interpolator
     * @return
     */
    public double interpolate(int sampleId, double x, Interpolator interpolator) {
        return visible.interpolate(sampleId, x, interpolator);
    }

    /**
     * Looks outside of visible area - might search through whole array
     *
     * @param x
     * @param rect
     * @param start
     * @param finish
     * @return
     */
    public int findAbsoluteIndex(double x, Rectangle rect, int start, int finish) {
        double coordX = revertXtoRatio(x, rect, zoom);
        return BinarySearch.search(visible.getTimePoints(), start, finish, coordX);
    }

    @Override
    public long getTimeFromX(double x, Rectangle rect) {
        int idx = findIndex(x, rect);
        return getTimePoint(idx).getTimestamp();
    }

    /**
     * Visible range on axis Y
     *
     * @param chartConfig
     * @param overlays
     */
    @Override
    public void calculateRange(ChartConfig chartConfig, List<Overlay> overlays) {
        Range range = new Range();
        if (!isVisibleNull() && !getVisible().isEmpty()) {
            //  System.out.println("eq: " +minY+"- ("+maxY+"-"+minY+")");
            double x = minY - (maxY - minY) * 0.01;
            double y = maxY + (maxY - minY) * 0.01;
            //   System.out.println("setttin new range " +x+", "+y);
            range = new Range(x, y);
            /*
             * if (overlays != null && !overlays.isEmpty()) { for
             * (AbstractOverlay overlay : overlays) { if
             * (overlay.isIncludedInRange()) { Range oRange =
             * overlay.getRange(chartConfig, overlay.getPrice()); if (oRange !=
             * null) { range = Range.combine(range, oRange); } } } }
             */
        }
        setVisibleRange(range);
    }
    private transient EventListenerList datasetListeners = new EventListenerList();

    @Override
    public void addDatasetListener(DatasetListener listener) {
        if (datasetListeners == null) {
            datasetListeners = new EventListenerList();
        }
        datasetListeners.add(DatasetListener.class, listener);
    }

    public void removeDatasetListeners(DatasetListener listener) {
        if (datasetListeners == null) {
            datasetListeners = new EventListenerList();
            return;
        }
        datasetListeners.remove(DatasetListener.class, listener);
    }

    public void removeAllDatasetListeners() {
        if (datasetListeners == null) {
            datasetListeners = new EventListenerList();
            return;
        }
        DatasetListener[] listeners = datasetListeners.getListeners(DatasetListener.class);
        for (DatasetListener listener : listeners) {
            datasetListeners.remove(DatasetListener.class, listener);
        }
    }
    private transient EventListenerList indicatorsDatasetListeners = new EventListenerList();
    private transient EventListenerList overlaysDatasetListeners = new EventListenerList();

    public void addIndicatorsDatasetListeners(DatasetListener listener) {
        if (indicatorsDatasetListeners == null) {
            indicatorsDatasetListeners = new EventListenerList();
        }
        indicatorsDatasetListeners.add(DatasetListener.class, listener);
    }

    public void removeIndicatorsDatasetListeners(DatasetListener listener) {
        if (indicatorsDatasetListeners == null) {
            indicatorsDatasetListeners = new EventListenerList();
            return;
        }
        indicatorsDatasetListeners.remove(DatasetListener.class, listener);
    }

    public void removeAllIndicatorsDatasetListeners() {
        if (indicatorsDatasetListeners == null) {
            indicatorsDatasetListeners = new EventListenerList();
            return;
        }
        DatasetListener[] listeners = indicatorsDatasetListeners.getListeners(DatasetListener.class);
        for (DatasetListener listener : listeners) {
            indicatorsDatasetListeners.remove(DatasetListener.class, listener);
        }
    }

    public void addOverlaysDatasetListeners(DatasetListener listener) {
        if (overlaysDatasetListeners == null) {
            overlaysDatasetListeners = new EventListenerList();
        }
        overlaysDatasetListeners.add(DatasetListener.class, listener);
    }

    public void removeOverlaysDatasetListeners(DatasetListener listener) {
        if (overlaysDatasetListeners == null) {
            overlaysDatasetListeners = new EventListenerList();
            return;
        }
        overlaysDatasetListeners.remove(DatasetListener.class, listener);
    }

    public void removeAllOverlaysDatasetListeners() {
        if (overlaysDatasetListeners == null) {
            overlaysDatasetListeners = new EventListenerList();
            return;
        }
        DatasetListener[] listeners = overlaysDatasetListeners.getListeners(DatasetListener.class);
        for (DatasetListener listener : listeners) {
            overlaysDatasetListeners.remove(DatasetListener.class, listener);
        }
    }
    transient protected EventListenerList markerListener;

    protected EventListenerList eventListenerList() {
        if (markerListener == null) {
            markerListener = new EventListenerList();
        }
        return markerListener;
    }

    public void removeMarkerListener(NormalizationListener listener) {
        eventListenerList().remove(NormalizationListener.class, listener);
    }

    public void fireMarkerMoved(NormalizationEvent evt) {
        NormalizationListener[] listeners = eventListenerList().getListeners(NormalizationListener.class);
        for (NormalizationListener listener : listeners) {
            listener.markerMoved(evt);
        }
    }

    public void addNormalizationListener(NormalizationListener listener) {
        eventListenerList().add(NormalizationListener.class, listener);
    }

    public void fireDataNormalized() {
        NormalizationListener[] listeners = eventListenerList().getListeners(NormalizationListener.class);
        for (NormalizationListener listener : listeners) {
            listener.normalizationCompleted();
        }
    }

    public boolean fireDatasetCropped(DatasetEvent evt) {
        DatasetListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DatasetListener.class);
            for (DatasetListener listener : listeners) {
                listener.datasetCropped(evt);
            }
        }
        return true;
    }

    public boolean fireDatasetChanged(DatasetEvent evt) {
        DatasetListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DatasetListener.class);
            for (DatasetListener listener : listeners) {
                listener.datasetChanged(evt);
            }
        }

        if (indicatorsDatasetListeners != null) {
            listeners = indicatorsDatasetListeners.getListeners(DatasetListener.class);
            for (DatasetListener listener : listeners) {
                listener.datasetChanged(evt);
            }
        }

        if (overlaysDatasetListeners != null) {
            listeners = overlaysDatasetListeners.getListeners(DatasetListener.class);
            for (DatasetListener listener : listeners) {
                listener.datasetChanged(evt);
            }
        }
        return true;
    }

    public void setSavedOverlays(List<Overlay> list) {
        savedOverlays = list;
    }

    public List<Overlay> getSavedOverlays() {
        return savedOverlays;
    }

    public void clearSavedOverlays() {
        savedOverlays.clear();
    }

    public void setAnnotationsCount(List<Integer> list) {
        annotationsCount = list;
    }

    public List<Integer> getAnnotationsCount() {
        return annotationsCount;
    }

    public void clearAnnotationsCount() {
        annotationsCount.clear();
    }

    public void setAnnotationotation(ArrayList<Annotation> list) {
        annotations = list;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void clearAnnotations() {
        annotations.clear();
    }

    @Override
    public void chartChanged(Chart newChart) {
        setChart(newChart);
    }

    @Override
    public void overlayAdded(Overlay overlay) {
    }

    @Override
    public void overlayRemoved(Overlay overlay) {
    }

    @Override
    public double zoomIn(Rectangle rect) {
        zoom += 0.1;
        ///does not work on small datasets
        double pos = startX + (finishX - startX) * 0.01;
        int idx = findIndex(pos, rect);
        setStart(idx, rect); //TODO count displayed items and move to 10% value

        return zoom;
    }

    @Override
    public double zoomOut(Rectangle rect) {
        double newZoom = zoom - 0.1;
        double fin = xFromTime(lastTimePoint, rect, newZoom) - startX;
        if (fin > rect.getWidth()) {
            zoom -= 0.1;
        } else {
            if (getStartX() > 0) {
                zoom -= 0.1;
                setStart(start - 10, rect); //move chart to left, if possible
            }
        }

        return zoom;
    }

    public double getZoom() {
        return zoom;
    }

    /**
     * zoom = 1.0 means 100%
     *
     * @param z
     */
    public void setZoom(double z) {
        this.zoom = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ChartDataImpl)) {
            return false;
        }
        ChartDataImpl that = (ChartDataImpl) obj;
        return visible.equals(that.getVisible());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.visible != null ? this.visible.hashCode() : 0);
        return hash;
    }

    @Override
    public Insets getDataInsets() {
        return dataOffset;
    }

    @Override
    public Dataset<? extends Instance> getDataset() {
        return visible;
    }

}
