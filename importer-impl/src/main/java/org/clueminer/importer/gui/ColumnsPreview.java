package org.clueminer.importer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import org.clueminer.importer.impl.AttributeDraftImpl;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.ContainerUnloader;
import org.clueminer.processor.ui.AttributeProp;
import org.clueminer.spi.ImportListener;
import org.clueminer.spi.Importer;

/**
 *
 * @author Tomas Barton
 */
public class ColumnsPreview extends JPanel implements ImportListener {

    private static final long serialVersionUID = -1812301314761225939L;
    private int numAttributes = 0;
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private AttributeDraft[] attributes;

    public ColumnsPreview() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
    }

    @Override
    public void importerChanged(Importer importer) {
        System.out.println("imporer changed: " + importer.getClass().getName());
        ContainerUnloader loader = importer.getUnloader();
        if (loader != null) {
            Iterable<AttributeDraft> attrs = loader.getAttributes();
            System.out.println("detected " + loader.getAttributeCount() + " attributes");
            if (numAttributes != loader.getAttributeCount()) {
                numAttributes = loader.getAttributeCount();
                this.removeAll();
                int i = 0;
                attributes = new AttributeDraft[numAttributes];
                for (AttributeDraft atrd : attrs) {
                    generateAttribute(i++, atrd);
                }
            }
        } else {
            //NotifyUtil.error("Error", "missing loader", false);
            numAttributes = 5;
            attributes = new AttributeDraft[numAttributes];
            for (int i = 0; i < numAttributes; i++) {
                generateAttribute(i, new AttributeDraftImpl("attr " + i));

            }
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

        JPanel column = new AttributeProp(atrd);
        add(column, c);
        this.validate();
        this.revalidate();
        this.repaint();
    }

}
