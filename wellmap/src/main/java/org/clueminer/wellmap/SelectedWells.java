package org.clueminer.wellmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;

/**
 *
 * @author Tomas Barton
 */
public class SelectedWells extends JPanel implements MouseMotionListener {

    private static final long serialVersionUID = 562527367320413135L;
    private WellGrid grid;
    /**
     * Selected wells are marked with 1
     */
    private int[] selected = null;
    private HtsPlate<HtsInstance> plate;
    private int totalCnt = 0;
    private BufferedImage bufferedImage = null;
    private Graphics2D bufferedGraphics;
    protected JToolTip m_customToolTip = null;
    private Rectangle captureRect;
    private Point start;
    private BufferedImage screenCopy;
    private static final Logger logger = Logger.getLogger(SelectedWells.class.getName());

    public SelectedWells(WellGrid g) {
        this.grid = g;
        initComponents();
    }

    private void initComponents() {
        //transparent layer
        setOpaque(false);
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                //System.out.println(me);
            }

            @Override
            public void mouseClicked(MouseEvent me) {
                int id = grid.translatePosition(me.getX(), me.getY());
                if (selected != null && id >= 0) {
                    if (selected[id] == 1) {
                        selected[id] = 0;
                    } else {
                        selected[id] = 1;
                    }
                    clearCache();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println(e);
            }
        });
        this.addMouseMotionListener(this);
        //for any JComponent support
        //      setCustomToolTip(new JCustomTooltip(this, new ToolTipContent()));
    }

    public void clearCache() {
        bufferedImage = null;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (totalCnt == 0 || plate == null) {
            return;
        }
        Dimension size = grid.getSize();
        Insets insets = grid.getInsets();

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

    private void drawComponent(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Shape circle;
        int row = 0, col = 1;
        for (int i = 0; i < totalCnt; i++) {
            if (selected[i] == 1) {
                //column numbering starts from 1
                circle = grid.createCircle(row, col);
                g2.draw(circle);
                g2.setPaint(plate.instance(i).getColor());
                g2.fill(circle);
            }

            col++;
            if (col > plate.getColumnsCount()) {
                col = 1;
                row++;
            }
        }
        g2.dispose();

    }

    public void select(int row, int col) {
        int pos = row * plate.getColumnsCount() + col;
        if (pos >= 0 && pos < totalCnt) {
            if (selected[pos] == 1) {
                selected[pos] = 0;
            } else {
                selected[pos] = 1;
            }
        } else {
            logger.log(Level.INFO, "Invalid position of well = {0}, row= " + row + ", col = " + col + ", total cnt = " + totalCnt, pos);
        }
    }

    public void setSelected(HtsPlate<HtsInstance> selection) {
        if (selection != null) {
            for (HtsInstance inst : selection) {
                select(inst.getRow(), inst.getColumn());
            }
        }
    }

    public void setPlate(HtsPlate p) {
        this.plate = p;
        totalCnt = p.getColumnsCount() * p.getRowsCount();
        selected = new int[totalCnt];
        revalidate();
    }

    public void updateSize(Dimension size) {
        setPreferredSize(size);
        clearCache();
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point end = me.getPoint();
        System.out.println("mouse dragged: " + me);
        captureRect = new Rectangle(start, new Dimension(end.x - start.x, end.y - start.y));

        screenCopy = new BufferedImage(
                bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                bufferedImage.getType());

        // repaint(bufferedImage, screenCopy);

        Graphics2D g = bufferedImage.createGraphics();

        g.drawImage(bufferedImage, 0, 0, null);
        if (captureRect != null) {
            System.out.println("painting" + captureRect);
            g.setColor(Color.RED);
            g.draw(captureRect);
            g.setColor(new Color(255, 255, 255, 150));
            g.fill(captureRect);
        }
        repaint();

        //screenLabel.repaint();
        //selectionLabel.setText("Rectangle: " + captureRect);

    }

    public void repaint(BufferedImage orig, BufferedImage copy) {
        Graphics2D g = copy.createGraphics();
        g.drawImage(orig, 0, 0, null);
        if (captureRect != null) {
            g.setColor(Color.RED);
            g.draw(captureRect);
            g.setColor(new Color(255, 255, 255, 150));
            g.fill(captureRect);
        }
        g.dispose();
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        //System.out.println("mouse: " + e.getX() + ", " + e.getY());
        final int pos = grid.translatePosition(e.getX(), e.getY());
        HtsInstance inst;
        if (pos > -1 && plate != null) {
            inst = plate.instance(pos);
            if (inst != null) {
                setToolTipText(plate.instance(pos).getName() + ": " + inst.getMax());
            }
        }
        start = e.getPoint();
    }

    /**
     * Returns the tooltip instance to use for this Display. By default, uses
     * the normal Swing tooltips, returning the result of this same method
     * invoked on the JComponent super-class. If a custom tooltip has been set,
     * that is returned instead.
     *
     * @see #setCustomToolTip(JToolTip)
     * @see javax.swing.JComponent#createToolTip()
     */
    @Override
    public JToolTip createToolTip() {
        if (m_customToolTip == null) {
            return super.createToolTip();
        } else {
            return m_customToolTip;
        }
    }

    /**
     * Set a custom tooltip to use for this Display. To trigger tooltip display,
     * you must still use the
     * <code>setToolTipText</code> method as usual. The actual text will no
     * longer have any effect, other than that a null text value will result in
     * no tooltip display while a non-null text value will result in a tooltip
     * being shown. Clients are responsible for setting the tool tip text to
     * enable/disable tooltips as well as updating the content of their own
     * custom tooltip instance.
     *
     * @param tooltip the tooltip component to use
     * @see prefuse.util.ui.JCustomTooltip
     */
    public void setCustomToolTip(JToolTip tooltip) {
        m_customToolTip = tooltip;
    }

    /**
     * Get the custom tooltip used by this Display. Returns null if normal
     * tooltips are being used.
     *
     * @return the custom tooltip used by this Display, or null if none
     */
    public JToolTip getCustomToolTip() {
        return m_customToolTip;
    }
}
