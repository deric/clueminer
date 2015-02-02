package org.clueminer.evolution.hac;

import java.util.List;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.evolution.Individual;
import org.clueminer.evolution.AbstractIndividual;
import org.clueminer.utils.Props;

/**
 *
 * @author Tomas Barton
 */
public class BaseIndividual extends AbstractIndividual implements Individual {

    private final Clustering<? extends Cluster> clustering;

    public BaseIndividual(Clustering<? extends Cluster> clustering) {
        this.clustering = clustering;
    }

    @Override
    public Clustering getClustering() {
        return clustering;
    }

    @Override
    public void countFitness() {
        //
    }

    @Override
    public double getFitness() {
        return Double.NaN;
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
    public Individual duplicate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Individual deepCopy() {
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

}
