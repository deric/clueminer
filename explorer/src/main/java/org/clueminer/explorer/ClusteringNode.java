package org.clueminer.explorer;

import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Action;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.DgViewer;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.openide.actions.NewAction;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeTransfer;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Tomas Barton
 */
public class ClusteringNode extends AbstractNode {

    public ClusteringNode(Clustering<Cluster> clusters) {
        super(Children.LEAF, Lookups.singleton(clusters));
        String name = generateName();
        setDisplayName(name);
        setName(name);
    }
    /*
     @Override
    public Image getIcon(int type) {
        Image img;


        //return ImageUtilities.loadImage("org/myorg/myeditor/icon.png");
     return null;
     }
*/
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

    private String generateName() {
        Clustering<Cluster> clustering = getLookup().lookup(Clustering.class);
        if (clustering != null) {
            StringBuilder sb = new StringBuilder("(" + clustering.size() + ")");
            sb.append("[");
            int i = 0;
            for (int s : clustering.clusterSizes()) {
                if (i > 0) {
                    sb.append(',');
                }
                sb.append(s);
                i++;
            }
            sb.append("]");
            return sb.toString();
        }
        return "(empty)";
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

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (set == null) {
            set = Sheet.createPropertiesSet();
        }
        Clustering<Cluster> clustering = getLookup().lookup(Clustering.class);
        if (clustering != null) {
            try {
                set.setDisplayName("Clustering (" + clustering.size() + ")");
                Property nameProp = new PropertySupport.Reflection(clustering, String.class, "getName", null);
                nameProp.setName("Name");
                set.put(nameProp);

                Property sizeProp = new PropertySupport.Reflection(clustering, Integer.class, "size", null);
                sizeProp.setName("Size");
                set.put(sizeProp);

            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }

            sheet.put(set);
            algorithmSheet(clustering, sheet);
            internalSheet(clustering, sheet);
            externalSheet(clustering, sheet);
        }
        return sheet;
    }

    private EvaluationTable evaluationTable(Clustering<Cluster> clustering) {
        EvaluationTable evalTable = clustering.getLookup().lookup(EvaluationTable.class);
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            evalTable = new HashEvaluationTable(clustering, clustering.getLookup().lookup(Dataset.class));
            clustering.lookupAdd(evalTable);
        }
        return evalTable;
    }

    private void internalSheet(Clustering<Cluster> clustering, Sheet sheet) {
        Sheet.Set set = new Sheet.Set();
        EvaluationTable evalTable = evaluationTable(clustering);
        set.setName("Internal Evaluation");
        set.setDisplayName("Internal Evaluation");
        for (final Entry<String, Double> score : evalTable.getInternal().entrySet()) {
            Property evalProp = new EvaluatorProperty(score.getKey(), score.getValue());
            set.put(evalProp);
        }
        sheet.put(set);
    }

    private void externalSheet(Clustering<Cluster> clustering, Sheet sheet) {
        Sheet.Set set = new Sheet.Set();
        EvaluationTable evalTable = evaluationTable(clustering);
        set.setName("External Evaluation");
        set.setDisplayName("External Evaluation");
        for (final Entry<String, Double> score : evalTable.getExternal().entrySet()) {
            Property evalProp = new PropertySupport.ReadOnly<Double>(score.getKey(), Double.class, "", "") {
                @Override
                public Double getValue() throws IllegalAccessException, InvocationTargetException {
                    return score.getValue();
                }
            };
            evalProp.setDisplayName(score.getKey());
            set.put(evalProp);
        }

        sheet.put(set);
    }

    private void algorithmSheet(Clustering<Cluster> clustering, Sheet sheet) {
        try {
            final Preferences params = clustering.getParams();
            if (params == null) {
                return;
            }
            Sheet.Set set = new Sheet.Set();
            set.setName("Algorithm");
            set.setDisplayName("Algorithm");
            for (final String key : params.keys()) {
                Property evalProp = new PropertySupport.ReadOnly<String>(key, String.class, "", "") {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return params.get(key, "");
                    }
                };
                evalProp.setDisplayName(key);
                set.put(evalProp);
            }

            sheet.put(set);
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
