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
package org.clueminer.meta.redis;

import java.util.Collection;
import java.util.Map;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.api.Evolution;
import org.clueminer.meta.api.CostMeasure;
import org.clueminer.meta.api.CostMeasurement;
import org.clueminer.meta.api.MetaResult;
import org.clueminer.meta.api.MetaStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author deric
 */
public class RedisStore<E extends Instance, C extends Cluster<E>> implements MetaStorage<E, C> {

    private static final String NAME = "Redis";
    private JedisPool pool = null;
    private static final Logger LOG = LoggerFactory.getLogger(RedisStore.class);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void initialize() {
        pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            if (!jedis.exists("algorithms")) {
                LOG.info("Redis is empty");
            }
        }
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.destroy();
        }
    }

    @Override
    public double findScore(Dataset<E> dataset, Clustering<E, C> clustering, ClusterEvaluation<E, C> eval) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<String> getEvolutionaryAlgorithms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int registerRun(Evolution evolution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(Dataset<E> dataset, Clustering<E, C> clustering) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(int runId, Clustering<E, C> clustering) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<MetaResult> findResults(Dataset<E> dataset, ClusterEvaluation<E, C> score, String evolutionaryAlgorithm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<MetaResult> findResults(Dataset<E> dataset, ClusterEvaluation<E, C> score) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertCost(String method, CostMeasure measure, double value, Map<String, Double> parameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<CostMeasurement> findAllCostMeasurements(String method, CostMeasure measure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
