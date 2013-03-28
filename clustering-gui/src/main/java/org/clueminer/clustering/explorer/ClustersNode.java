package org.clueminer.clustering.explorer;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringController;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ClustersNode extends AbstractNode implements PropertyChangeListener {

    private Clustering<Cluster> clusters;

    public ClustersNode(Clustering<Cluster> clusters) {
        super(new ClustersChildren(clusters), Lookups.singleton(clusters));
        this.clusters = clusters;
        setIconBaseWithExtension("org/clueminer/resources/cluster.png");
    }

    @Override
    public String getHtmlDisplayName() {
        String msg = "<html>" + NbBundle.getMessage(ClusterNode.class, "ClustersNode.displayName") + " - <font color='AAAAAA'><i> " + clusters.size() + "</i></font></html>";
        return msg;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new SelectAllAction(), new GroupAllAction(), new UngroupAllAction()};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("date".equals(evt.getPropertyName())) {
            this.fireDisplayNameChange(null, getDisplayName());
        }
    }

    private class SelectAllAction extends AbstractAction {

        private static final long serialVersionUID = 8713314714388683362L;

        public SelectAllAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClustersNode.actions.SelectAll.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClusteringController cc = Lookup.getDefault().lookup(ClusteringController.class);
            for (Cluster cluster : clusters) {
                cc.selectCluster(cluster);
            }
        }
    }

    private class GroupAllAction extends AbstractAction {

        private static final long serialVersionUID = -1293855348017563628L;

        public GroupAllAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClustersNode.actions.GroupAll.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClusteringController cc = Lookup.getDefault().lookup(ClusteringController.class);
            for (Cluster cluster : clusters) {
                if (cc.canGroup(cluster)) {
                    cc.groupCluster(cluster);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            ClusteringController cc = Lookup.getDefault().lookup(ClusteringController.class);
            for (Cluster cluster : clusters) {
                if (cc.canGroup(cluster)) {
                    return true;
                }
            }
            return false;
        }
    }

    private class UngroupAllAction extends AbstractAction {

        private static final long serialVersionUID = 5367069713647184940L;

        public UngroupAllAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClustersNode.actions.UngroupAll.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClusteringController cc = Lookup.getDefault().lookup(ClusteringController.class);
            for (Cluster cluster : clusters) {
                if (cc.canUngroup(cluster)) {
                    cc.ungroupCluster(cluster);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            ClusteringController cc = Lookup.getDefault().lookup(ClusteringController.class);
            for (Cluster cluster : clusters) {
                if (cc.canUngroup(cluster)) {
                    return true;
                }
            }
            return false;
        }
    }
}