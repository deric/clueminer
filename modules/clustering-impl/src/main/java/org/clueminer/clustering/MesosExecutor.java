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
package org.clueminer.clustering;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.Executor;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
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

    @Override
    public HierarchicalResult hclustRows(Dataset<E> dataset, Props params) {
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
