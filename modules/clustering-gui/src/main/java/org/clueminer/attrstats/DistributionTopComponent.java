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
package org.clueminer.attrstats;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Clustering;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 * Top component which displays data distribution.
 */
@ConvertAsProperties(
        dtd = "-//org.clueminer.attrstats//Distribution//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "DistributionTopComponent",
        iconBase = "org/clueminer/clustering/preview/histogram16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "rightSlidingSide", openAtStartup = false)
@ActionID(category = "Window", id = "org.clueminer.attrstats.DistributionTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DistributionAction",
        preferredID = "DistributionTopComponent"
)
@Messages({
    "CTL_DistributionAction=Distribution",
    "CTL_DistributionTopComponent=Distribution Window",
    "HINT_DistributionTopComponent=This is a Distribution window"
})
public final class DistributionTopComponent extends TopComponent implements LookupListener {

    private DistributionFrame frame;
    private final InstanceContent content = new InstanceContent();
    private Lookup.Result<Clustering> result = null;
    private static final Logger logger = Logger.getLogger(DistributionTopComponent.class.getName());

    public DistributionTopComponent() {
        initComponents();
        associateLookup(new AbstractLookup(content));
        setName(Bundle.CTL_DistributionTopComponent());
        setToolTipText(Bundle.HINT_DistributionTopComponent());
        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);
        frame = new DistributionFrame();
        add(frame, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
    public void resultChanged(LookupEvent le) {
        Collection<? extends Clustering> allClusterings = result.allInstances();
        logger.log(Level.INFO, "clustering lookup: got {0} clusterings", allClusterings.size());
        for (Clustering c : allClusterings) {
            frame.setClustering(c);
        }
    }
}
