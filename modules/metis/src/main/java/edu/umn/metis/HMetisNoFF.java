/*
 * The hMETIS package is copyrighted by the Regents of the University of
 * Minnesota. It is meant to be used solely for educational, research, and
 * benchmarking purposes by non-profit institutions and US government agencies
 * only. Other organizations are allowed to use hMETIS for evaluation purposes
 * only. The software may not be sold or redistributed. One may make copies of
 * the software for their use provided that the copies, are not sold or
 * distributed, are used under the same terms and conditions. As unestablished
 * research software, this code is provided on an ``as is'' basis without
 * warranty of any kind, either expressed or implied. The downloading, or
 * executing any part of this software constitutes an implicit agreement to
 * these terms. These terms and conditions are subject to change at any time
 * without prior notice.
 */
package edu.umn.metis;

import java.util.ArrayList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.utils.Props;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = Partitioning.class)
public class HMetisNoFF extends HMetis implements Partitioning {

    private static final String name = "hMETIS";

    @Override
    public String getName() {
        return name;
    }

    /**
     * Directly return hMetis result without flood fill
     *
     * @param maxPartitionSize
     * @param g
     * @return
     */
    @Override
    public ArrayList<ArrayList<Node>> partition(int maxPartitionSize, Graph g, Props params) {
        int k = (int) Math.ceil(g.getNodeCount() / (double) maxPartitionSize);
        if (k == 1) {
            ArrayList<ArrayList<Node>> nodes = new ArrayList<>();
            nodes.add((ArrayList<Node>) g.getNodes().toCollection());
            return nodes;
        }
        Node[] nodeMapping = createMapping(g);
        String path = runMetis(g, k, params);
        return importMetisResult(path, k, nodeMapping);
    }

}
