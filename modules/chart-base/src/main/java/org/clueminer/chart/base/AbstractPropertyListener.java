package org.clueminer.chart.base;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractPropertyListener implements Serializable {

    private static final long serialVersionUID = 5564974589758816040L;

    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    public void fire(String propertyName, Object old, Object nue) {
        PropertyChangeListener[] pcls = listeners.toArray(new PropertyChangeListener[0]);
        for (PropertyChangeListener pcl : pcls) {
            pcl.propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }
}
