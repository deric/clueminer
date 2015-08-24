package org.clueminer.clustering.explorer;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.openide.nodes.AbstractNode;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 * @param <E>
 * @param <C>
 */
public class ClustersNode<E extends Instance, C extends Cluster<E>> extends AbstractNode implements PropertyChangeListener {

    private Clustering<E, C> clusters;

    public ClustersNode(Clustering<E, C> clusters) {
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

        }
    }

    private class GroupAllAction extends AbstractAction {

        private static final long serialVersionUID = -1293855348017563628L;

        public GroupAllAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClustersNode.actions.GroupAll.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {

        }

        @Override
        public boolean isEnabled() {
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
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }
}
