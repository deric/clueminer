/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.evolution.eval;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.BaseIndividual;
import org.clueminer.evolution.api.EvolutionSO;
import org.clueminer.evolution.api.Individual;
import org.clueminer.utils.Props;

/**
 *
 * @author deric
 */
public class ObjectiveIndividual<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>>
        extends BaseIndividual<I, E, C> implements Individual<I, E, C> {

    protected double fitness = 0;
    protected static Random rand = new Random();
    protected Clustering<E, C> clustering;
    protected EvalGenom genom;
    private static final Logger logger = Logger.getLogger(ObjectiveIndividual.class.getName());

    public ObjectiveIndividual() {

    }

    public ObjectiveIndividual(EvolutionSO<I, E, C> evolution) {
        this.evolution = evolution;
        this.algorithm = evolution.getAlgorithm();
        init();
    }

    private void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<E, C> getClustering() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double countFitness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getFitness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mutate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<I> cross(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCompatible(Individual other) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public I duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public I deepCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Props getProps() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Clustering<E, C> updateCustering() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
