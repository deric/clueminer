package org.clueminer.clustering.explorer;

import javax.swing.JPanel;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author Tomas Barton
 */
public class ClusterExplorer extends JPanel implements ExplorerManager.Provider {

    private static final long serialVersionUID = 5019390510462612214L;
    private BeanTreeView tree;
    private final ExplorerManager manager = new ExplorerManager();

    public ClusterExplorer() {
        initComponents();
    }

    public void initExplorer(Clustering<Cluster> clusters) {
        if (clusters != null) {
            manager.setRootContext(new ClustersNode(clusters));
            ((BeanTreeView) tree).setRootVisible(true);
        } else {
            resetExplorer();
        }
    }

    public void resetExplorer() {
        manager.setRootContext(new AbstractNode(Children.LEAF));
        ((BeanTreeView) tree).setRootVisible(false);
    }

    private void initComponents() {
        tree = new BeanTreeView();

        setLayout(new java.awt.BorderLayout());

        //((BeanTreeView) tree).setRootVisible(false);
        add(tree, java.awt.BorderLayout.CENTER);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }
}
