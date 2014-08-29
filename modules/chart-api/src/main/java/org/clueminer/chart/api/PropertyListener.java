package org.clueminer.chart.api;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Tomas Barton
 */
public interface PropertyListener {

    public void addPropertyChangeListener(PropertyChangeListener pcl);

    public void removePropertyChangeListener(PropertyChangeListener pcl);

    public void fire(String propertyName, Object old, Object nue);

}
