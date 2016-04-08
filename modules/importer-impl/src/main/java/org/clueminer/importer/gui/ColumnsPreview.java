package org.clueminer.importer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.processor.ui.AttributeProp;
import org.clueminer.spi.ImportListener;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;

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
    private static final Logger logger = Logger.getLogger(ColumnsPreview.class.getName());

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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (container != null) {
                    // we might have to check if reload was completed
                    ContainerLoader loader = container.getLoader();
                    logger.log(Level.INFO, "md5: {0}", container.getMD5());
                    if (loader != null) {
                        Iterable<AttributeDraft> attrs = loader.getAttrIter();
                        logger.log(Level.INFO, "detected {0} attributes", loader.getAttributeCount());
                        if (numAttributes != loader.getAttributeCount()) {
                            numAttributes = loader.getAttributeCount();
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
                }
            }
        });
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
        logger.log(Level.INFO, "data was loaded");
        attributes = null;
        attrPanels = null;
        //remove all previous attributes
        removeAll();
    }

    public void attributeChanged(AttributeDraft attr) {
        logger.log(Level.INFO, "updating attribute {0} idx {1}", new Object[]{attr.getName(), attr.getIndex()});
        int idx = attr.getIndex();
        if (idx < attrPanels.length) {
            attrPanels[idx].setAttrName(attr.getName());
            attrPanels[idx].setType(attr.getJavaType());
            attrPanels[idx].setRole(attr.getRole().toString().toLowerCase());
        }
    }

}
