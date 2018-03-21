/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.export.newick;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.prefs.Preferences;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.clustering.api.dendrogram.DendroTreeData;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
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
    private ProgressHandle ph;
    private Dataset<? extends Instance> dataset;
    private boolean includeNodeNames;
    private boolean exportRows = true;
    private boolean includeRoot = false;
    private String label = "index";
    private int cnt;
    private DecimalFormat df;
    private DendrogramMapping mapping;

    public NewickExportRunner() {
        df = initFormat(3);
    }

    public NewickExportRunner(File file, DendrogramMapping mapping, Preferences pref, ProgressHandle ph) {
        this.file = file;
        this.mapping = mapping;
        this.ph = ph;
        parsePref(pref);
    }

    private void parsePref(Preferences p) {
        includeNodeNames = p.getBoolean(NewickOptions.INNER_NODES_NAMES, false);
        exportRows = p.getBoolean(NewickOptions.EXPORT_ROWS, true);
        label = p.get(NewickOptions.NODE_LABEL, "index");
        includeRoot = p.getBoolean(NewickOptions.INCLUDE_ROOT, false);
        df = initFormat(3);
    }

    private DecimalFormat initFormat(int d) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        return format;
    }

    @Override
    public void run() {
        try (FileWriter fw = new FileWriter(file)) {
            HierarchicalResult hres;
            if (exportRows) {
                hres = mapping.getRowsResult();
            } else {
                hres = mapping.getColsResult();
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

        if (sb.length() > 0) {
            char c = sb.charAt(sb.length() - 1);
            if (c != ')' && c != '(') {
                sb.append(",");
            }
        }

        if (node.getLeft() != null && node.getRight() != null) {
            openBracket = true;
            sb.append("(");
        }
        postOrder(node.getLeft(), sb, true);
        postOrder(node.getRight(), sb, false);

        if (openBracket) {
            sb.append(")");
        }

        if (node.isLeaf()) {
            sb.append(getLabel(node));
        } else {
            if (includeNodeNames) {
                sb.append("#").append(node.getId());
            }
        }
        if (!node.isRoot()) {
            sb.append(":");
            DendroNode parent = node.getParent();
            if (parent != null) {
                sb.append(df.format(parent.getHeight() - node.getHeight()));
            } else {
                if (includeRoot) {
                    sb.append("0.0");
                }
            }
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
