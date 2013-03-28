package org.clueminer.clustering.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Barton
 */
public class AlgorithmDialog implements ActionListener, PropertyChangeListener {

    private DialogDescriptor d = null;
    private ClusteringDialog config = null;
    private ClusterAnalysis analysis;
    private static final RequestProcessor RP = new RequestProcessor("Clustering");
    private RequestProcessor.Task task;

    public void showDialog(ClusterAnalysis clust, ClusteringDialog config) {
        this.config = config;
        config.setParent(clust);
        analysis = clust;
        analysis.setAlgorithm(config.getAlgorithm());
        d = new DialogDescriptor(config, config.getAlgorithm().getName(), true, NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.OK_CANCEL_OPTION,
                DialogDescriptor.BOTTOM_ALIGN, null, this);

        d.setClosingOptions(new Object[]{});
        d.addPropertyChangeListener(this);
        DialogDisplayer.getDefault().notifyLater(d);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        System.out.println(event);
        if (event.getSource() == DialogDescriptor.OK_OPTION) {
            task = RP.create(new ClusteringRunner(analysis, config));
            task.addTaskListener(analysis);
            task.schedule(0);

            d.setClosingOptions(null);
        } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            d.setClosingOptions(null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(DialogDescriptor.PROP_VALUE)
                && pce.getNewValue() == DialogDescriptor.OK_OPTION) {
            task = RP.create(new ClusteringRunner(analysis, config));
            task.addTaskListener(analysis);
            task.schedule(0);
        }
    }
}
