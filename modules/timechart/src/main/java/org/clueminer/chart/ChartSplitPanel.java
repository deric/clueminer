package org.clueminer.chart;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.ChartData;
import org.clueminer.chart.api.Tracker;
import org.clueminer.chart.api.Overlay;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.gui.ColorGenerator;
import org.clueminer.timeseries.chart.NormalizationEvent;
import org.clueminer.timeseries.utils.CoordCalc;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class ChartSplitPanel extends JLayeredPane implements Serializable, Tracker {

    private static final long serialVersionUID = 1538591500876681086L;
    private ChartConfig chartFrame;
    private ChartPanel chartPanel;
    private JLabel label;
    //index of the marker
    private int index = -1;
    private int width = 200;
    private int height;
    private int lines = 5;
    private Color lineColor = new Color(0xef2929);
    private Color color = new Color(0x1C2331);
    private Color backgroundColor = ColorGenerator.getTransparentColor(color, 100);
    private Color fontColor = new Color(0xffffff);
    private Font font;

    public ChartSplitPanel(ChartConfig frame) {
        chartFrame = frame;
        chartPanel = new ChartPanel(chartFrame);

        font = new Font(chartFrame.getChartProperties().getFont().getName(), Font.PLAIN, 10);
        height = font.getSize() + 4;

        initComponents();
    }

    private void initComponents() {
        label = new JLabel();
        label.setOpaque(true);
        label.setBackground(backgroundColor);
        label.setBorder(BorderFactory.createLineBorder(color));
        label.setFont(font);
        label.setForeground(fontColor);
        label.setVisible(index != -1);
        label.setPreferredSize(new Dimension(width, height));

        Draggable draggable = new Draggable(label);
        label.addMouseListener(draggable);
        label.addMouseMotionListener(draggable);


        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setLayout(new LayoutManager() {

            @Override
            public void addLayoutComponent(String name, Component comp) {
            }

            @Override
            public void removeLayoutComponent(Component comp) {
            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return new Dimension(0, 0);
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return new Dimension(0, 0);
            }

            @Override
            public void layoutContainer(Container parent) {
                Insets insets = parent.getInsets();
                int w = parent.getWidth() - insets.left - insets.right;
                int h = parent.getHeight() - insets.top - insets.bottom;

                chartPanel.setBounds(insets.left, insets.top, w, h);

                Point dp = new Point(0, 50);
                Point p = label.getLocation();
                if (!dp.equals(p)) {
                    label.setBounds(p.x, p.y, width + 2, height * lines + 2);
                } else {
                    label.setBounds(dp.x, dp.y, width + 2, height * lines + 2);
                }
            }
        });

        add(label);
        add(chartPanel);
        label.setLocation(0, 50);
        ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
        cd.addNormalizationListener(chartPanel.getAnnotationPanel());
    }

    public ChartConfig getChartFrame() {
        return chartFrame;
    }

    public void setChartFrame(ChartConfig frame) {
        chartFrame = frame;
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setChartPanel(ChartPanel panel) {
        chartPanel = panel;
    }

    @Override
    public void setIndex(int i) {
        if (i != index) {
            index = i;
            ChartDataImpl chd = (ChartDataImpl) chartFrame.getChartData();
            chd.fireMarkerMoved(new NormalizationEvent(this, chd.getTimeAt(index), index));
        }
    }

    public int getIndex() {
        return index;
    }

    public ChartData getChartData() {
        return chartFrame.getChartData();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        if (chartFrame.getChartProperties().getMarkerVisibility()) {
            if (index != -1) {
                labelText();
                paintMarkerLine(g2);
                label.setVisible(true);
            }
        } else {
            label.setVisible(false);
        }

        g2.dispose();
    }

    private void paintMarkerLine(Graphics2D g) {
        Rectangle bounds = chartPanel.getBounds();
        bounds.grow(-2, -2);
        ContinuousInstance inst = chartFrame.getChartData().getVisible().instance(0);
        ChartDataImpl chd = (ChartDataImpl) chartFrame.getChartData();
        long time = chd.getTimeAt(index) + inst.getStartTime();
        String s = getMarkerString(time);
        double dx = chartFrame.getChartData().getX(index, bounds);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics(font);
        int w = fm.stringWidth(s) + 2;
        int h = fm.getHeight() + 2;
        boolean inv = (getWidth() - dx < w);

        // paint line
        g.setPaint(lineColor);
        g.draw(CoordCalc.line(dx, 0, dx, getHeight()));
        // paint background
        g.fill(CoordCalc.rectangle(inv ? dx - w : dx, 0, w, h));
        // paint rectangle and string
        g.draw(CoordCalc.rectangle(inv ? dx - w : dx, 0, w, h));
        g.setPaint(fontColor);
        g.drawString(s, inv ? (float) (dx - w + 1) : (float) (dx + 1), (float) (fm.getAscent() + 1));

        bounds.grow(2, 2);
    }

    public String getMarkerString(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        StringBuilder sb = new StringBuilder();

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);


        if (day < 10) {
            sb.append("0");
        }
        sb.append(Integer.toString(day));
        sb.append(".");
        if (month < 10) {
            sb.append("0");
        }
        sb.append(Integer.toString(month));

        sb.append(".");
        sb.append(Integer.toString(year));
        sb.append(" ");

        if (hour < 10) {
            sb.append("0");
        }
        sb.append(Integer.toString(hour));
        sb.append(":");

        if (minute < 10) {
            sb.append("0");
        }
        sb.append(Integer.toString(minute));

        return sb.toString();

    }

    private String addLine(String left, String right) {
        if (!right.equals(" ")) {
            return NbBundle.getMessage(
                    ChartSplitPanel.class,
                    "HTML_Line",
                    new String[]{String.valueOf(width / 2), left, right});
        } else {
            return NbBundle.getMessage(
                    ChartSplitPanel.class,
                    "HTML_EmptyLine",
                    new String[]{String.valueOf(width), left});
        }
    }

    @Override
    public void labelText() {
        if (index != -1) {
            ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
            DecimalFormat df = new DecimalFormat("#,##0.00");

            ContinuousInstance inst = (ContinuousInstance) cd.getVisible().instance(0);
            long time = cd.getTimeAt(index) + inst.getStartTime();
            String date = getMarkerString(time);

            StringBuilder sb = new StringBuilder();
            // Date
            sb.append(addLine("Compound added:", date));

            lines = 1;

            boolean hasOverlays = chartPanel.getOverlaysCount() > 0;

            if (hasOverlays) {
                sb.append(addLine(" ", " "));
                lines++;
            }

            if (hasOverlays) {
                for (Overlay overlay : chartPanel.getOverlays()) {
                    LinkedHashMap map = overlay.getHTML(chartFrame, index);
                    Iterator it = map.keySet().iterator();
                    while (it.hasNext()) {
                        Object key = it.next();
                        sb.append(addLine(key.toString(), map.get(key).toString()));
                        lines++;
                    }
                }
            }


            String labelText = NbBundle.getMessage(
                    ChartSplitPanel.class,
                    "HTML_Marker",
                    new String[]{String.valueOf(width), sb.toString()});
            if (!label.getText().equals(labelText)) {
                label.setText(labelText);
            }

            Dimension dimension = new Dimension(width, height * lines);
            if (!label.getPreferredSize().equals(dimension)) {
                label.setPreferredSize(dimension);
            }
        } else {
            label.setVisible(false);
        }
    }

    public void moveLeft() {
        ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
        System.out.println("more left " + index);
        labelText();
        chartFrame.repaint();
    }

    public void moveRight() {
        ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
        System.out.println("more right " + index);
        labelText();
        chartFrame.repaint();
    }

    public static class Draggable extends MouseAdapter implements MouseMotionListener {

        Point lastP;
        Component cDraggable;

        public Draggable(Component comp) {
            comp.setLocation(0, 0);
            cDraggable = comp;
        }

        private void setCursorType(Point p) {
            Point loc = cDraggable.getLocation();
            Dimension size = cDraggable.getSize();
            if ((p.y + 4 < loc.y + size.height) && (p.x + 4 < p.x + size.width)) {
                cDraggable.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (cDraggable.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))) {
                lastP = e.getPoint();
            } else {
                lastP = null;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            lastP = null;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            setCursorType(e.getPoint());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x, y;
            if (lastP != null) {
                x = cDraggable.getX() + (e.getX() - (int) lastP.getX());
                y = cDraggable.getY() + (e.getY() - (int) lastP.getY());
                cDraggable.setLocation(x, y);
            }
        }
    }
}
