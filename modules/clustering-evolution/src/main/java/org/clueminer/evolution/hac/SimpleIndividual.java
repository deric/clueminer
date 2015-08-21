package org.clueminer.evolution.hac;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.EvaluationTable;
import org.clueminer.dataset.api.Instance;
import org.clueminer.evolution.BaseIndividual;
import org.clueminer.evolution.api.Individual;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 * @param <I>
 * @param <E>
 * @param <C>
 */
public class SimpleIndividual<I extends Individual<I, E, C>, E extends Instance, C extends Cluster<E>> extends BaseIndividual<I, E, C> implements Individual<I, E, C> {

    private final Clustering<E, C> clustering;

    public SimpleIndividual(Clustering<E, C> clustering) {
        this.clustering = clustering;
    }

    @Override
    public Clustering getClustering() {
        return clustering;
    }

    @Override
    public double countFitness() {
        EvaluationTable et = evaluationTable(clustering);
        //this might take a while...
        et.countAll();
        return et.getScore("Precision");
    }

    @Override
    public double getFitness() {
        return clustering.getEvaluationTable().getScore("Precision");
    }

    @Override
    public void mutate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List cross(Individual other) {
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
    public Clustering updateCustering() {
        //nothing to do
        return clustering;
    }

}
