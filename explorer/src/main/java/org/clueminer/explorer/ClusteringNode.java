package org.clueminer.explorer;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.util.List;
import javax.swing.Action;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.openide.actions.NewAction;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringNode extends AbstractNode {
    
    private Clustering<Cluster> clustering;
    
    public ClusteringNode(Clustering<Cluster> clusters) {
        super(new ClusteringChildren(clusters), Lookups.singleton(clusters));
        setDisplayName("cluster " + clusters.size());
        this.clustering = clusters;
    }
    
    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
        final Node dropNode = NodeTransfer.node(t,
                DnDConstants.ACTION_COPY_OR_MOVE + NodeTransfer.CLIPBOARD_CUT);
        if (null != dropNode) {
            /*   final Movie movie = (Movie)dropNode.getLookup().lookup( Movie.class );
             if( null != movie  && !this.equals( dropNode.getParentNode() )) {
             return new PasteType() {
             public Transferable paste() throws IOException {
             getChildren().add( new Node[] { new MovieNode(movie) } );
             if( (action & DnDConstants.ACTION_MOVE) != 0 ) {
             dropNode.getParentNode().getChildren().remove( new Node[] {dropNode} );
             }
             return null;
             }
             };
             }*/
        }
        return null;
    }
    
    @Override
    public Cookie getCookie(Class clazz) {
        Children ch = getChildren();
        
        if (clazz.isInstance(ch)) {
            return (Cookie) ch;
        }
        
        return super.getCookie(clazz);
    }
    
    @Override
    protected void createPasteTypes(Transferable t, List s) {
        super.createPasteTypes(t, s);
        PasteType paste = getDropType(t, DnDConstants.ACTION_COPY, -1);
        if (null != paste) {
            s.add(paste);
        }
    }
    
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            SystemAction.get(NewAction.class),
            SystemAction.get(PasteAction.class)};
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
}