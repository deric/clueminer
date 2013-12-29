package org.clueminer.clustering.selection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;

/**
 *
 * @author Tomas Barton
 */
public class SelectionFrame extends JPanel {

    private Dataset<? extends Instance> dataset;
    private Dimension dimChart;
    private static final Logger logger = Logger.getLogger(SelectionFrame.class.getName());
    private int minWidth = 100;

    public SelectionFrame() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.red);
    }

    private void redraw() {
        //remove all components
        this.removeAll();

        Instance inst;

        if (dataset != null && dataset.size() > 0) {
            logger.log(Level.INFO, "dataset size {0}", dataset.size());
            inst = dataset.instance(0);
            /**
             * @TODO We can't support visualization of all possible
             * kinds of data, this ability should be implemented
             * elsewhere (dataset itself or a visualization
             * controller...)
             */
            //logger.log(Level.INFO, "dataset is kind of {0}", dataset.getClass().toString());
            //logger.log(Level.INFO, "instace is kind of {0}", inst.getClass().toString());
            while (inst.getAncestor() != null) {
                inst = inst.getAncestor();
            }

            Plotter plot = inst.getPlotter();
            if (dataset.size() > 1) {
                for (int k = 1; k < dataset.size(); k++) {
                    inst = dataset.instance(k);
                    while (inst.getAncestor() != null) {
                        inst = inst.getAncestor();
                    }
                    plot.addInstance(inst);
                    //logger.log(Level.INFO, "sample id {0}, name = {1}", new Object[]{inst.classValue(), inst.getName()});
                }
            }

            if (dimChart == null) {
                dimChart = new Dimension(minWidth, 100);
            }
            plot.setMinimumSize(dimChart);
            plot.setPreferredSize(dimChart);
            this.setPreferredSize(dimChart);
            plot.setTitle(dataset.getName());
            add((JComponent) plot, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            revalidate();
            logger.log(Level.INFO, "preview size " + dimChart.toString());
            super.repaint();
        }

    }

    public Dataset<? extends Instance> getDataset() {
        return dataset;
    }

    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
        redraw();
    }

}
