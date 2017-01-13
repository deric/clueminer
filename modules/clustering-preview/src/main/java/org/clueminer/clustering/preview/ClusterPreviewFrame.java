/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.clustering.preview;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.attributes.TimePointAttribute;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.TimeseriesDataset;
import org.openide.util.Task;
import org.openide.util.TaskListener;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClusterPreviewFrame<E extends Instance, C extends Cluster<E>>
        extends JPanel implements Serializable, ActionListener, AdjustmentListener, ChangeListener, TaskListener {

    private static final long serialVersionUID = -8719504995316248781L;
    private JScrollPane scroller;
    private PreviewFrameSet previewSet;
    private JSlider chartSizeSlider;
    private JToolBar toolbar;
    private JCheckBox chckScale;
    private JButton btnChooseMeta;
    private final int minChartHeight = 150;
    private final int maxChartHeight = 650;
    private final MetaLoaderDialog loader = new MetaLoaderDialog(this);
    private Logger logger = Logger.getLogger(ClusterPreviewFrame.class.getName());

    public ClusterPreviewFrame() {
        initComponents();
    }

    private void initComponents() {
        //setLayout(new GridBagLayout());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        previewSet = new PreviewFrameSet(this);

        chartSizeSlider = new JSlider(SwingConstants.HORIZONTAL);
        chartSizeSlider.setMinimum(minChartHeight);
        chartSizeSlider.setMaximum(maxChartHeight);
        chartSizeSlider.addChangeListener(this);
        chartSizeSlider.setMaximumSize(new Dimension(250, 20));
        chartSizeSlider.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        btnChooseMeta = new JButton("choose meta-data...");
        btnChooseMeta.addActionListener(loader);

        toolbar = new JToolBar(SwingConstants.HORIZONTAL);
        JLabel label = new JLabel(java.util.ResourceBundle.getBundle("org/clueminer/clustering/preview/Bundle").getString("CHART HEIGHT:"));
        toolbar.add(label);
        toolbar.add(chartSizeSlider);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.add(btnChooseMeta);
        chckScale = new JCheckBox("use global scale");
        chckScale.setSelected(true);
        toolbar.add(chckScale);
        chckScale.addActionListener(this);

        scroller = new JScrollPane(previewSet);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.setVisible(true);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getVerticalScrollBar().addAdjustmentListener(this);

        add(toolbar);
        add(scroller);
        scroller.getViewport().revalidate();
    }

    public PreviewFrameSet getViewer() {
        return previewSet;
    }

    @Override
    public void repaint() {
        if (scroller != null) {
            scroller.getViewport().revalidate();
            super.repaint();
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        //System.out.println("clust preview adjusted");
        //scroller.getViewport().revalidate();
    }

    public void setClustering(Clustering<E, C> clustering) {
        previewSet.setClustering(clustering);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            previewSet.setChartHeight(chartSizeSlider.getValue());
        }
    }

    public int getChartSize() {
        return chartSizeSlider.getValue();
    }

    public void setChartSize(int size) {
        if (size > 0) {
            chartSizeSlider.setValue(size);
            previewSet.setChartHeight(size);
        }
    }

    /**
     * Loading meta-data finished
     *
     * @param task
     */
    @Override
    public void taskFinished(Task task) {
        logger.log(Level.INFO, "meta data loading finished");
        Dataset<? extends Instance>[] result = loader.getDatasets();
        HashMap<Integer, Instance> metaMap = new HashMap<>(3000);
        int id;
        double ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
        double xmax = Double.MIN_VALUE;
        TimeseriesDataset<ContinuousInstance> ts;
        TimePointAttribute timeAttr;
        if (result != null) {
            for (Dataset<? extends Instance> d : result) {
                if (d != null) {
                    ts = (TimeseriesDataset<ContinuousInstance>) d;
                    if (ts.getMax() > ymax) {
                        ymax = ts.getMax();
                    }

                    if (ts.getMin() < ymin) {
                        ymin = ts.getMin();
                    }
                    timeAttr = (TimePointAttribute) d.getAttribute(d.attributeCount() - 1);
                    if (timeAttr.getPosition() > xmax) {
                        xmax = timeAttr.getPosition();
                    }
                    //Dump.array(((TimeseriesDataset<ContinuousInstance>) d).getTimePointsArray(), "timepoints ");
                    for (Instance inst : d) {
                        //id = Integer.valueOf(inst.getId());
                        id = Integer.valueOf((String) inst.classValue());
                        metaMap.put(id, (Instance) inst);
                    }
                } else {
                    logger.log(Level.WARNING, "dataset d null!!!");
                }

            }
            previewSet.setYmax(ymax);
            previewSet.setYmin(ymin);
            previewSet.setXmax(xmax);
        }
        previewSet.setMetaMap(metaMap);
        previewSet.setMetaColors(loader.getColors());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.log(Level.INFO, "global scale {0}", chckScale.isSelected());
        previewSet.setGlobalScale(chckScale.isSelected());
    }

}
