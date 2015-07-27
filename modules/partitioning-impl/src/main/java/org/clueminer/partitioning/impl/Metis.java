package org.clueminer.partitioning.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Bisection;
import org.clueminer.partitioning.api.Partitioning;
import org.clueminer.resources.ResourceLoader;
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
    private static final String prefix = "/org/clueminer/partitioning/impl";

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
        return "Metis";
    }

    @Override
    public void setBisection(Bisection bisection) {
        // cannot change in Metis
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int maxPartitionSize, Graph g) {
        graph = g;
        k = g.getNodeCount() / maxPartitionSize;
        createMapping();
        runMetis();
        ArrayList<LinkedList<Node>> clusters = importMetisResult();
        Graph clusteredGraph = new EdgeRemover().removeEdges(graph, clusters);
        FloodFill f = new FloodFill();
        return f.findSubgraphs(clusteredGraph);
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
            File metisFile = resource("gpmetis");
            //make sure metis is executable
            Process p = Runtime.getRuntime().exec("chmod u+x " + metisFile.getAbsolutePath());
            p.waitFor();
            //run metis
            p = Runtime.getRuntime().exec(metisFile.getAbsolutePath() + " -ptype=" + ptype + " inputGraph " + String.valueOf(k));
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

    /**
     * Resource packed in jar is not possible to open directly, this method uses
     * a .tmp file which should be on exit deleted
     *
     * @param path
     * @return
     */
    public File resource(String path) {
        String resource = prefix + File.separatorChar + path;
        File file;
        URL url = Metis.class.getResource(resource);
        if (url == null) {
            //probably on Windows
            Collection<String> res = ResourceLoader.getResources("gpmetis");
            if (res.isEmpty()) {
                throw new RuntimeException("could not find metis binary!");
            }
            String fullPath = res.iterator().next();
            file = new File(fullPath);
            if (file.exists()) {
                return file;
            }
            //non existing URL
            //no classpath, compiled as JAR
            //if path is in form: "jar:path.jar!resource/data"
            int pos = fullPath.lastIndexOf("!");
            if (pos > 0) {
                resource = fullPath.substring(pos + 1);
                if (!resource.startsWith("/")) {
                    //necessary for loading as a stream
                    resource = "/" + resource;
                }
            }
            return loadResource(resource);
        }

        if (url.toString().startsWith("jar:")) {
            return loadResource(resource);
        } else {
            file = new File(url.getFile());
        }
        return file;
    }

    private File loadResource(String resource) {
        File file = null;
        try {
            InputStream input = getClass().getResourceAsStream(resource);
            file = File.createTempFile("metis", ".tmp");
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            file.deleteOnExit();
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
        return file;
    }

}
