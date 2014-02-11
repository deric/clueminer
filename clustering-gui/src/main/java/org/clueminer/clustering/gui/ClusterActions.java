package org.clueminer.clustering.gui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.LONG_DESCRIPTION;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.clueminer.export.impl.CsvExporter;
import org.clueminer.export.impl.ImageExporter;
import org.netbeans.api.print.PrintManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor.InputLine;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public final class ClusterActions {

    private static String title = "";

    private ClusterActions() {
    }

    /*
     * ClusterAnalysis popup actions & ProjectToolbar actions
     */
    public static Action zoomIn(ClusterAnalysis clusteringFrame) {
        return ZoomIn.getAction(clusteringFrame);
    }

    public static Action zoomOut(ClusterAnalysis clusteringFrame) {
        return ZoomOut.getAction(clusteringFrame);
    }

    public static Action zoomShowAll(ClusterAnalysis clusteringFrame) {
        return ZoomShowAll.getAction(clusteringFrame);
    }

    public static Action clusterPopup(ClusterAnalysis clusteringFrame) {
        return ClusterPopup.getAction(clusteringFrame);
    }

    public static Action exportImage(ClusterAnalysis clusteringFrame) {
        return ExportImage.getAction(clusteringFrame);
    }

    public static Action printChart(ClusterAnalysis clusteringFrame) {
        return PrintChart.getAction(clusteringFrame);
    }

    public static Action chartProperties(ClusterAnalysis clusteringFrame) {
        return ChartProps.getAction(clusteringFrame);
    }

    public static Action clusterExport(ClusterAnalysis clusteringFrame) {
        return ClusterExport.getAction(clusteringFrame);
    }

    public static Action saveToTemplate(ClusterAnalysis clusteringFrame) {
        return SaveToTemplate.getAction(clusteringFrame);
    }

    /*
     * Submenu actions
     */
    public static Action changeAlgorithm(ClusterAnalysis clusteringFrame, String chartName, boolean current) {
        return ChangeAlgoritm.getAction(clusteringFrame, chartName, current);
    }

    /*
     * ProjectToolbar popup actions
     */
    public static Action toggleToolbarSmallIcons(ClusterAnalysis clusteringFrame, ClusteringToolbar clusteringToolbar) {
        return ToggleToolbarSmallIcons.getAction(clusteringFrame, clusteringToolbar);
    }

    public static Action toggleToolbarShowLabels(ClusterAnalysis clusteringFrame, ClusteringToolbar clusteringToolbar) {
        return ToggleToolbarShowLabels.getAction(clusteringFrame, clusteringToolbar);
    }


    /*
     * Abstract MainAction
     */
    private static abstract class MainAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public MainAction(String name, boolean flag) {
            putValue(NAME, NbBundle.getMessage(ClusterActions.class, "ACT_" + name));
            putValue(SHORT_DESCRIPTION,
                     NbBundle.getMessage(ClusterActions.class, "TOOL_" + name));
            System.out.println("org/clueminer/clustering/resources/" + name.toLowerCase() + "16.png");
            if (flag) {
                Icon ico = ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/" + name + "16.png", true);
                System.out.println("ico: " + name + " = " + ico);
                putValue(SMALL_ICON,
                         ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/" + name + "16.png", true));
                putValue(LONG_DESCRIPTION, name);
                putValue(LARGE_ICON_KEY,
                         ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/" + name + "24.png", true));
            }
        }
    }

    private static class ZoomShowAll extends MainAction {

        private static final long serialVersionUID = 1L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ZoomShowAll(clusteringFrame);
        }

        private ZoomShowAll(ClusterAnalysis clusteringFrame) {
            super("ZoomShowAll", true);
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e);
            // clusteringFrame.setZoom(1.0);
        }
    }

    private static class ZoomIn extends MainAction {

        private static final long serialVersionUID = 1L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ZoomIn(clusteringFrame);
        }

        private ZoomIn(ClusterAnalysis clusteringFrame) {
            super("ZoomIn", true);
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clusteringFrame.zoomIn();
        }
    }

    private static class ZoomOut extends MainAction {

        private static final long serialVersionUID = 1L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ZoomOut(clusteringFrame);
        }

        private ZoomOut(ClusterAnalysis clusteringFrame) {
            super("ZoomOut", true);
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clusteringFrame.zoomOut();
        }
    }

    private static class ClusterPopup extends AbstractAction {

        private static final long serialVersionUID = 1633826646185767281L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ClusterPopup(clusteringFrame);
        }

        private ClusterPopup(ClusterAnalysis clusteringFrame) {
            putValue(NAME, NbBundle.getMessage(ClusterActions.class, "ACT_Clustering"));
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ClusterActions.class, "TOOL_Clustering"));
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/clustering16.png", true));
            putValue(LONG_DESCRIPTION, "clustering");
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/clustering24.png", true));
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String current = clusteringFrame.getAlgorithm().getName();

            JButton button = (JButton) e.getSource();
            JPopupMenu popupMenu = new JPopupMenu();

            JMenuItem item;
            for (String algorithm : ClusteringDialogFactory.getInstance().getProviders()) {
                popupMenu.add(item = new JMenuItem(
                        ClusterActions.changeAlgorithm(clusteringFrame, algorithm, algorithm.equals(current))));
                item.setMargin(new Insets(0, 0, 0, 0));
            }

            popupMenu.show(button, 0, button.getHeight());
        }
    }

    private static class ChangeAlgoritm extends MainAction {

        private static final long serialVersionUID = 4315986500869885780L;
        private ClusterAnalysis clusteringFrame;
        private String algorithmName;
        private AlgorithmDialog algorithmDialog;

        public static Action getAction(ClusterAnalysis clusteringFrame, String chartName, boolean current) {
            return new ChangeAlgoritm(clusteringFrame, chartName, current);
        }

        private ChangeAlgoritm(ClusterAnalysis clusteringFrame, String algName, boolean current) {
            super("Clustering", current);
            this.clusteringFrame = clusteringFrame;
            this.algorithmName = algName;
            algorithmDialog = new AlgorithmDialog();
            putValue(NAME, algName);
            putValue(SHORT_DESCRIPTION, algName);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClusteringDialog config = ClusteringDialogFactory.getInstance().getProvider(algorithmName);
            algorithmDialog.showDialog(clusteringFrame, config);
        }
    }

    private static class ExportImage extends AbstractAction {

        private static final long serialVersionUID = -661781520577850266L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ExportImage(clusteringFrame);
        }

        private ExportImage(ClusterAnalysis clusteringFrame) {
            this.clusteringFrame = clusteringFrame;
            putValue(NAME, NbBundle.getMessage(ClusterActions.class, "ACT_ExportImage"));
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ClusterActions.class, "TOOL_ExportImage"));
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/exportimage16.png", true));
            putValue(LONG_DESCRIPTION, "Export Image");
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/exportimage24.png", true));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ImageExporter.getDefault().export(clusteringFrame.getMainPanel());
        }
    }

    private static class ClusterExport extends AbstractAction {

        private static final long serialVersionUID = -661781520577850266L;
        private final ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ClusterExport(clusteringFrame);
        }

        private ClusterExport(ClusterAnalysis clusteringFrame) {
            this.clusteringFrame = clusteringFrame;
            putValue(NAME, NbBundle.getMessage(ClusterActions.class, "ACT_ClusterExport"));
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ClusterActions.class, "TOOL_ClusterExport"));
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/csvexport16.png", true));
            putValue(LONG_DESCRIPTION, "Export cluster assignments");
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/csvexport24.png", true));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CsvExporter.getDefault().setAnalysis(clusteringFrame);
            CsvExporter.getDefault().showDialog();
        }
    }

    private static class PrintChart extends AbstractAction {

        private static final long serialVersionUID = -8912120693706179845L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new PrintChart(clusteringFrame);
        }

        private PrintChart(ClusterAnalysis clusteringFrame) {
            putValue(NAME, NbBundle.getMessage(ClusterActions.class, "ACT_Print"));
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ClusterActions.class, "TOOL_Print"));
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/print16.png", true));
            putValue(LONG_DESCRIPTION, "Print");
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/print24.png", true));
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PrintManager.printAction((JComponent) clusteringFrame.getMainPanel()).actionPerformed(e);
        }
    }

    private static class ChartProps extends AbstractAction {

        private static final long serialVersionUID = -2363806065135286271L;
        private final ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new ChartProps(clusteringFrame);
        }

        private ChartProps(ClusterAnalysis clusteringFrame) {
            putValue(NAME, NbBundle.getMessage(ClusterActions.class, "ACT_ClusteringProperties"));
            putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ClusterActions.class, "TOOL_ClusteringProperties"));
            putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/clusteringproperties16.png", true));
            putValue(LONG_DESCRIPTION, "Clustering Properties");
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/clusteringproperties24.png", true));
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DialogDescriptor descriptor = new DialogDescriptor(this, title, true, null);
            descriptor.setMessageType(DialogDescriptor.PLAIN_MESSAGE);
            descriptor.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
            Object ret = DialogDisplayer.getDefault().notify(descriptor);
            if (ret != null) {
                /* if (object instanceof ChartFrame) {
                 ((ChartFrame) object).repaint();
                 } else if (object instanceof AbstractOverlay) {
                 ((AbstractOverlay) object).calculate();
                 } */
            }
        }
    }

    private static class ToggleToolbarSmallIcons extends MainAction {

        private static final long serialVersionUID = 1L;
        private final ClusterAnalysis clusteringFrame;
        private final ClusteringToolbar clusteringToolbar;

        public static Action getAction(ClusterAnalysis clusteringFrame, ClusteringToolbar clusteringToolbar) {
            return new ToggleToolbarSmallIcons(clusteringFrame, clusteringToolbar);
        }

        private ToggleToolbarSmallIcons(ClusterAnalysis clusteringFrame, ClusteringToolbar clusteringToolbar) {
            super("SmallToolbarIcons", false);
            this.clusteringFrame = clusteringFrame;
            this.clusteringToolbar = clusteringToolbar;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //clusteringFrame.getChartProperties().toggleToolbarSmallIcons();
            clusteringToolbar.toggleIcons();
        }
    }

    private static class ToggleToolbarShowLabels extends MainAction {

        private static final long serialVersionUID = 1L;
        private ClusterAnalysis clusteringFrame;
        private ClusteringToolbar clusteringToolbar;

        public static Action getAction(ClusterAnalysis clusteringFrame, ClusteringToolbar clusteringToolbar) {
            return new ToggleToolbarShowLabels(clusteringFrame, clusteringToolbar);
        }

        private ToggleToolbarShowLabels(ClusterAnalysis clusteringFrame, ClusteringToolbar clusteringToolbar) {
            super("HideLabels", false);
            this.clusteringFrame = clusteringFrame;
            this.clusteringToolbar = clusteringToolbar;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clusteringToolbar.toggleLabels();
        }
    }

    public static JMenu generateChartsMenu(ClusterAnalysis clusteringFrame) {
        JMenuItem menuItem;

        JMenu menu = new JMenu(NbBundle.getMessage(ClusterActions.class, "ACT_Charts"));
        menu.setIcon(ImageUtilities.loadImageIcon("org/clueminer/clustering/resources/chart16.png", false));

        String current = clusteringFrame.getAlgorithm().getName();

        for (String alg : ClusteringDialogFactory.getInstance().getProviders()) {
            menu.add(menuItem = new JMenuItem(ClusterActions.changeAlgorithm(clusteringFrame, alg, current.equals(alg))));
            menuItem.setMargin(new Insets(0, 0, 0, 0));
        }

        return menu;
    }

    private static JMenu generateTempMenu(ClusterAnalysis clusteringFrame) {
        JMenu menu = new JMenu(NbBundle.getMessage(ClusterActions.class, "ACT_SelectTemplate"));
        /*
         * for (Object template :
         * TemplateFactory.getInstance().getTemplateNames()) { if
         * ((clusteringFrame.getTemplate() == null) ||
         * (!template.equals(clusteringFrame.getTemplate().getName()))) {
         * menu.add(new JMenuItem(ChangeTemplate.getAction(clusteringFrame,
         * (String) template))); } }
         */
        return menu;
    }

    public static JMenu generateTemplatesMenu(ClusterAnalysis clusteringFrame) {
        JMenu menu = new JMenu(NbBundle.getMessage(ClusterActions.class, "ACT_Templates"));
        menu.add(generateTempMenu(clusteringFrame));
        menu.add(new JMenuItem(ClusterActions.saveToTemplate(clusteringFrame)));
        return menu;
    }

    private static class ChangeTemplate extends MainAction {

        private static final long serialVersionUID = 1L;
        private ClusterAnalysis clusteringFrame;
        private String template;

        public static Action getAction(ClusterAnalysis clusteringFrame, String template) {
            return new ChangeTemplate(clusteringFrame, template);
        }

        private ChangeTemplate(ClusterAnalysis clusteringFrame, String template) {
            super("SaveToTemplate", false);
            this.clusteringFrame = clusteringFrame;
            this.template = template;
            putValue(NAME, template);
            putValue(SHORT_DESCRIPTION, template);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //   clusteringFrame.setTemplate(TemplateFactory.getInstance().getTemplate(template));
        }
    }

    private static class SaveToTemplate extends MainAction {

        private static final long serialVersionUID = 1L;
        private ClusterAnalysis clusteringFrame;

        public static Action getAction(ClusterAnalysis clusteringFrame) {
            return new SaveToTemplate(clusteringFrame);
        }

        private SaveToTemplate(ClusterAnalysis clusteringFrame) {
            super("SaveToTemplate", false);
            this.clusteringFrame = clusteringFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            InputLine descriptor = new DialogDescriptor.InputLine(
                    "Template Name:", "Save to Template");
            descriptor.setOptions(new Object[]{
                DialogDescriptor.OK_OPTION,
                DialogDescriptor.CANCEL_OPTION
            });
            Object ret = DialogDisplayer.getDefault().notify(descriptor);
            if (ret.equals(DialogDescriptor.OK_OPTION)) {
                String name = descriptor.getInputText();
                /*
                 * if (!TemplateFactory.getInstance().templateExists(name)) {
                 * TemplateFactory.getInstance().saveToTemplate(name,
                 * clusteringFrame); } else { Confirmation confirmation = new
                 * DialogDescriptor.Confirmation( "<html>This template already
                 * exists!<br>Do you want to overwrite this template?</html>",
                 * "Overwrite"); Object obj =
                 * DialogDisplayer.getInstance().notify(confirmation); if
                 * (obj.equals(DialogDescriptor.OK_OPTION)) {
                 * TemplateFactory.getInstance().removeTemplate(name);
                 * TemplateFactory.getInstance().saveToTemplate(name,
                 * clusteringFrame); } }
                 */
            }
        }
    }
}
