package org.clueminer.dendrogram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.gui.ClusteringExport;
import org.clueminer.export.impl.ImageExporter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tomas Barton
 */
public class DendroToolbar extends JToolBar {

    private static final long serialVersionUID = 3796559248116111100L;
    private JButton btnFitToSpace;
    private JButton btnScreenshot;
    private JButton btnExport;
    private JToggleButton btnEvaluation;
    private JToggleButton btnLegend;
    private final DendroViewer viewer;

    public DendroToolbar(DendroViewer viewer) {
        super(SwingConstants.HORIZONTAL);
        this.viewer = viewer;
        initComponents();
    }

    private void initComponents() {

        this.setFloatable(false);
        this.setRollover(true);

        btnFitToSpace = new JButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/fullscreen16.png", false));
        btnFitToSpace.setToolTipText("Fit to window");
        btnFitToSpace.setSelected(true);
        btnScreenshot = new JButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/screenshot16.png", false));
        btnScreenshot.setToolTipText("Make a screenshot of this window");
        btnExport = new JButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/save16.png", false));
        btnExport.setToolTipText("Export this dendrogram");
        btnEvaluation = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/eval16.png", false));
        btnLegend = new JToggleButton(ImageUtilities.loadImageIcon("org/clueminer/dendrogram/gui/legend16.png", false));
        btnLegend.setSelected(true);
        add(btnFitToSpace);
        add(btnScreenshot);
        add(btnExport);
        add(btnEvaluation);
        add(btnLegend);
        addSeparator();

        btnFitToSpace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setFitToPanel(btnFitToSpace.isSelected());
            }
        });

        btnScreenshot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ImageExportDialog exportDialog = new ImageExportDialog();
                DialogDescriptor dd = new DialogDescriptor(exportDialog, "Screenshot");
                if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    //exportDialog.destroy();
                    return;
                }
                //exportDialog.destroy();

                if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                    ImageExporter.getDefault().export(viewer);
                }

            }
        });

        btnExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FileExportDialog exportDialog = new FileExportDialog();
                DialogDescriptor dd = new DialogDescriptor(exportDialog, "Export to...");
                if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                    //exportDialog.destroy();
                    return;
                }
                //exportDialog.destroy();

                if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                    ClusteringExport exp = exportDialog.getExporter();
                    exp.setViewer(viewer);
                    exp.export();
                }
            }
        });

        btnEvaluation.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setEvaluationVisible(!viewer.isEvaluationVisible());
            }
        });

        btnLegend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.setLegendVisible(!viewer.isLegendVisible());
            }
        });

    }

}
