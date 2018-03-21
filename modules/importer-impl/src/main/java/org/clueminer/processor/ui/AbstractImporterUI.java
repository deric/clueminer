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
package org.clueminer.processor.ui;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import org.clueminer.spi.ImportListener;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractImporterUI extends JPanel implements ImporterUI {

    private static final long serialVersionUID = -4104486470131884231L;

    private final transient EventListenerList importListeners = new EventListenerList();

    @Override
    public void addListener(ImportListener listener) {
        importListeners.add(ImportListener.class, listener);
    }

    @Override
    public void removeListener(ImportListener listener) {
        importListeners.remove(ImportListener.class, listener);
    }

    @Override
    public void fireImporterChanged() {
        //getImporter().reload();
        for (ImportListener im : importListeners.getListeners(ImportListener.class)) {
            im.importerChanged(getImporter(), this);
        }
    }

    public abstract Importer getImporter();

}
