package org.clueminer.attributes;

import java.util.Iterator;
import org.clueminer.dataset.api.AbstractAttribute;
import org.clueminer.dataset.api.NominalMapping;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.stats.NumericalStats;

/**
 *
 * @author Tomas Barton
 */
public class MetaNumericAttribute extends AbstractAttribute {

    public MetaNumericAttribute(String name) {
        super(name, BasicAttrType.NUMERICAL);
        registerStatistics(new NumericalStats(this));
    }

    private MetaNumericAttribute(MetaNumericAttribute attr) {
        super(attr);
    }

    @Override
    public Object clone() {
        return new MetaNumericAttribute(this);
    }

    @Override
    public void updateStatistics(Object value) {
        double val = (Double) value;
        for (Statistics s : statistics) {
            s.valueAdded(val);
        }
    }

    @Override
    public NominalMapping getMapping() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMapping(NominalMapping nominalMapping) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNominal() {
        return false;
    }

    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public Iterator<? extends Object> values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        return dataset.size();
    }

    @Override
    public String asString(double value, int digits, boolean quoteNominal) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double[] asDoubleArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
