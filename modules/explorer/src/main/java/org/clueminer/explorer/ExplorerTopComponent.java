/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.explorer;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Collection;
import org.clueminer.clustering.ClusteringExecutorCached;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.colors.ColorBrewer;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.external.NMIsqrt;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.UpdateFeed;
import org.clueminer.evolution.api.UpdateFeedFactory;
import org.clueminer.explorer.gui.ExplorerToolbar;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.utils.Props;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.IconView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@ActionReference(path = "Menu/Window" /* , position = 333 */)
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
    private static final Logger LOG = LoggerFactory.getLogger(ExplorerTopComponent.class);
    private ExplorerToolbar toolbar;
    private IconView iconView;
    private ClustComparator comparator;
    private ClustSorted children;
    private Evolution alg;
    private Project project;
    //private final Executor exec = new ClusteringExecutor();
    private final Executor exec = new ClusteringExecutorCached();
    private ProjectController pc;

    public ExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_ExplorerTopComponent());
        setToolTipText(Bundle.HINT_ExplorerTopComponent());
        init();

        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
        pc = Lookup.getDefault().lookup(ProjectController.class);
        exec.setColorGenerator(new ColorBrewer());

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
        project = pc.getCurrentProject();
        //find only clusterings for given project
        result = project.getLookup().lookupResult(Clustering.class);
        result.addLookupListener(this);

        comparator = new ClustComparator(new NMIsqrt());
        children = new ClustSorted();
        children.setComparator(comparator);

        //alg.addEvolutionListener(this);
        //children = Children.create(new MyChildFactory(myModels), true);
        root = new AbstractNode(children);
        root.setDisplayName("root node");
        mgr.setRootContext(root);
    }

    @Override
    public void componentClosed() {
        if (result != null) {
            result.removeLookupListener(this);
        }
    }

    @Override
    public void componentActivated() {
        pc.setCurrentProject(project);
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
        Collection<? extends Clustering> allClusterings = result.allInstances();
        for (Clustering c : allClusterings) {
            LOG.info("found clustering {}", c.fingerprint());
            children.addUniqueClustering(c);
        }
    }

    public void setDataset(Dataset<E> dataset) {
        this.dataset = dataset;
    }

    @Override
    public void taskFinished(Task task) {
        LOG.info("evolution finished");
        if (!task.isFinished()) {
            LOG.warn("task should have been already finished");
        }
        toolbar.evolutionFinished();
        //shutdown image workers
        //ImageFactory.getInstance().shutdown();
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

                final ProgressHandle ph = ProgressHandle.createHandle("Evolution", new Cancellable() {

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
                LOG.info("starting evolution...");
                task = RP.create(alg);
                task.addTaskListener(this);
                task.schedule(0);
            }
        }

    }

    private boolean handleCancel() {
        LOG.info("Evolution task was canceled");
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
    public void runClustering(final ClusteringAlgorithm alg, final Dataset<E> data, final Props props) {
        LOG.info("starting clustering {0}", alg.getName());
        if (data == null) {
            throw new RuntimeException("missing dataset");
        }
        task = RP.create(new Runnable() {

            @Override
            public void run() {
                LOG.info("clustering {} [{}x{}]", data.getName(), data.size(), data.attributeCount());
                exec.setAlgorithm(alg);
                Clustering<E, C> clustering;
                ClusteringType ct = ClusteringType.parse(props.get(AlgParams.CLUSTERING_TYPE, "ROWS_CLUSTERING"));
                if (ct == ClusteringType.BOTH) {
                    DendrogramMapping mapping = exec.clusterAll(data, props);
                    clustering = mapping.getRowsClustering();
                    children.addClustering(clustering);
                } else {
                    clustering = exec.clusterRows(data, props);
                    children.addClustering(clustering);
                }
                LOG.info("finished clustering dataset {} with algorithm {}",
                        data.getName(), alg.getName());
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

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void updateThumbnails(Props prop) {
        if (children != null) {
            LOG.debug("explorer contains {} nodes", children.getNodesCount());
            for (Node n : children.getNodes()) {
                ClusteringNode cn = (ClusteringNode) n;
                LOG.debug("updating clustering {} preview", cn.getName());
                cn.updateIcon(prop);
            }
        }
    }

    @Override
    public void comparatorChanged(ClustComparator compare, ClusterEvaluation[] evals) {
        this.comparator = (ClustComparator) compare;
        //comparator.setEvaluator(eval);
        //comparator.setAscOrder(!eval.isMaximized());
        children.setComparator(comparator);
    }

    @Override
    public ClustComparator getComparator() {
        return this.comparator;
    }

    @Override
    public ClustSorted getSortedClusterings() {
        return this.children;
    }

}
