package org.clueminer.dendrogram.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;

/**
 *
 * @author Tomas Barton
 */
public class CutoffSlider extends JPanel implements DendrogramDataListener, TreeListener, ChangeListener {

    private static final long serialVersionUID = 660065260598083707L;
    private JSlider slider;
    /**
     * horizontal or vertical
     * @see SwingConstants.HORIZONTAL
     */
    private int orientation;
    private CutoffLine cutoffLine;
    private JPanel parent;

    /**
     * 
     * @param panel
     * @param orientation -- SwingConstants.HORIZONTAL
     * @param cutoff 
     */
    public CutoffSlider(JPanel panel, int orientation, CutoffLine cutoff) {
        this.parent = panel;
        this.orientation = orientation;
        this.cutoffLine = cutoff;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(parent.getBackground());

        GridBagConstraints noFill = new GridBagConstraints();
        noFill.anchor = GridBagConstraints.WEST;
        noFill.fill = GridBagConstraints.NONE;

        GridBagConstraints verticalFill = new GridBagConstraints();
        verticalFill.anchor = GridBagConstraints.NORTH;
        verticalFill.fill = GridBagConstraints.VERTICAL;
        verticalFill.weighty = 1.0;

        slider = new JSlider(orientation);
        slider.setMinimum((int) cutoffLine.getMinDistance());
        slider.setMaximum((int) cutoffLine.getMaxDistance());
        slider.setValue(cutoffLine.getLinePosition());
        slider.addChangeListener(this);
        slider.setBackground(parent.getBackground());


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 1;
        add(Box.createVerticalGlue(), verticalFill);
        add(slider, c);
    }

    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
    }

    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //we don't care
    }

    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //we don't care
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        //we don't care
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
        int max = source.getMaxDistance();
        int min = source.getMinDistance();

        slider.setMinimum(min);
        slider.setMaximum(max);
        slider.setValue(cutoffLine.getLinePosition());

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        cutoffLine.setCutoff(slider.getValue(), slider.getValueIsAdjusting());
    }
}
