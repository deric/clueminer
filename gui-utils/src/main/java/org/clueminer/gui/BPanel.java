package org.clueminer.gui;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Buffered panel
 *
 * @author Tomas Barton
 */
public abstract class BPanel extends JPanel {

    protected Dimension size = new Dimension(0, 0);
    protected BufferedImage bufferedImage;
    protected Graphics2D g;

    public abstract void render(Graphics2D g);

    public abstract void updateSize(Dimension size);

    public abstract boolean hasData();

    /**
     * Recalculate dimension of component
     */
    public abstract void recalculate();

    public abstract boolean isAntiAliasing();

    public void createBufferedGraphics() {
        if (!hasData() || size.width <= 0 || size.height <= 0) {
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

        if (isAntiAliasing()) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        render(g);

        g.dispose();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferedImage == null) {
            createBufferedGraphics();
        }
        //if no data, bufferedImage is null
        if (bufferedImage != null) {
            int dx = Math.abs(size.width - bufferedImage.getWidth());
            int dy = Math.abs(size.height - bufferedImage.getHeight());

            //requested size is different from buffered one, clear the cache
            if (dx > 5 || dy > 5) {
                createBufferedGraphics();
            }

            //cached image
            g.drawImage(bufferedImage,
                        0, 0,
                        size.width, size.height,
                        null);
        }
    }

    public void resetCache() {
        recalculate();
        createBufferedGraphics();
        repaint();
    }

}
