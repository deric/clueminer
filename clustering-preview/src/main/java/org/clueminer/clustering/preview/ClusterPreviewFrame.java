package org.clueminer.clustering.preview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public class ClusterPreviewFrame extends JPanel implements Serializable, AdjustmentListener {

    private static final long serialVersionUID = -8719504995316248781L;
    private JScrollPane scroller;
    private PreviewFrameSet previewSet;

    public ClusterPreviewFrame() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        previewSet = new PreviewFrameSet(this);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;

        scroller = new JScrollPane(previewSet);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.setVisible(true);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.getVerticalScrollBar().addAdjustmentListener(this);


        scroller.getViewport().revalidate();
        add(scroller, c);
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

    public void setClustering(Clustering<Cluster> clustering) {
        previewSet.setClustering(clustering);
    }
}
