package org.clueminer.distance.api;

/**
 *
 * @author Tomas Barton
 */
public abstract class SymmetricDistance extends AbstractDistance implements DistanceMeasure {

    private static final long serialVersionUID = -7841432480140668046L;
    
    @Override
    public boolean isSymmetric(){
        return true;
    }
}
