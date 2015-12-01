package org.clueminer.explorer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AgglParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.vis.ImageFactory;
import org.clueminer.eval.external.Precision;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.UpdateFeed;
import org.clueminer.evolution.api.UpdateFeedFactory;
import org.clueminer.explorer.gui.ExplorerToolbar;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.IconView;
import org.openide.nodes.AbstractNode;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 *
 * @param <E>
 * @param <C>
 */
@ConvertAsProperties(
        dtd = "//org.clueminer.explorer//Explorer//EN",
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
public final class ExplorerTopComponent<E extends Instance, C extends Cluster<E>> extends CloneableTopComponent implements ExplorerManager.Provider, LookupListener, TaskListener, ToolbarListener<E> {

    private static final long serialVersionUID = 5542932858488609860L;
    private final transient ExplorerManager mgr = new ExplorerManager();
    private Lookup.Result<Clustering> result = null;
    private AbstractNode root;
    private Dataset<E> dataset;
    private static final RequestProcessor RP = new RequestProcessor("Evolution", 100, false, true);
    private volatile RequestProcessor.Task task;
    private static final Logger logger = Logger.getLogger(ExplorerTopComponent.class.getName());
    private ExplorerToolbar toolbar;
    private IconView iconView;
    private ClustComparator comparator;
    private ClustSorted children;
    private Evolution alg;
    //private final Executor exec = new ClusteringExecutor();
    private final Executor exec = new ClusteringExecutorCached();

    public ExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_ExplorerTopComponent());
        setToolTipText(Bundle.HINT_ExplorerTopComponent());
        init();

        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));

        //maybe we want IconView
        //explorerPane.setViewportView(new BeanTreeView());
        //explorerPane.setViewportView(new IconView());
//root = new AbstractNode(new ClusteringChildren());
        //root.setDisplayName("Clustering Evolution");
        //explorerManager.setRootContext(root);
//        mgr.setRootContext(new AbstractNode(Children.create(factory, true)));
    }

    private void init() {
        iconView = new IconView();
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        add(iconView, c);

        toolbar = new ExplorerToolbar();
        toolbar.setListener(this);
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(toolbar, c);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Clustering.class);

        comparator = new ClustComparator(new Precision());
        children = new ClustSorted();
        children.setComparator(comparator);

        //alg.addEvolutionListener(this);
        //children = Children.create(new MyChildFactory(myModels), true);
        root = new AbstractNode(children);
        root.setDisplayName("root node");
        mgr.setRootContext(root);

        //result.addLookupListener(this);
        //resultChanged(new LookupEvent(result));
        //ClustGlobal children = new ClustGlobal(result);
    /*    comparator = new ClustComparator(new AICScore());
         ClustSorted children = new ClustSorted(result);
         children.setComparator(comparator);
         root = new AbstractNode(children);

         root.setDisplayName("root node");
         mgr.setRootContext(root);*/
    }

    @Override
    public void componentClosed() {
        if (result != null) {
            result.removeLookupListener(this);
        }
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
        return mgr;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        /*  Collection<? extends Clustering> allClusterings = result.allInstances();
         ClusteringNode node;
         for (Clustering c : allClusterings) {
         //System.out.println("clustring size" + c.size());
         //System.out.println(c.toString());
         //root = new ClusteringNode(c);
         logger.log(Level.INFO, "created node in top component {0}", c.size());
         node = new ClusteringNode(c);
         //
         }
         mgr.setRootContext(root);*/
    }

    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void taskFinished(Task task) {
        logger.log(Level.INFO, "evolution finished");
        if (!task.isFinished()) {
            logger.warning("task should have been already finished");
        }
        toolbar.evolutionFinished();
        //shutdown image workers
        ImageFactory.getInstance().shutdown();
    }

    @Override
    public void evolutionAlgorithmChanged(ActionEvent evt) {
        //
    }

    @Override
    public void startEvolution(ActionEvent evt, final Evolution alg) {
        if (dataset != null) {
            //start evolution
            if (alg != null) {
                toolbar.evolutionStarted();
                alg.setDataset(dataset);
                if (alg instanceof EvolutionSO) {
                    EvolutionSO evoSo = (EvolutionSO) alg;
                    if (evoSo.getEvaluator() == null) {
                        InternalEvaluatorFactory<E, C> ief = InternalEvaluatorFactory.getInstance();
                        evoSo.setEvaluator(ief.getDefault());
                    }
                }

                final ProgressHandle ph = ProgressHandleFactory.createHandle("Evolution", new Cancellable() {

                    @Override
                    public boolean cancel() {
                        return handleCancel();
                    }
                });
                alg.setProgressHandle(ph);
                alg.addEvolutionListener(children);
                UpdateFeedFactory uf = UpdateFeedFactory.getInstance();
                //TODO: we could optionally disable storing data in meta-database
                UpdateFeed feed = uf.getDefault();
                if (feed != null) {
                    alg.addUpdateListener(feed);
                }
                //childern node will get all clustering results
                //ClusteringChildren children = new ClusteringChildren(alg);
                logger.log(Level.INFO, "starting evolution...");
                task = RP.create(alg);
                task.addTaskListener(this);
                task.schedule(0);
            }
        }

    }

    private boolean handleCancel() {
        logger.info("Evolution task was canceled");
        toolbar.evolutionFinished();
        return true;
    }

    @Override
    public void evaluatorChanged(ClusterEvaluation eval) {
        //TODO implement
        if (comparator != null) {
            comparator.setEvaluator(eval);
            comparator.setAscOrder(!eval.isMaximized());
            children.setComparator(comparator);
        }
    }

    @Override
    public void runClustering(final ClusteringAlgorithm alg, final Dataset<E> data,final Props props) {
        logger.log(Level.INFO, "starting clustering {0}", alg.getName());
        if (data == null) {
            throw new RuntimeException("missing dataset");
        }
        task = RP.create(new Runnable() {

            @Override
            public void run() {
                logger.log(Level.INFO, "clustering {0} [{1}x{2}]",
                        new Object[]{data.getName(), data.size(), data.attributeCount()});
                exec.setAlgorithm(alg);
                Clustering<E, C> clustering;
                ClusteringType ct = ClusteringType.parse(props.get(AgglParams.CLUSTERING_TYPE, "ROWS_CLUSTERING"));
                if (ct == ClusteringType.BOTH) {
                    DendrogramMapping mapping = exec.clusterAll(data, props);
                    clustering = mapping.getRowsClustering();
                    children.addClustering(clustering);
                } else {
                    clustering = exec.clusterRows(data, props);
                    children.addClustering(clustering);
                }
                logger.log(Level.INFO, "finished clustering dataset {1} with algorithm {0}",
                        new Object[]{alg.getName(), data.getName()});
            }
        });
        task.schedule(0);
    }

    @Override
    public Evolution currentEvolution() {
        return alg;
    }

    @Override
    public void clearAll() {
        children.clearAll();
    }

    @Override
    public Dataset<E> getDataset() {
        return dataset;
    }

}
