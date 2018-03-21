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
package org.clueminer.properties;

import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.eval.utils.HashEvaluationTable;
import org.clueminer.gui.EvaluatorProperty;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author deric
 */
public class ClusterNode<E extends Instance, C extends Cluster<E>> extends AbstractNode {

    public ClusterNode(Clustering<E, C> clusters) {
        super(Children.LEAF, Lookups.singleton(clusters));
        String name = clusters.getName();
        setDisplayName(name);
        setName(name);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Clustering<E, C> clustering = getClustering();
        if (clustering != null) {
            computeClusterProperties(sheet, clustering);
        }
        return sheet;
    }

    public Clustering<E, C> getClustering() {
        return getLookup().lookup(Clustering.class);
    }

    private void computeClusterProperties(final Sheet sheet, final Clustering<E, C> clustering) {
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (set == null) {
            set = Sheet.createPropertiesSet();
        }
        try {
            set.setDisplayName("Clustering (" + clustering.size() + ")");
            Node.Property nameProp = new PropertySupport.Reflection(clustering, String.class, "getName", null);
            nameProp.setName("Name");
            set.put(nameProp);

            Node.Property sizeProp = new PropertySupport.Reflection(clustering, Integer.class, "size", null);
            sizeProp.setName("Size");
            set.put(sizeProp);

        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        sheet.put(set);
        //algorithmSheet(clustering, sheet);
        internalSheet(clustering, sheet);
        //externalSheet(clustering, sheet);
    }

    private void internalSheet(Clustering<E, C> clustering, Sheet sheet) {
        Sheet.Set set = new Sheet.Set();
        EvaluationTable<E, C> evalTable = evaluationTable(clustering);
        set.setName("Internal Evaluation");
        set.setDisplayName("Internal Evaluation");
        for (final Map.Entry<String, Double> score : evalTable.getInternal().entrySet()) {
            Node.Property evalProp = new EvaluatorProperty(score.getKey(), score.getValue());
            set.put(evalProp);
        }
        sheet.put(set);
    }

    protected EvaluationTable<E, C> evaluationTable(Clustering<E, C> clustering) {
        EvaluationTable<E, C> evalTable = clustering.getEvaluationTable();
        //we try to compute score just once, to eliminate delays
        if (evalTable == null) {
            Dataset<E> dataset = getDataset(clustering);
            evalTable = new HashEvaluationTable<>(clustering, dataset);
            clustering.setEvaluationTable(evalTable);
        }
        return evalTable;
    }

    private Dataset<E> getDataset(Clustering<E, C> clustering) {
        Dataset<E> dataset = clustering.getLookup().lookup(Dataset.class);
        return dataset;
    }

}
