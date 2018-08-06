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
package org.clueminer.meta.engine;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exec.AbstractExecutor;
import org.clueminer.io.arff.ARFFWriter;
import org.clueminer.utils.PropType;
import org.clueminer.utils.Props;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 * @param <C>
 */
public class MesosExecutor<E extends Instance, C extends Cluster<E>> extends AbstractExecutor<E, C> implements Executor<E, C> {

    private String cluster;
    private ColorGenerator cg;
    private static final Logger LOG = LoggerFactory.getLogger(MesosExecutor.class);
    private static final Gson GSON = new Gson();

    public MesosExecutor(String uri) {
        this.cluster = uri;
        checkCluster();
        init();
    }

    private void init() {
        Unirest.setObjectMapper(new ObjectMapper() {

            public <T> T readValue(String s, Class<T> aClass) {
                try {
                    return GSON.fromJson(s, aClass);
                } catch (JsonSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object o) {
                try {
                    return GSON.toJson(o);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void checkCluster() {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(cluster + "/health")
                    .header("accept", "application/json")
                    .asJson();
            int resp = jsonResponse.getStatus();
            if (resp == 200) {
                LOG.info("connected to mesos cluster");
            } else {
                LOG.warn("Mesos cluster returned {}:", resp, jsonResponse.getBody().toString());
            }

        } catch (UnirestException ex) {
            LOG.error("failed to connect to a Mesos cluster: {}", ex.getMessage());
        }
    }

    private File writeTmpDataset(Dataset<E> dataset) throws IOException {
        File file = File.createTempFile("dataset", ".tmp");
        try (ARFFWriter writer = new ARFFWriter(new FileWriter(file))) {
            String[] labels = dataset.getClasses().toArray(new String[0]);
            writer.writeHeader(dataset, labels);
            E inst;
            //this might be relatively expensive, but we keep same order as
            //in case of original dataset (easier to diff)
            for (int i = 0; i < dataset.size(); i++) {
                inst = dataset.get(i);
                writer.write(inst, (String) dataset.classValue(i));
            }
            writer.close();

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return file;
    }

    private UUID uploadDataset(Dataset<E> dataset, Props params) {
        String sha1 = params.get(PropType.RUNTIME, "sha1", null);
        File file;
        // fallback
        if (sha1 == null) {
            try {
                file = writeTmpDataset(dataset);
                sha1 = computeSha1(file);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (NoSuchAlgorithmException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(cluster + "/datasets/find")
                    .header("accept", "application/json")
                    .queryString("sha1", sha1)
                    .asJson();
            int resp = jsonResponse.getStatus();
            if (resp == 200) {
                LOG.info(jsonResponse.getBody().toString());
            } else {
                LOG.warn("Mesos cluster returned {}:", resp, jsonResponse.getBody().toString());
            }

        } catch (UnirestException ex) {
            LOG.error("failed to connect to a Mesos cluster: {}", ex.getMessage());
        }
        return null;
    }

    /**
     * Read the file and calculate the SHA-1 checksum
     *
     * @param file the file to read
     * @return the hex representation of the SHA-1 using uppercase chars
     * @throws FileNotFoundException if the file does not exist, is a directory
     * rather than a regular file, or for some other reason cannot be opened for
     * reading
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException should never happen
     */
    private String computeSha1(File file) throws FileNotFoundException,
            IOException, NoSuchAlgorithmException {

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            return new HexBinaryAdapter().marshal(sha1.digest());
        }
    }

    @Override
    public HierarchicalResult hclustRows(Dataset<E> dataset, Props params) {
        UUID uuid = uploadDataset(dataset, params);

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HierarchicalResult hclustColumns(Dataset<E> dataset, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<E, C> clusterRows(Dataset<E> dataset, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DendrogramMapping clusterAll(Dataset<E> dataset, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setColorGenerator(ColorGenerator cg) {
        this.cg = cg;
    }

    @Override
    public void findCutoff(HierarchicalResult result, Props params) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
