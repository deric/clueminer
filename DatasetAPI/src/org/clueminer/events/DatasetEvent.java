package org.clueminer.events;

import java.util.EventObject;
import org.clueminer.dataset.Dataset;
import org.clueminer.instance.Instance;

/**
 *
 * @author Tomas Barton
 */
public class DatasetEvent extends EventObject {

    private static final long serialVersionUID = -8727159443381190534L;
    public Dataset<Instance> dataset;

    public DatasetEvent(Object source, Dataset<Instance> data) {
        super(source);
        this.dataset = data;
    }

    public Dataset<Instance> getDataset() {
        return dataset;
    }
}
