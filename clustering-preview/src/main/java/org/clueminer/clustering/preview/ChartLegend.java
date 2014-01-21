package org.clueminer.clustering.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;

/**
 * Generates legend for chart coloring
 *
 * @author Tomas Barton
 */
public class ChartLegend extends JPanel {

    private Map<Integer, Color>[] colors;
    private BufferedImage buffImg;
    private Graphics2D g2d;
    private Dimension size = new Dimension(0, 0);
    private final Insets insets = new Insets(0, 50, 0, 0);
    private int lineHeight = 15;
    private int fontSize = 10;
    private Font defaultFont = new Font("verdana", Font.PLAIN, fontSize);
    private int width;
    private int height;
    private int maxWidth = 50;
    private int tickWidth = 20;
    private int spaceBetweenTickAndText = 20;

    public ChartLegend() {

    }

    public void updateChart() {
        if (!hasData()) {
            return;
        }
        width = insets.left + tickWidth + spaceBetweenTickAndText + maxWidth + insets.right;
        height = insets.top + numLines() * lineHeight + insets.bottom;
        //nodes on right, 90 deg rot
        setSizes(width, height);
        invalidateCache();
    }

    private int numLines() {
        int num = 0;
        if (!hasData()) {
            return 0;
        }
        for (Map<Integer, Color> set : colors) {
            num += set.size();
        }
        return num;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (buffImg == null) {
            drawData();
        }

        Graphics2D g2 = (Graphics2D) g;
        if (g2d != null) {
            g2.drawImage(buffImg,
                         0, 0,
                         size.width, size.height,
                         null);
        }
        g2.dispose();
    }

    public boolean hasData() {
        return colors != null;
    }

    /**
     * Set component size
     *
     * @param width
     * @param height
     */
    public void setSizes(int width, int height) {
        size.width = width;
        size.height = height;
        setPreferredSize(size);
        setSize(size);
        setMinimumSize(size);
    }

    private void drawData() {
        if (!hasData()) {
            return;
        }
        buffImg = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
        g2d = buffImg.createGraphics();
        double annY;
        g2d.setColor(Color.black);
        g2d.setFont(defaultFont);
        FontRenderContext frc = g2d.getFontRenderContext();
        FontMetrics fm = g2d.getFontMetrics();
        int ascent = fm.getMaxAscent();
        int descent = fm.getDescent();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(3));

        int i = 0;
        String s;
        double offset = (lineHeight / 2.0) + ((ascent - descent) / 2.0);
        double lineY;
        int textStart = insets.left + tickWidth + spaceBetweenTickAndText;
        for (Map<Integer, Color> set : colors) {
            for (Entry<Integer, Color> row : set.entrySet()) {
                annY = i * lineHeight + offset;
                lineY = i * lineHeight + (lineHeight / 2.0) + 1.0;
                g2d.setColor(row.getValue());
                g2d.draw(new Line2D.Double(insets.left, lineY, insets.left + tickWidth, lineY));

                s = String.valueOf(row.getKey());

                int w = (int) (g2d.getFont().getStringBounds(s, frc).getWidth());
                checkMax(w);
                g2d.setColor(Color.BLACK);
                g2d.drawString(s, textStart, (float) annY);
                i++;
            }
        }
    }

    private void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            updateChart();
        }
    }

    public Map<Integer, Color>[] getColors() {
        return colors;
    }

    public void setColors(Map<Integer, Color>[] colors) {
        this.colors = colors;
        updateChart();
    }

    /**
     * We try to avoid complete repainting as much as possible
     */
    public void invalidateCache() {
        //invalidate cache
        buffImg = null;
        repaint();
    }

}
