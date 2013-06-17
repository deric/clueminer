package org.clueminer.wellmap;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.clueminer.gui.ColorPalette;

/**
 *
 * @author Tomas Barton
 */
public abstract class ColorScale extends JPanel {

    private static final long serialVersionUID = 4165321091127479092L;
    protected Insets insets = new Insets(10, 10, 10, 0);
    protected int colorBarWidth = 30;
    protected int colorBarHeight;
    protected BufferedImage bufferedImage;
    protected Graphics2D bufferedGraphics;
    protected ColorPalette palette;
    protected boolean antialias = true;
    protected int tickSize = 5;
    protected DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ColorScale(ColorPalette palette) {
        this.palette = palette;
        setDoubleBuffered(false);
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                bufferedImage = null;
                repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
    }

    protected abstract void drawData(int colorBarWidth, int colorBarHeight, double min, double max);

    public ColorPalette getPalette() {
        return palette;
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
    }

    public boolean isAntialias() {
        return antialias;
    }

    public void setAntialias(boolean antialias) {
        this.antialias = antialias;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }
}
