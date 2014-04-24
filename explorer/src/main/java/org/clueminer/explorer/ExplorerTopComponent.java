package org.clueminer.explorer;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import org.clueminer.clustering.algorithm.KMeans;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Evolution;
import org.clueminer.clustering.api.evolution.EvolutionListener;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.clustering.api.evolution.Pair;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evaluation.AICScore;
import org.clueminer.evolution.EvolutionFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.IconView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;
import org.openide.windows.CloneableTopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.clueminer.explorer//Explorer//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ExplorerTopComponent",
        iconBase = "org/clueminer/explorer/evolution16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.clueminer.explorer.ExplorerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ExplorerAction",
        preferredID = "ExplorerTopComponent")
@Messages({
    "CTL_ExplorerAction=Explorer",
    "CTL_ExplorerTopComponent=Explorer Window",
    "HINT_ExplorerTopComponent=This is a Explorer window"
})
public final class ExplorerTopComponent extends CloneableTopComponent implements ExplorerManager.Provider, LookupListener, TaskListener, EvolutionListener {

    private static final long serialVersionUID = 5542932858488609860L;
    private final transient ExplorerManager explorerManager = new ExplorerManager();
    private Lookup.Result<Clustering> result = null;
    private AbstractNode root;
    private ClusteringChildren clustChildren;
    private Dataset<? extends Instance> dataset;
    private static final RequestProcessor RP = new RequestProcessor("Evolution");
    private RequestProcessor.Task task;
    private static final Logger logger = Logger.getLogger(ExplorerTopComponent.class.getName());

    public ExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_ExplorerTopComponent());
        setToolTipText(Bundle.HINT_ExplorerTopComponent());

        associateLookup(ExplorerUtils.createLookup(explorerManager, getActionMap()));

        //maybe we want IconView
        //explorerPane.setViewportView(new BeanTreeView());
        explorerPane.setViewportView(new IconView());

        clustChildren = new ClusteringChildren();

        //root = new AbstractNode(new ClusteringChildren());
        //root.setDisplayName("Clustering Evolution");
        //explorerManager.setRootContext(root);
        explorerManager.setRootContext(new AbstractNode(Children.create(new ClusteringChildFactory(), true)));

    }

    private String[] initEvolution() {
        EvolutionFactory ef = EvolutionFactory.getInstance();
        List<String> list = ef.getProviders();
        System.out.println("evolution providers: " + list.size());
        String[] res = new String[list.size()];
        int i = 0;
        for (String s : list) {
            res[i++] = s;
        }
        return res;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        explorerPane = new javax.swing.JScrollPane();
        jToolBar1 = new javax.swing.JToolBar();
        btnStart = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        comboEvolution = new javax.swing.JComboBox();
        comboEvolution.setModel(new DefaultComboBoxModel(initEvolution()));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jLabel1 = new javax.swing.JLabel();
        sliderGenerations = new javax.swing.JSlider();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(btnStart, org.openide.util.NbBundle.getMessage(ExplorerTopComponent.class, "ExplorerTopComponent.btnStart.text")); // NOI18N
        btnStart.setFocusable(false);
        btnStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        jToolBar1.add(btnStart);
        jToolBar1.add(filler3);

        comboEvolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboEvolutionActionPerformed(evt);
            }
        });
        jToolBar1.add(comboEvolution);
        jToolBar1.add(filler4);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ExplorerTopComponent.class, "ExplorerTopComponent.jLabel1.text")); // NOI18N
        jToolBar1.add(jLabel1);

        sliderGenerations.setMaximum(200);
        sliderGenerations.setMinimum(10);
        sliderGenerations.setValue(10);
        jToolBar1.add(sliderGenerations);
        jToolBar1.add(filler1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(explorerPane, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(explorerPane, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        System.out.println("start button clicked");
        if (dataset != null) {
            //start evolution
            String evolution = (String) comboEvolution.getSelectedItem();
            EvolutionFactory ef = EvolutionFactory.getInstance();
            Evolution alg = ef.getProvider(evolution);
            alg.setDataset(dataset);
            alg.setGenerations(sliderGenerations.getValue());
            alg.setAlgorithm(new KMeans(3, 100));
            alg.setEvaluator(new AICScore());
            alg.addEvolutionListener(this);

            result = alg.getLookup().lookupResult(Clustering.class);
            result.addLookupListener(this);

            logger.log(Level.INFO, "starting evolution...");
            task = RP.create(alg);
            task.addTaskListener(this);
            task.schedule(0);

        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void comboEvolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboEvolutionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboEvolutionActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JComboBox comboEvolution;
    private javax.swing.JScrollPane explorerPane;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JSlider sliderGenerations;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Clustering.class);
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Clustering> allClusterings = result.allInstances();
        System.out.println("lookup got " + allClusterings.size() + " clusterings");
        for (Clustering c : allClusterings) {
            System.out.println("clustring size" + c.size());
            System.out.println(c.toString());
            root = new ClusteringNode(c);
            //
        }
        //explorerManager.setRootContext(root);
    }

    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void taskFinished(Task task) {
        logger.log(Level.INFO, "evolution finished");
    }

    @Override
    public void bestInGeneration(int generationNum, Individual best, double avgFitness, double external) {
        logger.log(Level.INFO, "best in generation, fitness: {0}", avgFitness);
        clustChildren.createNodes(best.getClustering());

    }

    @Override
    public void finalResult(Evolution evolution, int g, Individual best, Pair<Long, Long> time, Pair<Double, Double> bestFitness, Pair<Double, Double> avgFitness, double external) {
        logger.log(Level.INFO, "final result of the evolution, generation: {0} best fitness: {1}", new Object[]{g, bestFitness});
    }
}
