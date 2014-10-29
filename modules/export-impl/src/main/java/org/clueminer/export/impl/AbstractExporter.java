package org.clueminer.export.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractExporter implements ActionListener, PropertyChangeListener {

    protected static final RequestProcessor RP = new RequestProcessor("Export");
    protected RequestProcessor.Task task;
    protected ClusterAnalysis analysis;
    protected static final String prefKey = "last_folder";
    protected DialogDescriptor d = null;

    public void setAnalysis(ClusterAnalysis analysis) {
        this.analysis = analysis;
    }

    public abstract JPanel getOptions();

    public abstract String getTitle();

    public abstract void updatePreferences(Preferences p);

    public abstract void export(Preferences p);

    public void showDialog() {

        d = new DialogDescriptor(getOptions(), "Export", true, NotifyDescriptor.OK_CANCEL_OPTION,
                                 NotifyDescriptor.OK_CANCEL_OPTION,
                                 DialogDescriptor.BOTTOM_ALIGN, null, this);

        d.setClosingOptions(new Object[]{});
        d.addPropertyChangeListener(this);
        DialogDisplayer.getDefault().notifyLater(d);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.OK_OPTION) {
            Preferences p = NbPreferences.root().node("/clueminer/exporter");
            updatePreferences(p);
            export(p);

            d.setClosingOptions(null);
        } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            d.setClosingOptions(null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        //not much to do
    }

}
