package org.clueminer.export.newick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.dataset.api.Attribute;
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
    private ProgressHandle ph;
    private Dataset<? extends Instance> dataset;
    private boolean includeNodeNames;
    private boolean exportRows = true;
    private String label = "index";
    private int cnt;

    public NewickExportRunner() {
    }

    public NewickExportRunner(File file, DendroViewer analysis, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.analysis = analysis;
        this.ph = ph;
        parsePref(pref);
    }

    private void parsePref(Preferences p) {
        includeNodeNames = p.getBoolean(NewickOptions.INNER_NODES_NAMES, false);
        exportRows = p.getBoolean(NewickOptions.EXPORT_ROWS, true);
        label = p.get(NewickOptions.NODE_LABEL, "index");
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            HierarchicalResult hres;
            if (exportRows) {
                hres = analysis.getDendrogramMapping().getRowsResult();
            } else {
                hres = analysis.getDendrogramMapping().getColsResult();
            }
            String newick = doExport(hres);
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
            sb.append(getLabel(node)).append(":").append(node.getHeight());
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

    private String getLabel(DendroNode node) {
        if (exportRows) {
            Instance inst = dataset.get(node.getIndex());
            switch (label) {
                case "index":
                    return String.valueOf(inst.getIndex());
                case "name":
                    return inst.getName();
                case "ID":
                    return inst.getId();
                case "class":
                    return (String) inst.classValue();
            }
        } else {
            Attribute attr = dataset.getAttribute(node.getIndex());
            switch (label) {
                case "index":
                    return String.valueOf(attr.getIndex());
                case "name":
                    return attr.getName();
            }
        }

        return String.valueOf(node.getId());
    }

}
