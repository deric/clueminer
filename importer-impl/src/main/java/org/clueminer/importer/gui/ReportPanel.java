package org.clueminer.importer.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.clueminer.gui.BusyUtils;
import org.clueminer.importer.FileImporterFactory;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.Issue;
import org.clueminer.importer.impl.ImportControllerImpl;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.Report;
import org.clueminer.processor.spi.Processor;
import org.clueminer.processor.spi.ProcessorUI;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.ImporterUI;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author deric
 */
public class ReportPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1692175812146977202L;
    //Preferences
    private final static String SHOW_ISSUES = "ReportPanel_Show_Issues";
    private final static String SHOW_REPORT = "ReportPanel_Show_Report";
    private final static int ISSUES_LIMIT = 5000;
    private ThreadGroup fillingThreads;
    //Icons
    private ImageIcon infoIcon;
    private ImageIcon warningIcon;
    private ImageIcon severeIcon;
    private ImageIcon criticalIcon;
    //Container
    private Container container;
    //UI
    private ButtonGroup processorGroup = new ButtonGroup();
    private Outline issuesOutline;
    protected LinkedHashMap<String, FileImporter> providers;
    private final ImportController controller;
    private FileImporter fileImporter;
    private ImporterUI importerUI;
    private GridBagConstraints gbc;
    private ColumnsPreview colPreviewPane;

    /**
     * Creates new form ReportPanel
     */
    public ReportPanel() {
        fillingThreads = new ThreadGroup("Report Panel Issues");
        //controller = Lookup.getDefault().lookup(ImportController.class);
        controller = new ImportControllerImpl();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    issuesOutline = new org.netbeans.swing.outline.Outline();
                    initComponents();
                    tab1ScrollPane.setViewportView(issuesOutline);
                    initIcons();
                    initPreview();
                    initImporters();
                    initProcessors();
                    initProcessorsUI();
                }
            });
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void initIcons() {
        infoIcon = new javax.swing.ImageIcon(getClass().getResource("/org/clueminer/importer/gui/info.png"));
        warningIcon = new javax.swing.ImageIcon(getClass().getResource("/org/clueminer/importer/gui/warning.gif"));
        severeIcon = new javax.swing.ImageIcon(getClass().getResource("/org/clueminer/importer/gui/severe.png"));
        criticalIcon = new javax.swing.ImageIcon(getClass().getResource("/org/clueminer/importer/gui/critical.png"));
    }

    public void setData(Report report, Container container) {
        this.container = container;

        report.pruneReport(ISSUES_LIMIT);
        fillIssues(report);
        fillReport(report);

        fillStats(container);
        fillParameters(container);
    }

    private void fillIssues(Report report) {
        final List<Issue> issues = report.getIssues();
        if (issues.isEmpty()) {
            JLabel label = new JLabel(NbBundle.getMessage(getClass(), "ReportPanel.noIssues"));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            tab1ScrollPane.setViewportView(label);
        } else {
            //Busy label
            final BusyUtils.BusyLabel busyLabel = BusyUtils.createCenteredBusyLabel(tab1ScrollPane, "Retrieving issues...", issuesOutline);

            //Thread
            Thread thread = new Thread(fillingThreads, new Runnable() {
                @Override
                public void run() {
                    busyLabel.setBusy(true);
                    final TreeModel treeMdl = new IssueTreeModel(issues);
                    final OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, new IssueRowModel(), true);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            issuesOutline.setRootVisible(false);
                            issuesOutline.setRenderDataProvider((RenderDataProvider) new IssueRenderer());
                            issuesOutline.setModel(mdl);
                            busyLabel.setBusy(false);
                        }
                    });
                }
            }, "Report Panel Issues Outline");
            if (NbPreferences.forModule(ReportPanel.class).getBoolean(SHOW_ISSUES, true)) {
                thread.start();
            }
        }
    }

    public String[] getImporterProviders() {
        Collection<? extends FileImporter> list = FileImporterFactory.getInstance().getAll();
        String[] res = new String[list.size()];
        providers = new LinkedHashMap<String, FileImporter>();
        int i = 0;
        for (FileImporter importer : list) {
            providers.put(importer.getName(), importer);
            res[i++] = importer.getName();
        }
        return res;
    }

    private void initImporters() {

        importerPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);

        cbImporter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //importer selected
                comboImporterChanged();
            }
        });
        comboImporterChanged();
    }

    private void initPreview() {
        columnsPreview.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);

        colPreviewPane = new ColumnsPreview();
        columnsPreview.add(colPreviewPane);
        columnsPreview.validate();
        columnsPreview.revalidate();
        columnsPreview.repaint();

    }

    private void comboImporterChanged() {
        if (cbImporter.getSelectedItem() != null) {
            FileImporter fi = FileImporterFactory.getInstance().getProvider((String) cbImporter.getSelectedItem());
            if (fi != null) {
                fileImporterChanged(fi);
            }
        }
    }

    private void fileImporterChanged(FileImporter importer) {
        if (importerUI != null) {
            importerUI.unsetup(false);
            importerUI.removeListener(colPreviewPane);
            importerPanel.removeAll();
        }
        fileImporter = importer;
        if (controller != null) {
            importerUI = controller.getUI(importer);
            System.out.println("importer UI: " + importerUI);
            if (importerUI != null) {
                JPanel panel = importerUI.getPanel();
                importerUI.setup(importer);
                importerUI.addListener(colPreviewPane);
                importerUI.fireImporterChanged();

                importerPanel.add(panel, gbc);
                importerPanel.validate();
                importerPanel.revalidate();
                importerPanel.repaint();
            }
        } else {
            Logger.getLogger(ReportPanel.class.getName()).log(Level.SEVERE, "no controller found");
        }
    }

    private void fillReport(final Report report) {
        Thread thread = new Thread(fillingThreads, new Runnable() {
            @Override
            public void run() {
                final String str = report.getText();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        reportEditor.setText(str);
                    }
                });
            }
        }, "Report Panel Issues Report");
        if (NbPreferences.forModule(ReportPanel.class).getBoolean(SHOW_REPORT, true)) {
            thread.start();
        }
    }

    private void fillParameters(final Container container) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //get number of lines etc.

            }
        });
    }

    private void fillStats(final Container container) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Source
                String source = container.getSource();
                if (source != null) {
                    String[] label = source.split("\\.");
                    if (label.length > 2 && label[label.length - 2].matches("\\d+")) { //case of temp file
                        source = source.replaceFirst("." + label[label.length - 2], "");
                    }
                }

                sourceLabel.setText(source);
                lbNumLines.setText(String.valueOf(container.getLoader().getNumberOfLines()));
                lbAttr.setText(String.valueOf(container.getLoader().getNumberAttributes()));
            }
        });
    }

    private static final Object PROCESSOR_KEY = new Object();

    private void initProcessors() {
        int i = 0;
        for (Processor processor : Lookup.getDefault().lookupAll(Processor.class)) {
            JRadioButton radio = new JRadioButton(processor.getDisplayName());
            radio.setSelected(i == 0);
            radio.putClientProperty(PROCESSOR_KEY, processor);
            processorGroup.add(radio);
            GridBagConstraints constraints = new GridBagConstraints(0, i++, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            processorPanel.add(radio, constraints);
        }
    }

    private void initProcessorsUI() {
        for (Enumeration<AbstractButton> enumeration = processorGroup.getElements(); enumeration.hasMoreElements();) {
            AbstractButton radioButton = enumeration.nextElement();
            Processor p = (Processor) radioButton.getClientProperty(PROCESSOR_KEY);
            //Enabled
            ProcessorUI pui = getProcessorUI(p);
            if (pui != null) {
                radioButton.setEnabled(pui.isValid(container));
            }
        }
    }

    public void destroy() {
        fillingThreads.interrupt();
    }

    public Processor getProcessor() {
        for (Enumeration<AbstractButton> enumeration = processorGroup.getElements(); enumeration.hasMoreElements();) {
            AbstractButton radioButton = enumeration.nextElement();
            if (radioButton.isSelected()) {
                return (Processor) radioButton.getClientProperty(PROCESSOR_KEY);
            }
        }
        return null;
    }

    private ProcessorUI getProcessorUI(Processor processor) {
        for (ProcessorUI pui : Lookup.getDefault().lookupAll(ProcessorUI.class)) {
            if (pui.isUIFoProcessor(processor)) {
                return pui;
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        tab1ScrollPane = new javax.swing.JScrollPane();
        tab2ScrollPane = new javax.swing.JScrollPane();
        reportEditor = new javax.swing.JEditorPane();
        lbSource = new javax.swing.JLabel();
        sourceLabel = new javax.swing.JLabel();
        statsPanel = new javax.swing.JPanel();
        lbLines = new javax.swing.JLabel();
        lbNumLines = new javax.swing.JLabel();
        lbAttributes = new javax.swing.JLabel();
        lbAttr = new javax.swing.JLabel();
        processorPanel = new javax.swing.JPanel();
        importerPanel = new javax.swing.JPanel();
        lbImport = new javax.swing.JLabel();
        cbImporter = new JComboBox(getImporterProviders());
        jScrollPane1 = new javax.swing.JScrollPane();
        columnsPreview = new javax.swing.JPanel();

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.tab1ScrollPane.TabConstraints.tabTitle"), tab1ScrollPane); // NOI18N

        tab2ScrollPane.setViewportView(reportEditor);

        tabbedPane.addTab(org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.tab2ScrollPane.TabConstraints.tabTitle"), tab2ScrollPane); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbSource, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.lbSource.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sourceLabel, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.sourceLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbLines, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.lbLines.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbNumLines, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.lbNumLines.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbAttributes, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.lbAttributes.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbAttr, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.lbAttr.text")); // NOI18N

        javax.swing.GroupLayout statsPanelLayout = new javax.swing.GroupLayout(statsPanel);
        statsPanel.setLayout(statsPanelLayout);
        statsPanelLayout.setHorizontalGroup(
            statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statsPanelLayout.createSequentialGroup()
                        .addComponent(lbAttributes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbAttr)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(statsPanelLayout.createSequentialGroup()
                        .addComponent(lbLines)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbNumLines)))
                .addContainerGap())
        );
        statsPanelLayout.setVerticalGroup(
            statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbLines)
                    .addComponent(lbNumLines))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbAttributes)
                    .addComponent(lbAttr))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout importerPanelLayout = new javax.swing.GroupLayout(importerPanel);
        importerPanel.setLayout(importerPanelLayout);
        importerPanelLayout.setHorizontalGroup(
            importerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        importerPanelLayout.setVerticalGroup(
            importerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 84, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout processorPanelLayout = new javax.swing.GroupLayout(processorPanel);
        processorPanel.setLayout(processorPanelLayout);
        processorPanelLayout.setHorizontalGroup(
            processorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(processorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        processorPanelLayout.setVerticalGroup(
            processorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, processorPanelLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(importerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.openide.awt.Mnemonics.setLocalizedText(lbImport, org.openide.util.NbBundle.getMessage(ReportPanel.class, "ReportPanel.lbImport.text")); // NOI18N

        javax.swing.GroupLayout columnsPreviewLayout = new javax.swing.GroupLayout(columnsPreview);
        columnsPreview.setLayout(columnsPreviewLayout);
        columnsPreviewLayout.setHorizontalGroup(
            columnsPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
        );
        columnsPreviewLayout.setVerticalGroup(
            columnsPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(columnsPreview);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lbSource)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sourceLabel))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lbImport)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbImporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(statsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(processorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSource)
                    .addComponent(sourceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbImport)
                    .addComponent(cbImporter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(processorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbImporter;
    private javax.swing.JPanel columnsPreview;
    private javax.swing.JPanel importerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbAttr;
    private javax.swing.JLabel lbAttributes;
    private javax.swing.JLabel lbImport;
    private javax.swing.JLabel lbLines;
    private javax.swing.JLabel lbNumLines;
    private javax.swing.JLabel lbSource;
    private javax.swing.JPanel processorPanel;
    private javax.swing.JEditorPane reportEditor;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JPanel statsPanel;
    private javax.swing.JScrollPane tab1ScrollPane;
    private javax.swing.JScrollPane tab2ScrollPane;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    private class IssueTreeModel implements TreeModel {

        private List<Issue> issues;

        public IssueTreeModel(List<Issue> issues) {
            this.issues = issues;
        }

        @Override
        public Object getRoot() {
            return "root";
        }

        @Override
        public Object getChild(Object parent, int index) {
            return issues.get(index);
        }

        @Override
        public int getChildCount(Object parent) {
            return issues.size();
        }

        @Override
        public boolean isLeaf(Object node) {
            return node instanceof Issue;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            return issues.indexOf(child);
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
        }
    }

    private class IssueRowModel implements RowModel {

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueFor(Object node, int column) {
            if (node instanceof Issue) {
                Issue issue = (Issue) node;
                return issue.getLevel().toString();
            }
            return "";
        }

        @Override
        public Class getColumnClass(int column) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
            return false;
        }

        @Override
        public void setValueFor(Object node, int column, Object value) {
        }

        @Override
        public String getColumnName(int column) {
            return NbBundle.getMessage(ReportPanel.class, "ReportPanel.issueTable.issues");
        }
    }

    private class IssueRenderer implements RenderDataProvider {

        @Override
        public String getDisplayName(Object o) {
            Issue issue = (Issue) o;
            return issue.getMessage();
        }

        @Override
        public boolean isHtmlDisplayName(Object o) {
            return false;
        }

        @Override
        public Color getBackground(Object o) {
            return null;
        }

        @Override
        public Color getForeground(Object o) {
            return null;
        }

        @Override
        public String getTooltipText(Object o) {
            return "";
        }

        @Override
        public Icon getIcon(Object o) {
            Issue issue = (Issue) o;
            switch (issue.getLevel()) {
                case INFO:
                    return infoIcon;
                case WARNING:
                    return warningIcon;
                case SEVERE:
                    return severeIcon;
                case CRITICAL:
                    return criticalIcon;
            }
            return null;
        }
    }

}
