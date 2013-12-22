package org.clueminer.dendrogram.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.clueminer.dendrogram.DendroPane;
import org.clueminer.dendrogram.DendrogramData;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractAnnotation extends JPanel {

    private static final long serialVersionUID = 7226708070899572712L;
    protected DendroPane panel;
    protected boolean isAntiAliasing = true;
    protected boolean visible = true;
    protected Dimension elementSize;
    protected Dimension size = new Dimension(0, 0);
    protected BufferedImage bufferedImage;
    protected Graphics2D g;
    protected DendrogramData dendroData;
    protected Font defaultFont;
    protected int lineHeight = 12;
    protected int fontSize = 10;

    public AbstractAnnotation(DendroPane p) {
        this.panel = p;
        this.elementSize = panel.getElementSize();
        setBackground(panel.getBackground());
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
    }

    protected void createBufferedGraphics() {
        if (dendroData == null || !visible) {
            return;
        }
        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        g = bufferedImage.createGraphics();
        this.setOpaque(false);
        // clear the panel
        g.setColor(panel.getBackground());
        g.fillRect(0, 0, size.width, size.height);

        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);

        if (this.isAntiAliasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        render(g);

        g.dispose();
    }

    /**
     * Sets a new element height.
     */
    public void setLineHeight(int height) {
        if (height > 8) {
            this.lineHeight = height;
        }
    }

    public void setFontSize(int size) {
        if (size > 6) {
            this.fontSize = size;
        }
    }

    protected abstract void render(Graphics2D g);

    public void redraw() {
        Graphics2D g2 = (Graphics2D) this.getGraphics();
        if (g2 == null) {
            return;
        }
        //buffered graphics is usually created before
        if (dendroData != null && bufferedImage == null) {
            createBufferedGraphics();
        }
        if (bufferedImage != null) {
            g2.drawImage(bufferedImage,
                         0, 0,
                         size.width, size.height,
                         null);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (bufferedImage == null) {
            createBufferedGraphics(); // } //cached image
        }
        g.drawImage(bufferedImage, 0, 0, size.width, size.height, null);
    }

    public abstract void setDendrogramData(DendrogramData dendroData);

    public void resetCache() {
        updateSize();
        createBufferedGraphics();
        repaint();
    }

    protected abstract void updateSize();

    public Dimension getDimension() {
        return size;
    }
}
