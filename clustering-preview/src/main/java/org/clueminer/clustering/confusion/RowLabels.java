package org.clueminer.clustering.confusion;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

/**
 *
 * @author Tomas Barton
 */
public class RowLabels extends AbstractLabels {

    private int maxWidth;

    @Override
    protected void render(Graphics2D g) {
        if (hasData()) {
            g.setColor(Color.black);
            float annY;
            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            int ascent = fm.getMaxAscent();
            int descent = fm.getDescent();
            /*
             * Fonts are not scaling lineraly

             *---------------ascent
             *
             * FONT
             * ----- baseline
             *
             * --------------descent
             *
             */
            double offset = (elementSize.height / 2.0) + ((ascent - descent) / 2.0);
            for (int row = 0; row < a.size(); row++) {
                annY = (float) (row * elementSize.height + offset);
                String s = a.get(row).getName();
                if (s == null) {
                    s = unknownLabel;
                }

                int width = (int) (g.getFont().getStringBounds(s, frc).getWidth());
                checkMax(width);
                g.drawString(s, 0, annY);
            }
        }
    }

    private void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            updateSize();
            createBufferedGraphics();
        }
    }

    @Override
    protected void updateSize() {
        int width = 40 + maxWidth;
        int height;
        if (elementSize.height < lineHeight) {
            //no need to display unreadable text
            visible = false;
            width = 0;
            height = 0;
            bufferedImage = null;
        } else {
            visible = true;
            height = elementSize.height * a.size() + 1;

        }
        this.size.width = width;
        this.size.height = height;
        setMinimumSize(size);
        setPreferredSize(size);
    }

    /**
     * Check is clustering A is not empty
     *
     * @return true when has data to render component
     */
    @Override
    public boolean hasData() {
        return (a != null);
    }

}
