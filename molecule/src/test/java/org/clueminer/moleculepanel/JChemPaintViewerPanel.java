package org.clueminer.moleculepanel;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.io.IOException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.jchempaint.AbstractJChemPaintPanel;
import org.openscience.jchempaint.RenderPanel;
import org.openscience.jchempaint.applet.JChemPaintAbstractApplet;

public class JChemPaintViewerPanel extends AbstractJChemPaintPanel {

    private static final long serialVersionUID = -2931493746969632900L;

    /**
     * Builds a JCPViewerPanel with a certain model
     *
     * @param chemModel The model
     */
    public JChemPaintViewerPanel(IChemModel chemModel, int width, int height, boolean fitToScreen, boolean debug, JChemPaintAbstractApplet applet) {
        this.setLayout(new BorderLayout());
        try {
            renderPanel = new RenderPanel(chemModel, this.getWidth(), this.getHeight(), fitToScreen, debug, true, applet);
        } catch (IOException e) {
            announceError(e);
        }
        if (!fitToScreen) {
            ScrollPane scroller = new ScrollPane();
            scroller.add(renderPanel);
            this.add(scroller, BorderLayout.CENTER);
        } else {
            this.add(renderPanel, BorderLayout.CENTER);
        }
    }
}
