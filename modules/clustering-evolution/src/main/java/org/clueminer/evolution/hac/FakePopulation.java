package org.clueminer.evolution.hac;

import org.clueminer.evolution.api.Population;

/**
 *
 * @author Tomas Barton
 */
public class FakePopulation implements Population<SimpleIndividual> {

    private SimpleIndividual current;

    public SimpleIndividual getCurrent() {
        return current;
    }

    public void setCurrent(SimpleIndividual current) {
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
    public SimpleIndividual getBestIndividual() {
        return current;
    }

    @Override
    public SimpleIndividual[] getIndividuals() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIndividuals(SimpleIndividual[] individuals) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleIndividual getIndividual(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIndividuals(int index, SimpleIndividual individual) {
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

}
