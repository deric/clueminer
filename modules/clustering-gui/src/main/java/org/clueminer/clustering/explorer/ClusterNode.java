package org.clueminer.clustering.explorer;

import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ClusterNode extends AbstractNode {

    private Cluster cluster;

    public ClusterNode(Cluster cluster) {
        super(new ClusterMembers(cluster), Lookups.singleton(cluster));
        this.cluster = cluster;
        setShortDescription("<html><b>" + cluster.getName() + "</b><br>Elements: " + cluster.size() + "</html>");
    }

    @Override
    public String getHtmlDisplayName() {
        String msg = "<html>" + cluster.getName() + " <font color='AAAAAA'><i>- ";
        if (cluster.size() > 1) {
            msg += NbBundle.getMessage(ClusterNode.class, "ClusterNode.displayName.nodesCount.plural", cluster.size());
        } else {
            msg += NbBundle.getMessage(ClusterNode.class, "ClusterNode.displayName.nodesCount.singular", cluster.size());
        }
        msg += "</i></font></html>";
        return msg;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/clueminer/clustering/resources/cluster.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return ImageUtilities.loadImage("org/clueminer/clustering/resources/cluster-open.png");
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new SelectAction(), new GroupAction(), new UngroupAction()};
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Cluster<Instance> obj = getLookup().lookup(Cluster.class);

        try {

            Property sizeProp = new PropertySupport.Reflection(obj, Integer.class, "size", null);
            sizeProp.setName("size");

            set.put(sizeProp);

        } catch (NoSuchMethodException ex) {
            ErrorManager.getDefault();
        }

        sheet.put(set);
        return sheet;
    }

    private class SelectAction extends AbstractAction {

        private static final long serialVersionUID = -3121987182184999762L;

        public SelectAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClusterNode.actions.Select.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: implement
        }
    }

    private class GroupAction extends AbstractAction {

        private static final long serialVersionUID = -4195578033558470722L;

        public GroupAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClusterNode.actions.Group.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO: implement
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    private class UngroupAction extends AbstractAction {

        private static final long serialVersionUID = -4486880608005854650L;

        public UngroupAction() {
            putValue(NAME, NbBundle.getMessage(ClusterNode.class, "ClusterNode.actions.Ungroup.name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }
}
