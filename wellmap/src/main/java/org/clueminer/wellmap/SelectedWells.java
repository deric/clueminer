package org.clueminer.wellmap;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;

/**
 *
 * @author Tomas Barton
 */
public class SelectedWells extends JPanel {

    private static final long serialVersionUID = 562527367320413135L;
    private WellGrid grid;
    /**
     * Selected wells are marked with 1
     */
    private int[] selected = null;
    private HtsPlate plate;
    private int totalCnt = 0;
    private BufferedImage bufferedImage = null;
    private Graphics2D bufferedGraphics;

    public SelectedWells(WellGrid g) {
        this.grid = g;

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
            Logger.getLogger(SelectedWells.class.getName()).log(Level.INFO, "Invalid position of well = {0}, row= " + row + ", col = " + col, pos);
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
    }

    public void updateSize(Dimension size) {
        setPreferredSize(size);
        clearCache();
    }
}
