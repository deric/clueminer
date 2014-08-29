package org.clueminer.attrstats;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public class DistributionFrame extends JPanel implements Serializable, AdjustmentListener, ChangeListener, ActionListener {

    private Clustering<Cluster> clustering;
    private JScrollPane scroller;
    private BoxPlotSet boxPlotSet;
    private JSlider chartSizeSlider;
    private JComboBox comboAttr;
    private JToolBar toolbar;
    private final int minChartHeight = 150;
    private final int maxChartHeight = 650;

    public DistributionFrame() {
        initComponents();
    }

    public Clustering<Cluster> getClustering() {
        return clustering;
    }

    public void setClustering(Clustering<Cluster> clust) {
        this.clustering = clust;
        boxPlotSet.setClustering(clustering);
        updateAttributes(clust);
    }

    private void updateAttributes(Clustering<Cluster> clust) {
        if (clust.size() > 0) {
            Dataset<? extends Instance> d = clust.get(0);
            for (int i = 0; i < d.attributeCount(); i++) {
                comboAttr.addItem(d.getAttribute(i).getName());
            }
        }
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        boxPlotSet = new BoxPlotSet(this);

        chartSizeSlider = new JSlider(SwingConstants.HORIZONTAL);
        chartSizeSlider.setMinimum(minChartHeight);
        chartSizeSlider.setMaximum(maxChartHeight);
        chartSizeSlider.addChangeListener(this);
        chartSizeSlider.setMaximumSize(new Dimension(250, 20));
        chartSizeSlider.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        toolbar = new JToolBar(SwingConstants.HORIZONTAL);
        JLabel label = new JLabel(java.util.ResourceBundle.getBundle("org/clueminer/attrstats/Bundle").getString("CHART HEIGHT:"));
        toolbar.add(label);
        toolbar.add(chartSizeSlider);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboAttr = new JComboBox();
        comboAttr.setMaximumSize(new Dimension(250, 30));
        comboAttr.addActionListener(this);
        toolbar.add(comboAttr);

        scroller = new JScrollPane(boxPlotSet);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.setVisible(true);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getVerticalScrollBar().addAdjustmentListener(this);

        add(toolbar);
        scroller.getViewport().revalidate();
        add(scroller);
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

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            boxPlotSet.setChartHeight(chartSizeSlider.getValue());
        }
    }

    public int getChartSize() {
        return chartSizeSlider.getValue();
    }

    public void setChartSize(int size) {
        if (size > 0) {
            chartSizeSlider.setValue(size);
            boxPlotSet.setChartHeight(size);
        }
    }

    /**
     * Combo box changed
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("combo changed" + e.toString());
        boxPlotSet.setAttributeIndex(comboAttr.getSelectedIndex());
    }
}
