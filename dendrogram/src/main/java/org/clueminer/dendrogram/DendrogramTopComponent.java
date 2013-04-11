package org.clueminer.dendrogram;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramTree;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;
import org.clueminer.dendrogram.gui.DendrogramComponent;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.clueminer.dendrogram.gui//Dendrogram//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "DendrogramTopComponent",
        iconBase = "org/clueminer/dendrogram/gui/clustering16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.clueminer.dendrogram.gui.DendrogramTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DendrogramAction",
        preferredID = "DendrogramTopComponent")
@Messages({
    "CTL_DendrogramAction=Dendrogram",
    "CTL_DendrogramTopComponent=Dendrogram Window",
    "HINT_DendrogramTopComponent=This is a Dendrogram window"
})
public final class DendrogramTopComponent extends CloneableTopComponent implements LookupListener, TreeListener, ClusteringListener {

    private static final long serialVersionUID = -7289618311057427489L;
    private static DendrogramTopComponent instance;
    private static final String PREFERRED_ID = "DendrogramTopComponent";
    private DendrogramComponent dendrogram;
    private Project project;
    private Lookup.Result<Dataset> result = null;
    private final InstanceContent content = new InstanceContent();
    private static final Logger logger = Logger.getLogger(DendrogramTopComponent.class.getName());

    public DendrogramTopComponent() {
        initComponents();
        setName(Bundle.CTL_DendrogramTopComponent());
        setToolTipText(Bundle.HINT_DendrogramTopComponent());
        dendrogram = new DendrogramComponent();
        // ProxyLookup proxyLookup = new ProxyLookup(Utilities.actionsGlobalContext(), new AbstractLookup(content));
        associateLookup(new AbstractLookup(content));

        add(dendrogram, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        initContent();
    }

    private void initContent() {
        dendrogram.addRowsTreeListener(this);
        dendrogram.addClusteringListener(this);
        //Workspace events
        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
        projectController.addWorkspaceListener(new WorkspaceListener() {
            @Override
            public void initialize(Workspace workspace) {
            }

            @Override
            public void select(Workspace workspace) {
                System.out.println("workspace selected " + workspace.toString());
            }

            @Override
            public void unselect(Workspace workspace) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void close(Workspace workspace) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void disable() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void projectActivated(Project project) {
                //nothing to do? the event was fired from this class and if not it does not concern us
            }
        });
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
        System.out.println("dendrogram opened");
        result = Utilities.actionsGlobalContext().lookupResult(Dataset.class);
        System.out.println("lookup result " + result);
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        if (result != null) {
            result.removeLookupListener(this);
        }
    }

    /**
     * When component is activated context should be updated and also all
     * dependent components should be updated
     */
    @Override
    protected void componentActivated() {
        try {
            if (project != null) {
                logger.log(Level.INFO, "dendrogram top component activated - {0}", project.getName());
                ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
                logger.log(Level.INFO, "project controller - {0}", pc.toString());
                pc.setCurrentProject(project);
            }

        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }

    @Override
    protected void componentDeactivated() {
        if (project != null) {
            logger.log(Level.INFO, "dendrogram top component deactivated  - {0}", project.getName());
        }
    }

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link #findInstance}.
     */
    public static synchronized DendrogramTopComponent getDefault() {
        if (instance == null) {
            instance = new DendrogramTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the CustomerTopComponent instance. Never call {@link #getDefault}
     * directly!
     */
    public static synchronized DendrogramTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(DendrogramTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof DendrogramTopComponent) {
            return (DendrogramTopComponent) win;
        }
        Logger.getLogger(DendrogramTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
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
    public void resultChanged(LookupEvent ev) {
        System.out.println("dedrogram component lookup event " + ev.toString());
        Collection<? extends Dataset> allDatasets = result.allInstances();
        System.out.println("datasets size= " + allDatasets.size());
    }

    public void setDataset(Dataset<? extends Instance> dataset) {
        this.dendrogram.setDataset(dataset);
    }

    public void setPreprocessedDataset(Dataset<? extends Instance> dataset) {
        this.dendrogram.setApproximated(dataset);
    }

    /**
     * Currently dendrogram is the main view for a opened project. It might
     * change in the future
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
        //Lookup.Result<Dataset> result = project.getSelection().getLookup().lookupAll(Dataset.class);

        if (cluster.firstElem > -1) {
            Dataset<Instance> selected;
            //duplicate method should copy all attributes from original dataset
            selected = (Dataset<Instance>) data.getInstances().duplicate();

            if (selected != null) {
                Dataset<? extends Instance> original = data.getInstances();
                for (int i = cluster.firstElem; i <= cluster.lastElem; i++) {
                    Instance inst = original.instance(data.getRowIndex(i));
                    selected.add(inst);
                }
                content.set(Collections.singleton(selected), null);
            }
        }
    }

    @Override
    public void treeUpdated(DendrogramTree source, int width, int height) {
    }

    @Override
    public void clusteringChanged(Clustering clust) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        //add result to lookup
        pc.getCurrentProject().add(Lookups.singleton(clust));
        content.set(Collections.singleton(clust), null);
    }

    @Override
    public void resultUpdate(HierarchicalResult hclust) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        //add result to lookup
        if (hclust != null) {
            pc.getCurrentProject().add(Lookups.singleton(hclust));
            System.out.println("adding clustering result to lookup");
        }

    }
}
