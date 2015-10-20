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
package edu.umn.cluto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.clueminer.clustering.api.AbstractClusteringAlgorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.partitioning.impl.ExtBinHelper;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * An ugly wrapper to run CLUTO software.
 *
 * TODO: rewrite to native C calls
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
@ServiceProvider(service = ClusteringAlgorithm.class)
public class Cluto<E extends Instance, C extends Cluster<E>> extends AbstractClusteringAlgorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String name = "CLUTO";
    private final ExtBinHelper<E> helper;

    public static final String K = "k";
    public static final String CLMETHOD = "clmethod";
    public static final String SIM = "sim";
    public static final String AGGLOFROM = "agglofrom";

    private static final String space = " ";

    public Cluto() {
        helper = new ExtBinHelper();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Clustering<E, C> cluster(Dataset<E> dataset, Props props) {
        Clustering<E, C> clustering = null;
        try {
            File dataFile = new File("data.mat");
            try {
                helper.exportDataset(dataset, dataFile);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            File metisFile = getBinary();
            String result = "result.out";
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod u+x " + metisFile.getAbsolutePath());
            p.waitFor();

            //run CLUTO
            StringBuilder sb = new StringBuilder(space);
            sb.append(dataFile.getAbsolutePath()).append(space);
            String params = getParams(sb, dataset, props, result);
            System.out.println("args: " + params);
            p = Runtime.getRuntime().exec(metisFile.getAbsolutePath() + params);
            p.waitFor();
            helper.readStdout(p);
            dataFile.delete();
            dataFile.deleteOnExit();

            File output = new File(result);
            if (output.exists()) {
                clustering = parseResult(output, dataset);
            } else {
                helper.readStderr(p);
                throw new RuntimeException("no output was written");
            }
            output.deleteOnExit();

        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return clustering;
    }

    private File getBinary() {
        return helper.resource("vcluster");
    }

    private String getParams(StringBuilder sb, Dataset<E> dataset, Props props, String resFile) {
        sb.append(props.getInt(K, dataset.getClasses().size()));
        param(sb, props, CLMETHOD, "graph");
        param(sb, props, SIM, "dist");
        param(sb, props, AGGLOFROM, "30");
        param(sb, props, "clustfile", resFile);
        //6 -clmethod=graph -sim=dist -agglofrom=30
        return sb.toString();
    }

    private void param(StringBuilder sb, Props props, String key, String def) {
        sb.append(" -").append(key).append("=").append(props.get(key, def));
    }

    private Clustering<E, C> parseResult(File result, Dataset<E> dataset) throws FileNotFoundException, IOException {
        Clustering<E, C> clustering = new ClusterList();
        try (BufferedReader br = new BufferedReader(new FileReader(result))) {

            String line = br.readLine();
            int i = 0;
            int cluster;
            while (line != null) {
                cluster = Integer.valueOf(line);
                //assign item given by line number to a cluster
                assign(clustering, dataset, i, cluster);
                line = br.readLine();
                i++;
            }
        }
        return clustering;
    }

    private void assign(Clustering<E, C> clustering, Dataset<E> dataset, int idx, int clusterId) {
        C clust;
        if (!clustering.hasAt(clusterId)) {
            clust = clustering.createCluster(clusterId);
        } else {
            clust = clustering.get(clusterId);
        }
        clust.add(dataset.get(idx));
    }

}
