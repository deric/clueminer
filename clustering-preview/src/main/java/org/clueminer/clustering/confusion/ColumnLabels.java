package org.clueminer.clustering.confusion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;

/**
 *
 * @author Tomas Barton
 */
public class ColumnLabels extends AbstractLabels {

    @Override
    protected void render(Graphics2D g) {
        //we draw strings in rows and then we rotate the whole image
        String s;
        int coordX;
        if (hasData()) {
            g.setColor(Color.black);

            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            int height = fm.getHeight();
            int width;
            // clockwise 90 degrees
            g.rotate(Math.PI / 2.0);
            for (int col = 0; col < b.size(); col++) {
                coordX = (col + 1) * elementSize.width - elementSize.width / 2 - height / 2;
                s = b.get(col).getName();

                width = (int) (g.getFont().getStringBounds(s, frc).getWidth());
                checkMax(width);
                g.drawString(s, 0, -coordX);
            }
            g.rotate(-Math.PI / 2.0);
        }
    }

    /**
     * We care only about width
     *
     * @param size
     */
    @Override
    protected void updateSize(Dimension size) {
        if (elementSize.width != size.width) {
            elementSize.width = size.width;
            resetCache();
        }
    }

    @Override
    public boolean hasData() {
        return (b != null);
    }

    @Override
    protected void recalculate() {
        int width = 50;
        int height = 50 + maxWidth;
        if (hasData()) {
            width = elementSize.width * b.size() + 1;
        }
        this.size.width = width;
        this.size.height = height;
        setMinimumSize(this.size);
        setSize(this.size);
        setPreferredSize(size);
    }

}
