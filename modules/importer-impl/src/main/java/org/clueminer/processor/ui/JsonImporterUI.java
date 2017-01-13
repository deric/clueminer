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
package org.clueminer.processor.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import org.clueminer.importer.impl.JsonImporter;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ImporterUI.class)
public class JsonImporterUI extends AbstractImporterUI implements ImporterUI {

    private JsonImporter importer;

    public JsonImporterUI() {
        initialize();
    }

    @Override
    public Importer getImporter() {
        return importer;
    }

    @Override
    public void setup(Importer importer) {
        this.importer = (JsonImporter) importer;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public void unsetup(boolean update) {
        //
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "JsonImporterUI.displayName");
    }

    @Override
    public boolean isUIForImporter(Importer importer) {
        return importer instanceof JsonImporter;
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 0.1;
        c.weighty = 1.0;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
    }

}
