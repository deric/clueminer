/**
 * CLUTO is copyrighted by the Regents of the University of Minnesota. It can be
 * freely used for educational and research purposes by non-profit institutions
 * and US government agencies only. Other organizations are allowed to use CLUTO
 * only for evaluation purposes, and any further uses will require prior
 * approval. The software may not be sold or redistributed without prior
 * approval. One may make copies of the software for their use provided that the
 * copies, are not sold or distributed, are used under the same terms and
 * conditions. As unestablished research software, this code is provided on an
 * "as is'' basis without warranty of any kind, either expressed or implied. The
 * downloading, or executing any part of this software constitutes an implicit
 * agreement to these terms. These terms and conditions are subject to change at
 * any time without prior notice.
 *
 */
package edu.umn.cluto;

import edu.umn.metis.ExtBinHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.clueminer.clustering.api.Algorithm;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringAlgorithm;
import org.clueminer.clustering.struct.BaseCluster;
import org.clueminer.clustering.struct.ClusterList;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
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
public class Cluto<E extends Instance, C extends Cluster<E>> extends Algorithm<E, C> implements ClusteringAlgorithm<E, C> {

    public static final String name = "CLUTO";
    private final ExtBinHelper<E> helper;

    public static final String K = "k";
    public static final String CLMETHOD = "clmethod";
    public static final String SIM = "sim";
    public static final String AGGLOFROM = "agglofrom";
    public static final String CRFUN = "crfun";

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
        if (colorGenerator != null) {
            colorGenerator.reset();
        }
        Clustering<E, C> clustering = null;
        try {
            long current = System.currentTimeMillis();
            File dataFile = new File("data-" + ExtBinHelper.safeName(dataset.getName()) + "-" + current + ".mat");
            try {
                helper.exportDataset(dataset, dataFile);
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            File metisFile = getBinary();
            String result = "result-" + ExtBinHelper.safeName(dataset.getName()) + "-" + current + ".out";
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
            int repeat = 0;
            while (!output.exists() && repeat < 5) {
                System.out.println("waiting for output ... " + repeat);
                helper.readStderr(p);
                //Thread.sleep(500);
                repeat++;
            }
            clustering = parseResult(output, dataset, props);
            output.deleteOnExit();

        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        return clustering;
    }

    protected File getBinary() {
        return helper.resource("vcluster");
    }

    private String getParams(StringBuilder sb, Dataset<E> dataset, Props props, String resFile) {
        sb.append(props.getInt(K, dataset.getClasses().size()));
        param(sb, props, CLMETHOD, "graph");
        param(sb, props, SIM, "dist");
        param(sb, props, AGGLOFROM, "30");
        if (props.containsKey(CRFUN)) {
            sb.append(" -crfun=").append(props.get(CRFUN));
        }
        sb.append(" -clustfile=").append(resFile);
        //6 -clmethod=graph -sim=dist -agglofrom=30
        return sb.toString();
    }

    private void param(StringBuilder sb, Props props, String key, String def) {
        sb.append(" -").append(key).append("=").append(props.get(key, def));
    }

    private Clustering<E, C> parseResult(File result, Dataset<E> dataset, Props props) throws FileNotFoundException, IOException {
        Clustering<E, C> clustering = new ClusterList();
        Cluster<E> noise = new BaseCluster<>(5, dataset.attributeCount());
        noise.setAttributes(dataset.getAttributes());
        int i = 0;
        int cluster;
        try (BufferedReader br = new BufferedReader(new FileReader(result))) {

            String line = br.readLine();
            while (line != null) {
                cluster = Integer.valueOf(line) - 1;
                if (cluster >= 0) {
                    //assign item given by line number to a cluster
                    assign(clustering, dataset, i, cluster);
                } else if (i < dataset.size()) {
                    noise.add(dataset.get(i));
                } else {
                    debug(result);
                    throw new RuntimeException("trying to add non-existing instance #" + i);
                }
                line = br.readLine();
                i++;
            }
        }
        if (!noise.isEmpty()) {
            if (colorGenerator != null) {
                noise.setColor(colorGenerator.next());
            }
            noise.setName(Algorithm.OUTLIER_LABEL);
            clustering.add((C) noise);
        }
        clustering.setParams(props);
        clustering.lookupAdd(dataset);
        return clustering;
    }

    private void debug(File f) throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            int i = 0;
            while (line != null) {
                System.out.println((i++) + ":" + line);
                line = br.readLine();
            }
        }
    }

    private void assign(Clustering<E, C> clustering, Dataset<E> dataset, int idx, int clusterId) {
        C clust;
        if (!clustering.hasAt(clusterId)) {
            clust = clustering.createCluster(clusterId);
            if (colorGenerator != null) {
                clust.setColor(colorGenerator.next());
            }
        } else {
            clust = clustering.get(clusterId);
        }
        if (idx < dataset.size()) {
            clust.add(dataset.get(idx));
        }
    }

}
