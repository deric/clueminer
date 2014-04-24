package org.clueminer.clustering.confusion;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractLabels extends JPanel {

    private static final long serialVersionUID = 7226708070899572712L;
    protected boolean isAntiAliasing = true;
    protected boolean visible = true;
    protected Dimension elementSize;
    protected Dimension size = new Dimension(0, 0);
    protected BufferedImage bufferedImage;
    protected Graphics2D g;
    protected Font defaultFont;
    protected int lineHeight = 12;
    protected int fontSize = 10;
    protected static final String unknownLabel = "(unknown)";

    protected Clustering<Cluster> a;
    protected Clustering<Cluster> b;

    public AbstractLabels() {
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        elementSize = new Dimension(30, 30);
        setBackground(Color.red);
    }

    protected abstract void render(Graphics2D g);

    protected abstract void updateSize();

    public abstract boolean hasData();

    protected void createBufferedGraphics() {
        if (!hasData() || !visible || size.width == 0 || size.height == 0) {
            return;
        }
        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        g = bufferedImage.createGraphics();
        this.setOpaque(false);
        // clear the panel
        g.setColor(getBackground());
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
     *
     * @param height one line height
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

    public void redraw() {
        Graphics2D g2 = (Graphics2D) this.getGraphics();
        if (g2 == null) {
            return;
        }
        //buffered graphics is usually created before
        if (!hasData() && bufferedImage == null) {
            createBufferedGraphics();
        }
        if (bufferedImage != null) {
            g2.drawImage(bufferedImage,
                         0, 0,
                         size.width, size.height,
                         null);
        }
    }
    /*
     @Override
     public void paint(Graphics g) {
     super.paint(g);

     if (bufferedImage == null) {
     createBufferedGraphics(); // } //cached image
     }
     g.drawImage(bufferedImage, 0, 0, size.width, size.height, null);
     }
     */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferedImage == null) {
            createBufferedGraphics();
        }
        //cached image
        g.drawImage(bufferedImage,
                    0, 0,
                    size.width, size.height,
                    null);
    }

    public void resetCache() {
        updateSize();
        createBufferedGraphics();
        repaint();
    }

    public Dimension getDimension() {
        return size;
    }

    public void setClusterings(Clustering<Cluster> a, Clustering<Cluster> b) {
        this.a = a;
        this.b = b;

        repaint();
    }
}
