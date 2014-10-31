package org.clueminer.export.newick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 * Export tree to the Newick format, a legal expression might look like this:
 *
 * (,(,),);
 *
 * with labels and distances, e.g.:
 *
 * ((A,B),(C,D));
 *
 * @see http://en.wikipedia.org/wiki/Newick_format
 *
 * @author Tomas Barton
 */
public class NewickExportRunner implements Runnable {

    private File file;
    private DendroViewer analysis;
    private Preferences pref;
    private ProgressHandle ph;
    private Dataset<? extends Instance> dataset;
    private boolean includeNodeNames;
    private int cnt;

    public NewickExportRunner() {
    }

    public NewickExportRunner(File file, DendroViewer analysis, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.analysis = analysis;
        this.pref = pref;
        this.ph = ph;
        parsePref(pref);
    }

    private void parsePref(Preferences p) {
        includeNodeNames = p.getBoolean(NewickOptions.INNER_NODES_NAMES, false);
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            //TODO: allow using columns result
            String newick = doExport(analysis.getDendrogramMapping().getRowsResult());
            fw.write(newick);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public String doExport(HierarchicalResult result) {
        StringBuilder sb = new StringBuilder();
        DendroTreeData tree = result.getTreeData();
        DendroNode node = tree.getRoot();
        dataset = result.getDataset();

        if (ph != null) {
            int todo = 2 * result.getDataset().size() + 1;
            ph.start(todo);
            cnt = 0; //TODO: not thread safe
        }

        postOrder(node, sb, false);
        sb.append(";");

        return sb.toString();
    }

    /**
     * Post-order tree walk
     *
     * @param node
     * @param sb
     * @param isLeft
     */
    private void postOrder(DendroNode node, StringBuilder sb, boolean isLeft) {

        if (node == null) {
            return;
        }
        boolean openBracket = false;

        if (node.getLeft() != null && node.getRight() != null) {
            openBracket = true;
            sb.append("(");
        }
        postOrder(node.getLeft(), sb, true);
        postOrder(node.getRight(), sb, false);

        if (openBracket) {
            sb.append(")");
        } else {
            if (!isLeft) {
                sb.append(",");
            }
        }

        if (node.isLeaf()) {
            Instance inst = dataset.get(node.getId());
            sb.append(inst.getIndex()).append(":").append(node.getHeight());
        } else {
            if (includeNodeNames) {
                sb.append("#").append(node.getId());
            }
            sb.append(":").append(node.getHeight());
        }
        if (ph != null) {
            ph.progress(cnt++);
        }
    }

    public void setIncludeNodeNames(boolean includeNodeNames) {
        this.includeNodeNames = includeNodeNames;
    }

}
