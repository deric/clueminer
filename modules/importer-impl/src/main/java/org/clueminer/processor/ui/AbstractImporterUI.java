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
