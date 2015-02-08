package org.clueminer.explorer;

import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.clustering.api.dendrogram.DendrogramVisualizationListener;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dgram.vis.DGramVis;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.utils.Props;
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
public class ClusteringNode extends AbstractNode implements DendrogramVisualizationListener {

    private Image image;
    DendrogramMapping mapping;
    private boolean rendering = false;
    private static final Logger logger = Logger.getLogger(ClusteringNode.class.getName());
    private final ReentrantLock lock = new ReentrantLock();

    public ClusteringNode(Clustering<Cluster> clusters) {
        super(Children.LEAF, Lookups.singleton(clusters));
        String name = generateName();
        setDisplayName(name);
        setName(name);
    }

    /**
     * Generate thumbnail of clustering
     *
     * @param type
     * @return
     */
    @Override
    public Image getIcon(int type) {
        if (image == null && rendering == false) {
            lock.lock();
            try {
                Clustering clustering = getClustering();
                //ensure that for each clustering we submit exatctly one task
                rendering = true;
                //image should be updated asynchronously when image is generated
                DGramVis.generate(clustering, 64, 64, this);
                //image is rendering, wait for it...
                return DGramVis.loading();
            } finally {
                lock.unlock();
            }
        }
        if (image == null) {
            return DGramVis.loading();
        }
        return image;
    }

    public Clustering<? extends Cluster> getClustering() {
        return getLookup().lookup(Clustering.class);
    }

    @Override
    public PasteType getDropType(Transferable t, final int action, int index) {
        final Node dropNode = NodeTransfer.node(t, DnDConstants.ACTION_COPY_OR_MOVE + NodeTransfer.CLIPBOARD_CUT);
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
        Clustering<? extends Cluster> clustering = getClustering();
        if (clustering != null) {
            String name = clustering.getName();
            if (name.length() > 10) {
                return name.substring(0, 7) + "...|" + clustering.size() + "|";
            }
            return name;
        }
        return "(missing)";
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
        Clustering<? extends Cluster> clustering = getClustering();
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

    protected EvaluationTable evaluationTable(Clustering<? extends Cluster> clustering) {
        EvaluationTable evalTable = clustering.getEvaluationTable();
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset == null) {
                logger.warning("no dataset in lookup");
            }
            evalTable = new HashEvaluationTable(clustering, dataset);
            clustering.setEvaluationTable(evalTable);
        }
        return evalTable;
    }

    private void internalSheet(Clustering<? extends Cluster> clustering, Sheet sheet) {
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

    private void externalSheet(Clustering<? extends Cluster> clustering, Sheet sheet) {
        Sheet.Set set = new Sheet.Set();
        EvaluationTable evalTable = evaluationTable(clustering);
        set.setName("External Evaluation");
        set.setDisplayName("External Evaluation");
        for (final Entry<String, Double> score : evalTable.getExternal().entrySet()) {
            Property evalProp = new EvaluatorProperty(score.getKey(), score.getValue());
            evalProp.setDisplayName(score.getKey());
            set.put(evalProp);
        }

        sheet.put(set);
    }

    private void algorithmSheet(Clustering<? extends Cluster> clustering, Sheet sheet) {
        final Props params = clustering.getParams();
        if (params == null) {
            return;
        }
        Sheet.Set set = new Sheet.Set();
        set.setName("Algorithm");
        set.setDisplayName("Algorithm");
        for (final String key : params.keySet()) {
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
    }

    @Override
    public void clusteringFinished(Clustering<? extends Cluster> clustering) {
        //not much to do
    }

    @Override
    public void previewUpdated(Image preview) {
        this.image = preview;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireIconChange();
            }
        });
    }
}
