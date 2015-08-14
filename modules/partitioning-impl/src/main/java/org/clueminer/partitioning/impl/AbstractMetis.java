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
package org.clueminer.partitioning.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import org.clueminer.graph.api.Graph;
import org.clueminer.graph.api.Node;
import org.clueminer.partitioning.api.Partitioning;
import static org.clueminer.partitioning.impl.Metis.prefix;
import org.clueminer.resources.ResourceLoader;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public abstract class AbstractMetis implements Partitioning {

    public abstract void runMetis(Graph graph, int k);

    private Node[] createMapping(Graph graph) {
        Node[] nodeMapping = new Node[graph.getNodeCount()];
        for (Node node : graph.getNodes()) {
            nodeMapping[graph.getIndex(node)] = node;
        }
        return nodeMapping;
    }

    @Override
    public ArrayList<LinkedList<Node>> partition(int maxPartitionSize, Graph g) {
        int k = (int) Math.ceil(g.getNodeCount() / (double) maxPartitionSize);
        if (k == 1) {
            ArrayList<LinkedList<Node>> nodes = new ArrayList<>();
            nodes.add(new LinkedList<>(g.getNodes().toCollection()));
            return nodes;
        }
        Node[] nodeMapping = createMapping(g);
        runMetis(g, k);
        ArrayList<LinkedList<Node>> clusters = importMetisResult(k, nodeMapping);
        Graph clusteredGraph = new EdgeRemover().removeEdges(g, clusters);
        FloodFill f = new FloodFill();
        return f.findSubgraphs(clusteredGraph);
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

    protected File loadResource(String resource) {
        File file = null;
        try {
            InputStream input = getClass().getResourceAsStream(resource);
            file = File.createTempFile("metis", ".tmp");
            try (OutputStream out = new FileOutputStream(file)) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
            }
            file.deleteOnExit();
        } catch (IOException ex) {
            System.err.println(ex.toString());
        }
        return file;
    }

    protected ArrayList<LinkedList<Node>> importMetisResult(int k, Node[] nodeMapping) {
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

    protected void readStdout(Process p) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
