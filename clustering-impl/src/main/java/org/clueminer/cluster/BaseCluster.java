package org.clueminer.cluster;

import java.awt.Color;
import org.clueminer.clustering.api.Cluster;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.SampleDataset;

/**
 *
 * @author Tomas Barton
 */
public class BaseCluster<E extends Instance> extends SampleDataset<E> implements Cluster<E> {

    private static final long serialVersionUID = -6931127664256794410L;
    private int clusterId;
    private Color color;
    
    public BaseCluster(int capacity){
        super(capacity);
    }

    @Override
    public void setClusterId(int id) {
        this.clusterId = id;
    }

    @Override
    public int getClusterId() {
        return clusterId;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public E getCentroid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int countMutualElements(Cluster c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
