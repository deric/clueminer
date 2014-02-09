package org.clueminer.chart;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.clueminer.gui.BottomBorder;
import org.clueminer.gui.ToolbarButton;
import org.clueminer.gui.ToolbarToggleButton;

/**
 *
 * @author Tomas Barton
 */
public class ChartToolbar extends JToolBar implements Serializable {

    private static final long serialVersionUID = 3219856833393000717L;
    private ChartFrame chartFrame;

    public ChartToolbar(ChartFrame frame) {
        super("ChartToolbar", JToolBar.HORIZONTAL);
        chartFrame = frame;
        initComponents();
        setFloatable(false);
        setBorder(new BottomBorder());
        addMouseListener(new ToolbarOptions(this));
    }

    private void initComponents() {
        // ChartToolbar buttons
        add(zoomInBtn = ToolbarButton.getButton(MainActions.zoomIn(chartFrame)));
        add(zoomOutBtn = ToolbarButton.getButton(MainActions.zoomOut(chartFrame)));
        add(zoomShowAll = ToolbarButton.getButton(MainActions.zoomShowAll(chartFrame)));
        add(chartBtn = ToolbarButton.getButton(MainActions.chartPopup(chartFrame)));
        add(overlaysBtn = ToolbarButton.getButton(MainActions.openOverlays(chartFrame)));
        add(annotationsBtn = ToolbarButton.getButton(MainActions.annotationPopup(chartFrame)));
        add(markerBtn = ToolbarToggleButton.getButton(MainActions.toggleMarker(chartFrame)));
        add(exportBtn = ToolbarButton.getButton(MainActions.exportImage(chartFrame)));
        add(printBtn = ToolbarButton.getButton(MainActions.printChart(chartFrame)));
        add(propertiesBtn = ToolbarButton.getButton(MainActions.chartProperties(chartFrame)));

        markerBtn.setSelected(true);
    }

    public void updateToolbar() {
    }

    public void toggleLabels() {
        boolean show = chartFrame.getChartProperties().getToolbarShowLabels();
        zoomInBtn.toggleLabel(show);
        zoomOutBtn.toggleLabel(show);
        zoomShowAll.toggleLabel(show);
        chartBtn.toggleLabel(show);
        overlaysBtn.toggleLabel(show);
        annotationsBtn.toggleLabel(show);
        markerBtn.toggleLabel(show);
        exportBtn.toggleLabel(show);
        printBtn.toggleLabel(show);
        propertiesBtn.toggleLabel(show);
    }

    public void toggleIcons() {
        boolean small = chartFrame.getChartProperties().getToolbarSmallIcons();
        zoomInBtn.toggleIcon(small);
        zoomOutBtn.toggleIcon(small);
        zoomShowAll.toggleIcon(small);
        chartBtn.toggleIcon(small);
        overlaysBtn.toggleIcon(small);
        annotationsBtn.toggleIcon(small);
        markerBtn.toggleIcon(small);
        exportBtn.toggleIcon(small);
        printBtn.toggleIcon(small);
        propertiesBtn.toggleIcon(small);
    }

    public JPopupMenu getToolbarMenu() {
        JPopupMenu popup = new JPopupMenu();
        JCheckBoxMenuItem item;

        popup.add(item = new JCheckBoxMenuItem(
                MainActions.toggleToolbarSmallIcons(chartFrame, this)));
        item.setMargin(new Insets(0, 0, 0, 0));
        item.setState(chartFrame.getChartProperties().getToolbarSmallIcons());


        popup.add(item = new JCheckBoxMenuItem(
                MainActions.toggleToolbarShowLabels(chartFrame, this)));
        item.setMargin(new Insets(0, 0, 0, 0));
        item.setState(!chartFrame.getChartProperties().getToolbarShowLabels());

        return popup;
    }

    public static class ToolbarOptions extends MouseAdapter {

        private ChartToolbar toolbar;

        public ToolbarOptions(ChartToolbar bar) {
            toolbar = bar;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                toolbar.getToolbarMenu().show(toolbar, e.getX(), e.getY());
            }
        }
    }
    private ToolbarButton zoomInBtn;
    private ToolbarButton zoomOutBtn;
    private ToolbarButton zoomShowAll;
    private ToolbarButton chartBtn;
    private ToolbarButton overlaysBtn;
    private ToolbarButton annotationsBtn;
    private ToolbarToggleButton markerBtn;
    private ToolbarButton exportBtn;
    private ToolbarButton printBtn;
    private ToolbarButton propertiesBtn;
}
