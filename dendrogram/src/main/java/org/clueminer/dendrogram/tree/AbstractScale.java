package org.clueminer.dendrogram.tree;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.JPanel;
import org.clueminer.dendrogram.DendroPane;
import org.clueminer.dendrogram.events.DendrogramDataListener;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractScale extends JPanel implements DendrogramDataListener, Serializable {

    private static final long serialVersionUID = 7723360865480072319L;
    //distance between tree and scale
    protected int treeScaleSpace = 10;
    protected int scaleTickLength = 5;
    /**
     * max width/height of ticks + text (number) label
     */
    protected int maxScaleDimension = 30;
    protected int scaleLabelDistance = 10;
    protected int distToScale = 0;
    protected BufferedImage bufferedImage;
    protected Graphics2D g2;
    protected Dimension size = new Dimension(5, 5);
    protected AbstractTree tree;
    protected Font defaultFont = new Font("verdana", Font.PLAIN, 10);
    protected DendroPane panel;

    protected abstract void drawScale(Graphics2D g2);

    protected abstract void updateSize();

    public AbstractScale(DendroPane panel) {
        setDoubleBuffered(false);
        this.panel = panel;
    }

    protected void setDimension(int width, int height) {
        //if there is some change
        if (width != this.size.width || height != this.size.height) {
            this.size.width = width;
            this.size.height = height;
            /**
             * we want exactly this size, when windows is enlarged the heatmap
             * should be bigger
             */
            setPreferredSize(this.size);
            setMinimumSize(this.size);
            setSize(this.size);
        }
    }

    private void createBufferedGraphics() {
        if (!tree.hasData()) {
            return;
        }
        bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        g2 = bufferedImage.createGraphics();
        this.setOpaque(true);

        g2.setFont(defaultFont);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        updateSize();
        drawScale(g2);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setMinimumSize(this.size);
        if (tree != null) {
            if (bufferedImage == null) {
                int max = maxScaleDimension;
                createBufferedGraphics(); //cached image
                if(maxScaleDimension != max){
                    //repaint if size of text has changed
                    updateSize();
                    createBufferedGraphics();
                }
            }
            g.drawImage(bufferedImage, 0, 0, size.width, size.height, null);
        }
        g.dispose();
    }

    @Override
    public int getHeight() {
        return size.height;
    }

    @Override
    public int getWidth() {
        return size.width;
    }
}
