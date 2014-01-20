package org.clueminer.clustering.preview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Generates legend for chart coloring
 *
 * @author Tomas Barton
 */
public class ChartLegend extends JPanel {

    private Map<Integer, Color> colors;
    private BufferedImage buffImg;
    private Graphics2D buffGr;
    private Dimension size = new Dimension(0, 0);
    private final Insets insets = new Insets(0, 0, 0, 0);

    public ChartLegend() {
        initComponent();
    }

    private void initComponent() {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (buffImg == null) {
            drawData();
        }

        Graphics2D g2 = (Graphics2D) g;
        if (buffGr != null) {
            g2.drawImage(buffImg,
                         insets.left, insets.top,
                         size.width, size.height,
                         null);
        }
        g2.dispose();
    }

    private void drawData() {
        buffImg = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
        buffGr = buffImg.createGraphics();

    }

    public Map<Integer, Color> getColors() {
        return colors;
    }

    public void setColors(Map<Integer, Color> colors) {
        this.colors = colors;
    }

}
