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
package org.clueminer.importer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.processor.ui.AttributeProp;
import org.clueminer.spi.ImportListener;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class ColumnsPreview extends JPanel implements ImportListener {

    private static final long serialVersionUID = -1812301314761225939L;
    private int numAttributes = 0;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private AttributeDraft[] attributes;
    private AttributeProp[] attrPanels;
    private ImporterUI importerUI;
    private static final Logger LOG = LoggerFactory.getLogger(ColumnsPreview.class);

    public ColumnsPreview() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
    }

    @Override
    public void importerChanged(Importer importer, ImporterUI importerUI) {
        this.importerUI = importerUI;
        final Container container = importer.getContainer();
        final ColumnsPreview preview = this;
        if (container != null) {
            LOG.info("detected {} attributes", container.getAttributeCount());
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (container != null) {
                        // we might have to check if reload was completed
                        if (container != null) {
                            Iterable<AttributeDraft> attrs = container.getAttrIter();
                            if (numAttributes != container.getAttributeCount()) {
                                numAttributes = container.getAttributeCount();
                                preview.removeAll();
                                attributes = new AttributeDraft[numAttributes];
                                attrPanels = new AttributeProp[numAttributes];
                                for (AttributeDraft atrd : attrs) {
                                    generateAttribute(atrd.getIndex(), atrd);
                                }
                            }
                        } else {
                            NotifyUtil.error("Error", "missing loader", false);
                        }
                    } else {
                        NotifyUtil.error("Error", "Missing loader", false);
                    }
                }
            });
        } else {
            NotifyUtil.error("Error", "Importer " + importer.getName() + " failed to parse data", false);
        }
    }

    private void generateAttribute(int num, AttributeDraft atrd) {
        attributes[num] = atrd;

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = num;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = WEST_INSETS;

        AttributeProp column = new AttributeProp(atrd, importerUI);
        attrPanels[num] = column;
        add(column, c);
    }

    @Override
    public void dataLoaded() {
        //
        LOG.info("data was loaded");
        attributes = null;
        attrPanels = null;
        numAttributes = 0;
        //remove all previous attributes
        removeAll();
    }

    public void attributeChanged(AttributeDraft attr) {
        LOG.info("updating attribute {} idx {}", attr.getName(), attr.getIndex());
        int idx = attr.getIndex();
        if (idx < attrPanels.length) {
            attrPanels[idx].setAttrName(attr.getName());
            attrPanels[idx].setType(attr.getJavaType());
            attrPanels[idx].setRole(attr.getRole().toString().toLowerCase());
        }
    }

}
