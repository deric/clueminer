package org.clueminer.importer.gui;

import javax.swing.JPanel;
import org.clueminer.spi.ImportListener;
import org.clueminer.spi.Importer;

/**
 *
 * @author Tomas Barton
 */
public class ColumnsPreview extends JPanel implements ImportListener {

    private static final long serialVersionUID = -1812301314761225939L;

    public ColumnsPreview() {
        initComponents();
    }

    private void initComponents() {

    }

    @Override
    public void importerChanged(Importer importer) {
        System.out.println("imporer changed: " + importer.getClass().getName());
    }

}
