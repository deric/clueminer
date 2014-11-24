package org.clueminer.evolution.hac;

import org.clueminer.clustering.api.evolution.Population;

/**
 *
 * @author Tomas Barton
 */
public class FakePopulation implements Population<BaseIndividual> {

    private BaseIndividual current;

    public BaseIndividual getCurrent() {
        return current;
    }

    public void setCurrent(BaseIndividual current) {
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
    public BaseIndividual getBestIndividual() {
        return current;
    }

    @Override
    public BaseIndividual[] getIndividuals() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIndividuals(BaseIndividual[] individuals) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BaseIndividual getIndividual(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIndividuals(int index, BaseIndividual individual) {
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
