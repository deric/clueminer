package org.clueminer.wellmap;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import javax.swing.JLayeredPane;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;

/**
 *
 * @author Tomas Barton
 */
public class WellMapFrame extends JLayeredPane implements DatasetListener, Serializable, TreeListener {

    private static final long serialVersionUID = -8022332634458493029L;
    private HtsPlate<HtsInstance> plate = null;
    private WellGrid grid;
    private SelectedWells selectedWells;

    public WellMapFrame() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(true);
        grid = new WellGrid();
        selectedWells = new SelectedWells(grid);
        setBackground(Color.WHITE);

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

                grid.setBounds(insets.left, insets.top, w, h);
                selectedWells.setBounds(insets.left, insets.top, w, h);
            }
        });

        add(grid, 0);
        add(selectedWells, 1); //upper lever

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
                update();
            }
        });
    }

    @Override
    public void datasetChanged(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetOpened(DatasetEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void datasetClosed(DatasetEvent evt) {
        repaint();
    }

    @Override
    public void datasetCropped(DatasetEvent evt) {
        //TODO update dataset
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        if (plate != null) {
            if (cluster.firstElem > -1) {
                HtsPlate selected = (HtsPlate) data.getInstances().duplicate();

                for (int i = cluster.firstElem; i <= cluster.lastElem; i++) {
                    selected.add(plate.instance(data.getRowIndex(i)));
                }
                selectedWells.setSelected(selected);
                selectedWells.repaint();
            }
        }
    }

    public void setSelected(HtsPlate<HtsInstance> p) {
        selectedWells.setSelected(p);
    }

    /**
     * @return the plate
     */
    public HtsPlate getPlate() {
        return plate;
    }

    public void update() {
        Rectangle bounds = getBounds();
        Dimension dim = grid.updateSize(new Dimension(bounds.width, bounds.height));
        selectedWells.updateSize(dim);
        setPreferredSize(dim);
    }

    /**
     * @param p the plate to set
     */
    public void setPlate(HtsPlate p) {
        if (p != null) {
            this.plate = p;
            grid.setDimensions(p.getRowsCount(), p.getColumnsCount());
            selectedWells.clearCache();
            selectedWells.setPlate(p);

        }
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
    }
}
