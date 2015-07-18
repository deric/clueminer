package org.clueminer.partitioning.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Partitioning via Metis software. In order to use the class, path to gpmetis
 * software has to be specified in the metisPath variable.
 *
 * @author Tomas Bruna
 */
@ServiceProvider(service = Partitioning.class)
public class Metis implements Partitioning {

    Node[] nodeMapping;
    Graph graph;
    int k;
    String ptype;
    ArrayList<LinkedList<Node>> clusters;
    String metisPath = "/home/tomas/clueminer/modules/partitioning-impl/gpmetis";

    public Metis() {
        this("kway");
    }

    public Metis(String ptype) {
        this.ptype = ptype;
        if (!"kway".equals(ptype) && !"rb".equals(ptype)) {
            throw new InvalidParameterException("Parameter ptype cannot have " + ptype + " value");
        }
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Metis partitioning");
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int maxPartitionSize, Graph g) {
        graph = g;
        k = g.getNodeCount() / maxPartitionSize;
        createMapping();
        runMetis();
        clusters = importMetisResult();
        return clusters;
    }

    private void createMapping() {
        nodeMapping = new Node[graph.getNodeCount()];
        for (Node node : graph.getNodes()) {
            nodeMapping[graph.getIndex(node)] = node;
        }
    }

    private void runMetis() {
        String metis = graph.metisExport(false);
        try (PrintWriter writer = new PrintWriter("inputGraph", "UTF-8")) {
            writer.print(metis);
            writer.close();
            Process p = Runtime.getRuntime().exec(metisPath + " -ptype=" + ptype + " inputGraph " + String.valueOf(k));
            p.waitFor();
            File file = new File("inputGraph");
            file.delete();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private ArrayList<LinkedList<Node>> importMetisResult() {
        ArrayList<LinkedList<Node>> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            result.add(new LinkedList<Node>());
        }
        File file = new File("inputGraph.part." + String.valueOf(k));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                int partitionNumber = Integer.parseInt(line);
                result.get(partitionNumber).add(nodeMapping[i]);
                i++;
            }
            file.delete();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
        if (!"kway".equals(ptype) && !"rb".equals(ptype)) {
            throw new InvalidParameterException("Parameter ptype cannot have " + ptype + " value");
        }
    }

    @Override
    public Graph removeUnusedEdges() {
        Graph g = null;
        try {
            //create instance of same graph storage implementation
            g = graph.getClass().newInstance();
            g.ensureCapacity(graph.getNodeCount());

            for (Node node : graph.getNodes()) {
                g.addNode(node);
            }

            for (LinkedList<Node> cluster : clusters) {
                for (int i = 0; i < cluster.size(); i++) {
                    for (int j = i + 1; j < cluster.size(); j++) {
                        if (graph.isAdjacent(cluster.get(i), cluster.get(j))) {
                            g.addEdge(graph.getEdge(cluster.get(i), cluster.get(j)));
                        }
                    }
                }
            }

        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }

        return g;
    }

}
