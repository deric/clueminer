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
package org.clueminer.evolution.multim;

import java.util.List;
import java.util.Random;
import org.clueminer.clustering.aggl.linkage.CompleteLinkage;
import org.clueminer.clustering.api.AgglomerativeClustering;
import org.clueminer.clustering.api.AlgParams;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.ClusterLinkage;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringType;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.clustering.api.InternalEvaluator;
import org.clueminer.clustering.api.factory.InternalEvaluatorFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.distance.api.Distance;
import org.clueminer.evolution.BaseIndividual;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.utils.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public class MultiMuteIndividual<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends BaseIndividual<I, E, C> implements Individual<I, E, C> {

    protected double fitness = 0;
    protected static Random rand = new Random();
    protected Clustering<E, C> clustering;
    protected Props genom;
    private static final Logger LOG = LoggerFactory.getLogger(MultiMuteIndividual.class);

    public MultiMuteIndividual() {

    }

    public MultiMuteIndividual(EvolutionSO<I, E, C> evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        this.genom = evolution.getDefaultProps();
        init();
    }

    /**
     * Copying constructor
     *
     * @param parent
     */
    public MultiMuteIndividual(MultiMuteIndividual parent) {
        this.evolution = parent.evolution;
        this.algorithm = parent.algorithm;
        this.genom = parent.genom.copy();

        this.fitness = parent.fitness;
    }

    private void init() {
        genom.put(AlgParams.ALG, algorithm.getName());
        genom.putBoolean(AlgParams.LOG, logscale(rand));
        genom.put(AlgParams.STD, std(rand));
        genom.put(AlgParams.CLUSTERING_TYPE, ClusteringType.ROWS_CLUSTERING);
        genom.put(AlgParams.CUTOFF_STRATEGY, "hill-climb inc");
        genom.put(AlgParams.CUTOFF_SCORE, evaluator().getName());
        do {
            genom.put(AlgParams.LINKAGE, linkage(rand));
        } while (!isValid());

        //genom.put(AlgParams.DIST, distance(rand));
        //first we might want to mutate etc, then count fitness
        //countFitness();
    }

    protected boolean logscale(Random rand) {
        return rand.nextBoolean();
    }

    protected String std(Random rand) {
        int size = ((MultiMuteEvolution) evolution).stds.size();
        int i = rand.nextInt(size);
        return (String) ((MultiMuteEvolution) evolution).stds.get(i);
    }

    protected String linkage(Random rand) {
        int size = ((MultiMuteEvolution) evolution).linkage.size();
        int i = rand.nextInt(size);
        ClusterLinkage<E> link = (ClusterLinkage<E>) ((MultiMuteEvolution) evolution).linkage.get(i);
        return link.getName();
    }

    protected String distance(Random rand) {
        int size = ((MultiMuteEvolution) evolution).dist.size();
        int i = rand.nextInt(size);
        Distance dist = (Distance) ((MultiMuteEvolution) evolution).dist.get(i);
        return dist.getName();
    }

    public InternalEvaluator evaluator() {
        InternalEvaluatorFactory ief = InternalEvaluatorFactory.getInstance();
        List<InternalEvaluator> evals = ief.getAll();
        int size = evals.size();
        int i = rand.nextInt(size);
        return evals.get(i);
    }

    @Override
    public Clustering<E, C> getClustering() {
        return clustering;
    }

    @Override
    public double countFitness() {
        if (algorithm instanceof AgglomerativeClustering) {
            AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
            while (!isValid()) {
                genom.put(AlgParams.LINKAGE, linkage(rand));
            }
        }
        clustering = updateCustering();
        if (!isValid()) {
            return Double.NaN;
        }
        EvaluationTable et = evaluationTable(clustering);
        if (et == null) {
            throw new RuntimeException("missing eval table");
        }
        fitness = et.getScore(evolution.getEvaluator());
        return fitness;
    }

    /**
     * Clustering should be updated after each mutation
     *
     * @param eval
     * @return
     */
    public double countFitness(ClusterEvaluation eval) {
        if (clustering == null) {
            updateCustering();
        }
        if (!isValid()) {
            return Double.NaN;
        }
        EvaluationTable<E, C> et = evaluationTable(clustering);
        if (et == null) {
            throw new RuntimeException("missing eval table");
        }
        System.out.println(clustering.getParams().toString());
        return et.getScore(eval);
    }

    /**
     * Some algorithms (like k-means) have random initialization, so we can't
     * reproduce the same results, therefore we have to keep the resulting
     * clustering
     *
     * @return clustering according to current parameters
     */
    @Override
    public Clustering<E, C> updateCustering() {
        LOG.info("starting clustering {}", genom.toString());
        clustering = ((MultiMuteEvolution) evolution).exec.clusterRows(evolution.getDataset(), genom);
        ClusterEvaluation eval = evolution.getExternal();
        if (eval != null) {
            LOG.info("finished clustering, supervised score ({}): {}", eval.getName(), countFitness(eval));
        }
        return clustering;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    /**
     * For tests only
     *
     * @param fitness
     */
    protected void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public void mutate() {
        if (performMutation()) {
            genom.putBoolean(AlgParams.LOG, logscale(rand));
        }
        if (performMutation()) {
            genom.put(AlgParams.STD, std(rand));
        }
        if (performMutation()) {
            genom.put(AlgParams.LINKAGE, linkage(rand));
        }
        //mutating distance is complicated
        /* if (performMutation()) {
         * genom.put(AlgParams.DIST, distance(rand));
         * } */
    }

    @Override
    public List<I> cross(Individual i) {
        throw new UnsupportedOperationException("not supported yet");
    }

    private boolean performMutation() {
        return rand.nextDouble() < evolution.getMutationProbability();
    }

    @Override
    public I deepCopy() {
        I newOne = (I) new MultiMuteIndividual(this);
        return newOne;
    }

    @Override
    public boolean isCompatible(Individual other) {
        return this.getClass() == other.getClass();
    }

    @Override
    public I duplicate() {
        I duplicate = (I) new MultiMuteIndividual(evolution);
        return duplicate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ ");
        sb.append(genom.toString());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean isValid() {
        boolean ret = true;
        if (algorithm instanceof AgglomerativeClustering) {
            AgglomerativeClustering aggl = (AgglomerativeClustering) algorithm;
            ret = ret && aggl.isLinkageSupported(genom.get(AlgParams.LINKAGE, CompleteLinkage.name));
        }
        if (clustering != null) {
            if (clustering.size() < 2) {
                //we don't want solutions with 0 or 1 cluster
                return false;
            }
            Dataset<? extends Instance> dataset = clustering.getLookup().lookup(Dataset.class);
            if (dataset != null) {
                if (clustering.instancesCount() != dataset.size()) {
                    return false;
                }
            }
        }

        return ret;
    }

    @Override
    public Props getProps() {
        return genom;
    }

    public String getGen(String key) {
        return genom.get(key);
    }

    public void setGen(String key, String value) {
        genom.put(key, value);
    }
}
