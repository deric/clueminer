package org.clueminer.wellmap;

import com.google.common.collect.MinMaxPriorityQueue;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import javax.swing.JPanel;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.clueminer.gui.ColorPalette;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class WellMapExtended extends JPanel implements DatasetListener, Serializable, TreeListener {

    private static final long serialVersionUID = -236150462081587319L;
    private WellMapFrame frame;
    private ColorScale scale;
    private Method metrics;
    private HtsPlate<HtsInstance> plate;
    private HashSet<Integer> ignoredRows = new HashSet<Integer>(2);
    private HashSet<Integer> ignoredColumns = new HashSet<Integer>(2);

    public WellMapExtended() {
        initialize();
    }

    private void initialize() {
        this.setLayout(new GridBagLayout());
        //wells        
        frame = new WellMapFrame();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        //c.gridwidth = GridBagConstraints.RELATIVE;
        //c.gridheight = GridBagConstraints.REMAINDER; //last in column
        c.insets = new java.awt.Insets(0, 5, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        add(frame, c);
        //color scheme visualization
        scale = new HorizontalScale(new ColorScheme());
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 1;
        add(scale, c);
    }

    @Override
    public void datasetChanged(DatasetEvent evt) {
        frame.datasetChanged(evt);
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        frame.datasetClosed(evt);
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        //@TODO clear cached data
        frame.datasetClosed(evt);
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        frame.datasetCropped(evt);
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        frame.clusterSelected(source, cluster, data);
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
        frame.treeUpdated(source, width, height);
    }

    public void setPlate(HtsPlate<HtsInstance> p) {
        frame.setPlate(p);
        this.plate = p;
    }

    public HtsPlate<HtsInstance> getPlate() {
        return plate;
    }

    public void setSelected(HtsPlate<HtsInstance> p, int timepoint) {
        ColorPalette palette = scale.getPalette();

        //find min-max values in selection
        MinMaxPriorityQueue<Double> pq = MinMaxPriorityQueue.<Double>create();
        for (HtsInstance inst : p) {
            if (!isIgnored(inst.getRow(), inst.getColumn())) {
                pq.add(inst.value(timepoint));
            }
        }
        System.out.println("min = " + pq.peekFirst() + ", max = " + pq.peekLast());
        palette.setRange(pq.peekFirst(), pq.peekLast());
        //  if (metrics != null) {
        for (HtsInstance inst : p) {
            // try {
            //Object v = metrics.invoke(p);
            //double value = Double.valueOf(v.toString());
            if (!isIgnored(inst.getRow(), inst.getColumn())) {
                inst.setColor(palette.getColor(inst.value(timepoint)));
            } else {
                inst.setColor(Color.GRAY);
            }
            /* } catch (IllegalAccessException ex) {
             Exceptions.printStackTrace(ex);
             } catch (IllegalArgumentException ex) {
             Exceptions.printStackTrace(ex);
             } catch (InvocationTargetException ex) {
             Exceptions.printStackTrace(ex);
             }*/
        }
        /*   } else {
         System.err.println("no metric defined");
         }*/
        frame.setSelected(p);
    }

    /**
     * Check whether well at given position is at blacklist
     *
     * @param row
     * @param column
     * @return
     */
    private boolean isIgnored(int row, int column) {
        if (!ignoredColumns.isEmpty() && ignoredColumns.contains(column)) {
            return true;
        }

        if (!ignoredRows.isEmpty() && ignoredRows.contains(row)) {
            return true;
        }

        return false;
    }

    public void setMetric(String method, HtsPlate<HtsInstance> p) {
        HtsInstance inst = p.instance(0);
        if (inst != null) {
            try {
                metrics = inst.getClass().getMethod(method);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SecurityException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public HashSet<Integer> getIgnoredRows() {
        return ignoredRows;
    }

    public void setIgnoredRows(HashSet<Integer> ignoredRows) {
        this.ignoredRows = ignoredRows;
    }

    public HashSet<Integer> getIgnoredColumns() {
        return ignoredColumns;
    }

    public void setIgnoredColumns(HashSet<Integer> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public int wellPosToId(int row, int column) {
        return frame.wellPosToId(row, column);
    }
    
    public String numberToRowLabel(int row){
        return frame.numberToRowLabel(row);
    }
}
