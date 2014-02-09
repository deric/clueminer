package org.clueminer.chart;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import org.clueminer.chart.api.Chart;
import org.clueminer.chart.api.ChartConfig;
import org.clueminer.chart.api.Overlay;
import org.clueminer.gui.ColorGenerator;
import org.clueminer.timeseries.chart.ChartFrameAdapter;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tomas Barton
 */
public class ChartPanel extends JLayeredPane implements Serializable {

    private static final long serialVersionUID = -8277777512558963882L;
    private ChartConfig chartFrame;
    private AnnotationPanel annotationPanel;
    private JLabel sampleInfo;
    private JToolBar overlayToolboxes;
    private List<Overlay> overlays;
    private boolean overlayToolboxesUpdated = false;

    public ChartPanel(ChartConfig frame) {
        chartFrame = frame;
        overlays = new ArrayList<Overlay>();
        initializeComponents();
    }

    private void initializeComponents() {
        annotationPanel = new AnnotationPanel(chartFrame);
        //chartFrame.getChartData().addDatasetListener(annotationPanel);

        overlayToolboxes = new JToolBar(JToolBar.HORIZONTAL);
        overlayToolboxes.setBorder(BorderFactory.createEmptyBorder());
        overlayToolboxes.setOpaque(false);
        overlayToolboxes.setFloatable(false);

        sampleInfo = new JLabel();
        sampleInfo.setOpaque(false);
        sampleInfo.setHorizontalAlignment(SwingConstants.LEFT);
        sampleInfo.setVerticalAlignment(SwingConstants.TOP);
        Font font = chartFrame.getChartProperties().getFont();
        font = font.deriveFont(font.getStyle() ^ Font.BOLD);
        sampleInfo.setFont(font);
        sampleInfo.setForeground(chartFrame.getChartProperties().getFontColor());

        setOpaque(false);
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
                int width = parent.getWidth();
                int height = parent.getHeight();

                sampleInfo.setBounds(0, 0, width, sampleInfo.getPreferredSize().height);
                annotationPanel.setBounds(0, 2, width - 4, height - 4);
                overlayToolboxes.setLocation(0, sampleInfo.getPreferredSize().height + 1);
            }
        });
        ChartFrameAdapter frameAdapter = new ChartFrameAdapter() {
            @Override
            public void chartChanged(Chart newChart) {
                repaint();
            }

            @Override
            public void overlayAdded(Overlay overlay) {
                addOverlay(overlay);
                chartFrame.getChartData().calculateRange(chartFrame, overlays);
                chartFrame.revalidate();
                chartFrame.repaint();
            }

            @Override
            public void overlayRemoved(Overlay overlay) {
                removeOverlay(overlay);
                chartFrame.getChartData().calculateRange(chartFrame, overlays);
                chartFrame.revalidate();
                chartFrame.repaint();
            }
        };
        chartFrame.addChartListener(frameAdapter);

        add(overlayToolboxes);
        add(annotationPanel);
        add(sampleInfo);
        doLayout();
    }

    public void setChartFrame(ChartFrame frame) {
        chartFrame = frame;
    }

    public AnnotationPanel getAnnotationPanel() {
        return annotationPanel;
    }

    public void setAnnotationPanel(AnnotationPanel panel) {
        annotationPanel = panel;
    }

    @Override
    public void paint(Graphics g) {
        Font font = chartFrame.getChartProperties().getFont();
        font = font.deriveFont(font.getStyle() ^ Font.BOLD);
        if (!sampleInfo.getFont().equals(font)) {
            sampleInfo.setFont(font);
        }

        if (!sampleInfo.getForeground().equals(chartFrame.getChartProperties().getFontColor())) {
            sampleInfo.setForeground(chartFrame.getChartProperties().getFontColor());
        }

        if (!overlayToolboxesUpdated) {
            updateOverlayToolbar();
        }

        Graphics2D g2 = (Graphics2D) g.create();
        setDoubleBuffered(true);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        g2.setPaintMode();

        chartFrame.getChartData().calculateRange(chartFrame, overlays);
        if (!chartFrame.getChartData().isChartNull()) {
            chartFrame.getChartData().getChart().paint(g2, chartFrame);
        }
        chartFrame.getChartData().updateLastX(getBounds());
        if (!overlays.isEmpty()) {
            Rectangle bounds = getBounds();
            bounds.grow(-2, -2);
            for (Overlay overlay : overlays) {
                overlay.paint(g2, chartFrame, bounds);
            }
        }
        sampleInfo.setText(chartFrame.getChartData().getName());

        super.paint(g);

        g2.dispose();
    }

    public synchronized void setOverlays(List<Overlay> list) {
        clearOverlays();
        ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
        cd.removeAllOverlaysDatasetListeners();
        for (Overlay o : list) {
            o.setDataset(chartFrame.getChartData().getVisible());
            o.calculate();
            cd.addOverlaysDatasetListeners(o);
            addOverlay(o);
        }
        overlayToolboxesUpdated = false;
    }

    public List<Overlay> getOverlays() {
        List<Overlay> list = new ArrayList<Overlay>();
        for (Overlay overlay : overlays) {
            list.add(overlay);
        }
        return list;
    }

    public Overlay getOverlay(int index) {
        if (index < 0 || index > overlays.size()) {
            return null;
        }
        return overlays.get(index);
    }

    public int getOverlaysCount() {
        return overlays.size();
    }

    public void addOverlay(Overlay overlay) {
        chartFrame.getChartProperties().addLogListener(overlay);
        ChartDataImpl cd = (ChartDataImpl) chartFrame.getChartData();
        cd.addOverlaysDatasetListeners(overlay);
        overlays.add(overlay);
        overlayToolboxesUpdated = false;
    }

    public void removeOverlay(Overlay overlay) {
        overlays.remove(overlay);
        overlayToolboxesUpdated = false;
    }

    public void clearOverlays() {
        overlays.clear();
        overlays = new ArrayList<Overlay>();
        overlayToolboxesUpdated = false;
    }

    public synchronized void updateOverlayToolbar() {
        int width = 0;
        int height = 0;

        overlayToolboxes.removeAll();
        for (Overlay overlay : overlays) {
            OverlayToolbox overlayToolbox = new OverlayToolbox(overlay);
            overlayToolboxes.add(overlayToolbox);
            overlayToolbox.update();

            width += overlayToolbox.getWidth() + 16;
            height = overlayToolbox.getHeight() + 4;
        }
        overlayToolboxes.validate();
        overlayToolboxes.repaint();
        overlayToolboxesUpdated = true;

        overlayToolboxes.setBounds(overlayToolboxes.getX(), overlayToolboxes.getY(), width, height);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    public final class OverlayToolbox extends JToolBar implements Serializable {

        private static final long serialVersionUID = 2223360025637017930L;
        private Overlay overlay;
        private JLabel overlayLabel;
        private JComponent container;
        public boolean mouseOver = false;
        private final Color backColor = ColorGenerator.getTransparentColor(new Color(0x1C2331), 50);

        public OverlayToolbox(Overlay overlay) {
            super(JToolBar.HORIZONTAL);
            this.overlay = overlay;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

            overlayLabel = new JLabel(overlay.getLabel());
            overlayLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            overlayLabel.setVerticalTextPosition(SwingConstants.CENTER);
            overlayLabel.setOpaque(false);
            overlayLabel.setBorder(BorderFactory.createEmptyBorder());
            add(overlayLabel);

            container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
            add(container);
            update();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseOver = true;
                    revalidate();
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    mouseOver = false;
                    revalidate();
                    repaint();
                }
            });
        }

        @Override
        public int getWidth() {
            return getLayout().preferredLayoutSize(this).width;
        }

        @Override
        public int getHeight() {
            return getLayout().preferredLayoutSize(this).height;
        }

        public void update() {
            // remove all buttons
            container.removeAll();

            OverlayToolboxButton button;

            // Settings
            container.add(button = new OverlayToolboxButton(overlaySettings(overlay)));
            button.setText("");
            button.setToolTipText("Settings");

            // Remove
            container.add(button = new OverlayToolboxButton(removeAction(overlay)));
            button.setText("");
            button.setToolTipText("Remove");

            revalidate();
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            overlayLabel.setFont(ChartPanel.this.chartFrame.getChartProperties().getFont());
            overlayLabel.setForeground(ChartPanel.this.chartFrame.getChartProperties().getFontColor());
            overlayLabel.setText(overlay.getLabel());

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

            g2.setPaintMode();

            if (mouseOver) {
                g2.setColor(backColor);
                int x = overlayLabel.getLocation().x - getInsets().left;
                int y = overlayLabel.getLocation().y - getInsets().top;
                RoundRectangle2D roundRectangle = new RoundRectangle2D.Double(x, y, getWidth(), getHeight(), 10, 10);
                g2.fill(roundRectangle);
            }

            super.paint(g);

            g2.dispose();
        }

        public class OverlayToolboxButton extends JButton implements Serializable {

            private static final long serialVersionUID = 6266934888054843208L;

            public OverlayToolboxButton(Action action) {
                super(action);
                setOpaque(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
                setBorder(new Border() {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    }

                    @Override
                    public Insets getBorderInsets(Component c) {
                        return new Insets(0, 2, 0, 2);
                    }

                    @Override
                    public boolean isBorderOpaque() {
                        return true;
                    }
                });
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        mouseOver = false;
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        mouseOver = true;
                    }
                });
            }
        }
    }

    private AbstractAction overlaySettings(final Overlay overlay) {

        return new AbstractAction("Overlay Settings", ImageUtilities.loadImageIcon("org/clueminer/timeseries/resources/settings16.png", false)) {
            private static final long serialVersionUID = 8711948053140429014L;

            @Override
            public void actionPerformed(ActionEvent e) {
                //   SettingsPanel.getDefault().openSettingsWindow(overlay);
            }
        };
    }

    private AbstractAction removeAction(final Overlay overlay) {
        return new AbstractAction("Remove Indicator", ImageUtilities.loadImageIcon("org/clueminer/timeseries/resources/remove.png", false)) {
            private static final long serialVersionUID = 8711948053140429014L;

            @Override
            public void actionPerformed(ActionEvent e) {
                ChartPanel.this.removeOverlay(overlay);
                ChartPanel.this.validate();
                ChartPanel.this.repaint();
            }
        };
    }
}
