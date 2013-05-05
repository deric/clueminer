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

    public BaseCluster(int capacity) {
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
        int attrCount = this.attributeCount();
        if (attrCount == 0) {
            throw new RuntimeException("number of attributes should not be 0");
        }
        double[] tmpOut = new double[attrCount];
        for (int i = 0; i < attrCount; i++) {
            double sum = 0;
            for (int j = 0; j < this.size(); j++) {
                sum += get(j).value(i);
            }
            tmpOut[i] = sum / (double) this.size();

        }
        Instance avg = this.builder().create(tmpOut);
        return (E) avg;
    }

    @Override
    public int countMutualElements(Cluster c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
