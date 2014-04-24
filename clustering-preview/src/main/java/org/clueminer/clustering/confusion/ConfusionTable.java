package org.clueminer.clustering.confusion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;

/**
 *
 * @author Tomas Barton
 */
public class ConfusionTable extends JPanel {

    private static final long serialVersionUID = -7558362062012338814L;
    private Dimension elemSize = new Dimension(10, 10);
    private Clustering<Cluster> a;
    private Clustering<Cluster> b;
    protected int fontSize = 10;
    protected Font defaultFont;

    public ConfusionTable() {
        initComponents();
    }

    private void initComponents() {
        defaultFont = new Font("verdana", Font.PLAIN, fontSize);
        setBackground(Color.BLUE);
    }

    public void setClusterings(Clustering<Cluster> a, Clustering<Cluster> b) {
        this.a = a;
        this.b = b;

        repaint();
    }

    protected void updateSize(Dimension size) {
        elemSize = size;
        repaint();
    }

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D) gr;

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (a != null && b != null) {

            int x, y;
            int cnt;
            String s;
            g.setColor(Color.RED);

            FontRenderContext frc = g.getFontRenderContext();
            FontMetrics fm = g.getFontMetrics();
            int fh = fm.getHeight();
            double fw;

            for (int i = 0; i < a.size(); i++) {
                for (int j = 0; j < b.size(); j++) {
                    cnt = a.get(i).countMutualElements(b.get(j));
                    System.out.println("a-" + a.get(i).getName() + "-vs" + "-b" + b.get(j).getName() + ": " + cnt);
                    x = j * elemSize.width;
                    y = i * elemSize.height;
                    g.drawRect(x, y, elemSize.width - 1, elemSize.height - 1);
                    s = String.valueOf(cnt);
                    fw = (g.getFont().getStringBounds(s, frc).getWidth());
                    g.drawString(s, (int) (x + fw / 2), y + elemSize.height / 2 + fh / 2);
                }
            }
            g.dispose();
        }
    }

}
