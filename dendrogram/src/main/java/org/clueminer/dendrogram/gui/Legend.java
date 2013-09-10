package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.clueminer.dendrogram.DendroPane;
import org.clueminer.dendrogram.DendrogramData;
import org.clueminer.dendrogram.events.DendrogramDataEvent;
import org.clueminer.dendrogram.events.DendrogramDataListener;

/**
 *
 * @author Tomas Barton
 */
public class Legend extends JPanel implements DendrogramDataListener {
    
    private static final long serialVersionUID = 5461063176271490884L;
    private Insets insets = new Insets(10, 10, 10, 0);
    private DendroPane panel;
    private int colorBarWidth = 30;
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;
    
    public Legend(DendroPane p) {
        this.panel = p;
        this.setBackground(panel.getBackground());
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

    /**
     * Filling rectangles is rather expensive operation therefore we try to call
     * it just once
     *
     * @TODO call this methods when color scheme changes
     *
     * @param colorBarWidth
     * @param colorBarHeight
     * @param min
     * @param max
     */
    private void drawData(int colorBarWidth, int colorBarHeight, double min, double max) {
        bufferedImage = new BufferedImage(colorBarWidth, colorBarHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedGraphics = bufferedImage.createGraphics();
        
        double range = max - min;
        double inc = range / (double) colorBarHeight;
        
        int yStart;
        bufferedGraphics.setColor(Color.black);
        bufferedGraphics.drawRect(0, 0, colorBarWidth - 1, colorBarHeight - 1);
        //maximum color is at the top
        double value = max;
        //draws box with colors
        for (int y = 2; y < colorBarHeight; y++) {
            yStart = colorBarHeight - y;
            bufferedGraphics.setColor(panel.getScheme().getColor(value));
            value -= inc;
            bufferedGraphics.fillRect(1, yStart, colorBarWidth - 2, 1);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (panel.getDendrogramData() == null) {
            return;
        }
        
        double min = panel.getDendrogramData().getMinValue();
        double max = panel.getDendrogramData().getMaxValue();
        double mid = panel.getDendrogramData().getMidValue();
        int colorBarHeight = this.getHeight() - insets.bottom - insets.top;
        if (colorBarHeight < 10) {
            //default height which is not bellow zero
            colorBarHeight = 20;
        }
        //create color palette
        if (bufferedImage == null) {
            drawData(colorBarWidth, colorBarHeight, min, max);
        }
        //places color bar to canvas
        g2d.drawImage(bufferedImage,
                insets.left, insets.top,
                colorBarWidth, colorBarHeight,
                null);
        
        
        if (panel.isAntiAliasing()) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        
        FontMetrics hfm = g.getFontMetrics();
        // int descent = hfm.getDescent();
        int fHeight = hfm.getHeight();
        
        g2d.setColor(Color.black);
        int textWidth;
        int spaceBetweenBarAndLabels = 5;
        String strMin = String.valueOf(panel.formatNumber(min));
        textWidth = hfm.stringWidth(strMin); //usually longest string FIXME for smartest string width detection
        g2d.drawString(strMin, colorBarWidth + spaceBetweenBarAndLabels + insets.left, 0 + fHeight);
        
        String strMid = String.valueOf(panel.formatNumber(mid));
        //textWidth = hfm.stringWidth(strMid);
        g2d.drawString(strMid, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight / 2 + fHeight);
        String strMax = String.valueOf(panel.formatNumber(max));
        //textWidth = hfm.stringWidth(strMax);
        g2d.drawString(strMax, colorBarWidth + spaceBetweenBarAndLabels + insets.left, colorBarHeight + fHeight);
        
        int totalWidth = insets.left + colorBarWidth + spaceBetweenBarAndLabels + textWidth + insets.right;
        int totalHeight = insets.top + colorBarHeight + insets.bottom;
        setMinimumSize(new Dimension(totalWidth, totalHeight));
    }
    
    @Override
    public void datasetChanged(DendrogramDataEvent evt, DendrogramData dataset) {
        bufferedImage = null;
        repaint();
    }
    
    @Override
    public void cellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting) {
        //do nothing
    }
    
    @Override
    public void cellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting) {
        //do nothing
    }
}
