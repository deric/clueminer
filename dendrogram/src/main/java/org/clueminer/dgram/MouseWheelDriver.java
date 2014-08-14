package org.clueminer.dgram;

import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollPane;
import org.clueminer.clustering.api.dendrogram.DendroViewer;

/**
 *
 * @author Tomas Barton
 */
public class MouseWheelDriver implements MouseWheelListener {

    private final DendroViewer parent;
    private final JScrollPane scrollPane;

    public MouseWheelDriver(DendroViewer parent, JScrollPane scrollPane) {
        this.parent = parent;
        this.scrollPane = scrollPane;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        Dimension dim = parent.getElementSize();
        int height, width;
        if (e.isControlDown() && !e.isAltDown()) {
            parent.setFitToPanel(false);
            if (notches < 0) {
                height = (int) (dim.height * 0.9);
                if (height == dim.height) {
                    height -= 1;
                }
                parent.setCellHeight(height, false);
            } else {
                height = (int) (dim.height * 1.1);
                if (height == dim.height) {
                    height += 1;
                }
                parent.setCellHeight(height, false);
            }
        } else if (e.isAltDown() && !e.isControlDown()) {
            parent.setFitToPanel(false);
            if (notches < 0) {
                width = (int) (dim.width * 0.9);
                if (width == dim.width) {
                    width -= 1;
                }
                parent.setCellWidth(width, false);
            } else {
                width = (int) (dim.width * 1.1);
                if (width == dim.width) {
                    width += 1;
                }
                parent.setCellWidth(width, false);
            }
        } else {
            //pass events to scrollpanel
            scrollPane.dispatchEvent(e);
        }
    }

}
