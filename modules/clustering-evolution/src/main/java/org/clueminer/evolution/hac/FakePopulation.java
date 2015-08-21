package org.clueminer.evolution.hac;

import org.clueminer.evolution.api.Individual;
import org.clueminer.evolution.api.Population;

/**
 *
 * @author Tomas Barton
 * @param <I>
 */
public class FakePopulation<I extends Individual> implements Population<I> {

    private I current;

    public I getCurrent() {
        return current;
    }

    public void setCurrent(I current) {
        this.current = current;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public double getAvgFitness() {
        if (current != null) {
            return current.getFitness();
        }
        return Double.NaN;
    }

    @Override
    public I getBestIndividual() {
        return current;
    }

    @Override
    public I[] getIndividuals() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIndividuals(I[] individuals) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public I getIndividual(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getBestFitness() {
        if (current != null) {
            return current.getFitness();
        }
        return Double.NaN;
    }

    @Override
    public void sortByFitness() {
        //nothing to do
    }

    @Override
    public void setIndividuals(int index, I individual) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
