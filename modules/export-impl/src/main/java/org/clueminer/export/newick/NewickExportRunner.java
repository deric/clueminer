package org.clueminer.export.newick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class NewickExportRunner implements Runnable {

    private File file;
    private ClusterAnalysis analysis;
    private Preferences pref;
    private ProgressHandle ph;

    public NewickExportRunner() {
    }

    public NewickExportRunner(File file, ClusterAnalysis analysis, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.analysis = analysis;
        this.pref = pref;
        this.ph = ph;
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            String newick = doExport(analysis.getResult());
            fw.write(newick);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public String doExport(HierarchicalResult result) {
        StringBuilder sb = new StringBuilder();
        DendroTreeData tree = result.getTreeData();
        DendroNode node = tree.getRoot();
        Dataset<? extends Instance> dataset = result.getDataset();
        Instance inst;
        Stack<DendroNode> stack = new Stack<>();

        while (!stack.isEmpty() || node != null) {
            if (node != null) {
                stack.push(node);
                node = node.getLeft();
                if (node != null && !node.isLeaf()) {
                    sb.append("(");
                }
            } else {
                node = stack.pop();
                if (node.isLeaf()) {
                    inst = dataset.get(node.getIndex());
                    sb.append(inst.getName()).append(":").append(node.getHeight());
                    //System.out.println((i - 1) + " -> " + mapping[(i - 1)]);
                }
                if (!node.isLeaf()) {
                    sb.append(")");
                }

                node = node.getRight();
                if (node != null) {
                    sb.append(",");
                }
            }
        }
        sb.append(";");

        return sb.toString();
    }

}
