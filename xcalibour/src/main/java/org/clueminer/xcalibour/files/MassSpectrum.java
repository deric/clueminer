package org.clueminer.xcalibour.files;

import java.util.ArrayList;
import java.util.Iterator;
import org.clueminer.dataset.api.AbstractTimeInstance;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Plotter;
import org.clueminer.math.Interpolator;

/**
 *
 * @author Tomas Barton
 */
public class MassSpectrum<E extends MassItem> extends AbstractTimeInstance<E> implements Instance, ContinuousInstance, Iterable<E> {

    private static final long serialVersionUID = 974569129848252471L;
    protected ArrayList<MassItem> data;
    private Interpolator interpolator;

    public MassSpectrum(int capacity) {
        data = new ArrayList<MassItem>(capacity);
    }

    @Override
    public E get(int index) {
        return (E) data.get(index);
    }

    @Override
    public String getFullName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void put(MassItem item) {
        checkMinMax(item.getIntensity());
        data.add(item);
    }

    @Override
    public int put(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(int i) {
        data.remove(i);
    }

    @Override
    public double value(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void put(int index, double value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCapacity(int capacity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCapacity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString(String separator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Plotter getPlotter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double valueAt(double x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double valueAt(double x, Interpolator interpolator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void crop(int begin, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void normalize(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContinuousInstance copy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>) data.iterator();
    }
    
    @Override
    public int size(){
        return data.size();
    }
}
