/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.fastcommunity.orig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.graph.knn.KNNGraphBuilder;
import org.clueminer.graph.adjacencyList.AdjListGraph;
import org.clueminer.graph.api.Graph;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;

/**
 * A wrapper for execution of original fastcommunity binary.
 *
 * @author deric
 */
public class FastCommunityBin<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements AgglomerativeClustering<E, C> {

    public static final String name = "FastCommunity-bin";

    private final FcLoader loader;

    public FastCommunityBin() {
        loader = new FcLoader();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        KNNGraphBuilder knn = new KNNGraphBuilder();
        Graph g = new AdjListGraph(dataset.size());
        g = knn.getNeighborGraph(dataset, g, props.getInt("k", 20));

        Clustering<E, C> clustering = null;

        long current = System.currentTimeMillis();
        File dataFile = new File("data-" + FcLoader.safeName(dataset.getName()) + "-" + current + ".pairs");
        try {
            loader.exportDataset(dataset, dataFile);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("file: " + dataFile.getAbsolutePath());
        return clustering;
    }

    @Override
    public HierarchicalResult hierarchy(Dataset<E> dataset, Props pref) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLinkageSupported(String linkage) {
        return false;
    }

    protected File getBinary() throws IOException, InterruptedException {
        File f = loader.resource("fastcommunity");
        if (!f.exists()) {
            System.err.println("file " + f.getAbsolutePath() + "does not exist!");
        }
        //make sure metis is executable
        Process p = Runtime.getRuntime().exec("chmod ugo+x " + f.getAbsolutePath());
        p.waitFor();
        loader.readStdout(p);
        loader.readStderr(p);
        return f;
    }

}
