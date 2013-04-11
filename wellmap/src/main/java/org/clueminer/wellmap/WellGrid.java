package org.clueminer.wellmap;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * This component display a grid for a plate which is used for biological
 * experiments
 *
 * @author Tomas Barton
 */
public class WellGrid extends JPanel {

    private static final long serialVersionUID = -6770026969501773475L;
    private int rows = 0;
    private int cols = 0;
    private Dimension size = new Dimension(0, 0);
    private Dimension minSize = new Dimension(200, 100);
    private Insets insets = new Insets(10, 10, 10, 10);
    private double aspectRatio = 0.667f;
    private double hgap;
    private double vgap;
    private float gapRatio = 0.9f;
    private int legendVspace = 0;
    private int legendHspace = 0;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;
    private static int charA = 65;
    private boolean fitToArea = true;
    private int maxId;
    /**
     * diameter of circle
     */
    private float r = 0;

    public WellGrid() {
        setOpaque(false);
        setDoubleBuffered(false);
    }

    /**
     * When cached graphics is available paint displays this image, which is
     * very fast and computationally less expensive. Coloring wells is done on
     * upper level.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        if (bufferedImage == null) {
            if (size.width <= 0 || size.height <= 0) {
                g.dispose();
                return;
            }
            bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            bufferedGraphics = bufferedImage.createGraphics();
            drawComponent(bufferedGraphics);
        }

        g.drawImage(bufferedImage,
                insets.left, insets.top,
                size.width, size.height,
                null);
        g.dispose();
    }

    public void clearCache() {
        bufferedImage = null;
        repaint();
    }

    /**
     * Create circle at given position (row, column). Column numbering starts
     * from 1
     *
     * @param row
     * @param column
     * @return
     */
    public Shape createCircle(int row, int column) {
        double x = insets.left + legendVspace, y = insets.top + legendHspace;
        x += (column - 1) * (r + vgap);
        y += row * (r + hgap);
        Shape circle = new Ellipse2D.Double(x, y, r, r);
        return circle;
    }

    /**
     * Converts mouse position to actual id of instance
     *
     * @param x coordinate
     * @param y coordinate
     * @return id of well in grid (that is also index in dataset)
     */
    public int translatePosition(int x, int y) {
        double square = r + hgap; //approximately area of one circle resp. square area
        double pos = (x - insets.left - legendVspace) / square;
        int transX = (int) Math.floor(pos);
        square = r + vgap;
        pos = (y - insets.top - legendHspace) / square;
        int transY = (int) Math.floor(pos);
        int id = cols * transY + transX;
        if (id < 0 || id > maxId) {
            return -1;
        }
        return id;
    }

    public void setDimensions(int rowCnt, int colCnt) {
        rows = rowCnt;
        cols = colCnt;
        maxId = rows * cols - 1;
        //we don't wanna divide by 0 or get a 0 aspect ratio
        if (cols > 0 && rows > 0) {
            aspectRatio = rows / (double) cols;
        }
        clearCache();
    }

    private void checkMinSize() {
        if (size.width < minSize.width) {
            size.width = minSize.width;
        }

        if (size.height < minSize.height) {
            size.height = minSize.height;
        }
    }

    public Dimension updateSize(Dimension parent) {
        size.width = parent.width - insets.left - insets.right;
        //we have to keep aspect ratio of plate
        size.height = (int) ((parent.width - insets.top - insets.bottom) * aspectRatio);
        checkMinSize();
        if (fitToArea) {
            if (size.height > parent.height) {
                size.height = parent.height;
                size.width = (int) ((size.height - insets.bottom - insets.top) / aspectRatio);
            }
        }
        legendVspace = (int) (0.1 * size.width);
        legendHspace = (int) (0.1 * size.height);
        int w = size.width - legendVspace;
        int h = size.height - legendHspace;
        float r1 = ((w * gapRatio) / (cols));
        float r2 = ((h * gapRatio) / (rows));

        r = Math.min(r1, r2);

        hgap = (h - (r * rows)) / (rows);
        vgap = (w - (r * cols)) / (cols);
        bufferedImage = null;
        return size;
    }

    private void drawComponent(Graphics2D g) {
        g.setColor(Color.BLACK);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //plate borders
        g.drawRoundRect(0, 0, size.width - 1, size.height - 1, 10, 10);
        Shape circle;
        float x = legendVspace, y = legendHspace;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                circle = new Ellipse2D.Double(x, y, r, r);
                g.draw(circle);
                x += r + vgap;
            }
            x = legendVspace;
            y += r + hgap;
        }

        //draw captions
        String caption;
        Font font = g.getFont();
        int fontSize = (int) (r * 0.45);
        g.setFont(new Font(font.getName(), font.getStyle(), fontSize));
        FontMetrics hfm = g.getFontMetrics();

        int fHeight = hfm.getHeight();
        int textWidth;
        x = legendVspace;
        float rHalf = r / 2;

        for (int j = 0; j < cols; j++) {
            caption = String.valueOf(j + 1);
            textWidth = hfm.stringWidth(caption);
            g.drawString(caption, x + rHalf - (textWidth / 2), (legendHspace + fHeight) / 2);
            x += r + vgap;
        }

        char sym;
        y = legendHspace;
        for (int i = 0; i < rows; i++) {
            sym = (char) (charA + i);
            caption = String.valueOf(sym);
            textWidth = hfm.stringWidth(caption);

            g.drawString(caption, (legendVspace - textWidth) / 2, y + rHalf + (fontSize / 3)); //no idea why to divide by 3, but it's better than 2 :)
            y += r + hgap;

        }
        g.dispose();
    }
}
