/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.evaluation.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;
import org.clueminer.clustering.api.Clustering;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.clueminer.evaluation.gui//Sorting//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SortingTopComponent",
        iconBase = "org/clueminer/evaluation/gui/sorting16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Window", id = "org.clueminer.evaluation.gui.SortingTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SortingAction",
        preferredID = "SortingTopComponent"
)
@Messages({
    "CTL_SortingAction=Sorting",
    "CTL_SortingTopComponent=Sorting Window",
    "HINT_SortingTopComponent=This is a Sorting window"
})
public final class SortingTopComponent extends TopComponent implements LookupListener {

    private final SortingPanel frame;
    private Lookup.Result<Clustering> result = null;

    public SortingTopComponent() {
        initComponents();
        setName(Bundle.CTL_SortingTopComponent());
        setToolTipText(Bundle.HINT_SortingTopComponent());
        frame = new SortingPanel();
        add(frame, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Clustering.class);
        result.addLookupListener(this);
        resultChanged(new LookupEvent(result));
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        result = null;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Clustering> allClusterings = result.allInstances();
        //unless we have at least 2 clustering there's nothing to compare
        if (allClusterings.size() > 1) {
            if (frame.getClusterings() != null) {
                if (allClusterings.size() == frame.getClusterings().size() && allClusterings.equals(frame.getClusterings())) {
                    return;
                }
            }
            frame.setClusterings(allClusterings);
            frame.revalidate();
            frame.validate();
            frame.repaint();

        }
    }
}
