package org.clueminer.wellmap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        scale = new ColorScale(new ColorScheme());
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
        ColorPalette palette = scale.getPalette();
        palette.setRange(p.getMin(), p.getMax());
        System.out.println("min = " + p.getMin() + ", max = " + p.getMax());
        if (metrics != null) {
            for (HtsInstance inst : p) {
                // try {
                //Object v = metrics.invoke(p);
                //double value = Double.valueOf(v.toString());
                inst.setColor(palette.getColor(inst.getMax()));
                /* } catch (IllegalAccessException ex) {
                 Exceptions.printStackTrace(ex);
                 } catch (IllegalArgumentException ex) {
                 Exceptions.printStackTrace(ex);
                 } catch (InvocationTargetException ex) {
                 Exceptions.printStackTrace(ex);
                 }*/
            }
        } else {
            System.err.println("no metric defined");
        }

    }

    public void setSelected(HtsPlate p) {
        frame.setSelected(p);
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
}
