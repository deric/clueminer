package org.clueminer.scatter.matrix;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;

/**
 *
 * @author Tomas Barton
 */
public class Legend extends JPanel {

    private static final long serialVersionUID = 7661870086510737247L;
    private Map<Integer, Entry<String, Color>> labels;
    protected int fontSize = 10;
    protected Font defaultFont;
    protected BufferedImage bufferedImage;
    protected Graphics2D g;
    private Dimension size = new Dimension(100, 100);
    protected int maxWidth;
    protected boolean changedMax = false;
    protected int lineHeight = 12;
    private float maxFontSize = 50;
    protected boolean visible = true;
    protected boolean isAntiAliasing = true;
    private final Insets insets = new Insets(10, 10, 10, 10);
    protected static final String unknownLabel = "(unknown)";
    private Shape shape;

    public Legend() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                bufferedImage = null;
                recalculate();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                size = getSize();
                recalculate();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                size = getSize();
                recalculate();
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }

        });
    }

    protected void render(Graphics2D g) {
        if (hasData()) {
            AffineTransform trans = new AffineTransform();
            g.setFont(defaultFont);
            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            int ascent = fm.getMaxAscent();
            int descent = fm.getDescent();
            int baseSize = (int) (0.33 * fm.getHeight());
            shape = new Ellipse2D.Double(0, 0, baseSize, baseSize);
            int leftPadding = shape.getBounds().width + 5;
            String str;
            BufferedImage symb;
            int x, y;
            double scale = 1.0;
            Color color;

            maxWidth = 0;
            for (int row = 0; row < labels.size(); row++) {
                g.setColor(Color.black);
                str = labels.get(row).getKey();
                if (str == null) {
                    str = unknownLabel;
                }
                x = insets.left + leftPadding;
                y = row * lineHeight;

                int width = (int) (g.getFont().getStringBounds(str, frc).getWidth());
                checkMax(width);
                g.drawString(str, x, (y + fm.getHeight() - (ascent - descent) / 4));

                color = labels.get(row).getValue();
                g.setColor(color);
                //for debugging
                //g.drawRect(x, y, width, fm.getHeight());
                trans.setToIdentity();

                Rectangle bounds = shape.getBounds();

                symb = drawSymbol(color, (int) (bounds.width * scale), (int) (bounds.height * scale));
                //System.out.println("shape pos" + );
                trans.translate(insets.left - shape.getBounds().width / 2.0, y + fm.getHeight() / 2.0 - bounds.height / 2.0 + (ascent - descent) / 8);
                g.drawImage(symb, trans, null);
            }
            if (changedMax) {
                changedMax = false;
                //bufferedImage = null;
            }
        }
    }

    private BufferedImage drawSymbol(Color color, int width, int height) {
        BufferedImage bi = new BufferedImage(width + 2, height + 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D buff = bi.createGraphics();
        buff.setPaint(color);
        buff.draw(shape);
        buff.fill(shape);
        return bi;
    }

    private void recalculate() {
        //System.out.println("prefered size: " + getPreferredSize());
        //System.out.println("size: " + getSize());
        //System.out.println("min size: " + getMinimumSize());
        //System.out.println("======");
        size.width = (int) Math.ceil(getSize().width * 0.9);
        size.height = (int) Math.ceil(getSize().height * 0.9);
        //System.out.println("matrix component " + dim.width + ", " + dim.height);

        if (sizeUpdated()) {
            createBufferedGraphics();
        }
    }

    public boolean sizeUpdated() {
        if (hasData()) {
            double fsize;
            int w, h;
            w = size.height;
            lineHeight = (size.height - insets.top - insets.bottom) / labels.size();
            h = labels.size() * lineHeight + insets.top + insets.bottom;
            fsize = (lineHeight * 0.5);
            if (fsize >= maxFontSize) {
                fsize = maxFontSize;
            }
            defaultFont = defaultFont.deriveFont((float) fsize);

            if (size.height != h) {
                this.size.width = w;
                this.size.height = h;

                //System.out.println("legend size: " + size.toString());
                setMinimumSize(size);
                setSize(size);
                setPreferredSize(size);
                return true;
            }

        }
        return false;
    }

    protected void checkMax(int width) {
        if (width > maxWidth) {
            maxWidth = width;
            changedMax = true;
        }
    }

    public void setLabels(Map<Integer, Entry<String, Color>> labels) {
        this.labels = labels;
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
        recalculate();
        revalidate();
        validate();
        repaint();
    }

    public boolean hasData() {
        return (labels != null && labels.size() > 0);
    }

    protected void createBufferedGraphics() {
        if (!hasData() || !visible || size.width <= 0 || size.height <= 0) {
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

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

}
