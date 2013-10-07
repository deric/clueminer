package org.clueminer.dataset.plugin;

import java.util.HashMap;
import javax.swing.event.EventListenerList;
import org.clueminer.dataset.api.ColorGenerator;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.events.DatasetEvent;
import org.clueminer.events.DatasetListener;

/**
 * Until java will have mix-ins we need this sort of code duplication:
 * AbstractDataset and AbstractArrayDataset are pretty much same just one use
 * methods inherited from ArrayList and the other use array storage.
 *
 * @author Tomas Barton
 */
public abstract class AbstractArrayDataset<E extends Instance> implements Dataset<E> {

    private static final long serialVersionUID = 2328076020347060920L;
    transient protected EventListenerList datasetListener;
    protected String id;
    protected String name;
    protected ColorGenerator colorGenerator;
    protected Dataset<? extends Instance> parent = null;
    protected HashMap<String, Dataset<Instance>> children;

    public AbstractArrayDataset() {
        //do nothing
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Dataset<? extends Instance> getParent() {
        return parent;
    }

    @Override
    public void setParent(Dataset<? extends Instance> parent) {
        this.parent = parent;
    }

    @Override
    public void setColorGenerator(ColorGenerator cg) {
        this.colorGenerator = cg;
    }

    protected EventListenerList eventListenerList() {
        if (datasetListener == null) {
            datasetListener = new EventListenerList();
        }
        return datasetListener;
    }

    public void removeDataSetListener(DatasetListener listener) {
        eventListenerList().remove(DatasetListener.class, listener);
    }

    public void fireDatasetOpened(DatasetEvent evt) {
        DatasetListener[] listeners = eventListenerList().getListeners(DatasetListener.class);
        for (DatasetListener listener : listeners) {
            listener.datasetOpened(evt);
        }
    }

    public void addDatasetListener(DatasetListener listener) {
        eventListenerList().add(DatasetListener.class, listener);
    }

    @Override
    public double[][] arrayCopy() {
        double[][] res = new double[this.size()][attributeCount()];
        int cols = this.attributeCount();
        if (cols <= 0) {
            throw new ArrayIndexOutOfBoundsException("given dataset has width " + cols);
        }
        for (int i = 0; i < this.size(); i++) {
            Instance inst = instance(i);
            for (int j = 0; j < inst.size(); j++) {
                res[i][j] = inst.value(j);///scaleToRange((float)inst.value(j), min, max, -10, 10);
            }
        }
        return res;
    }
    
    @Override
    public void addChild(String key, Dataset<Instance> dataset){
        if(children == null){
           children = new HashMap<String, Dataset<Instance>>(5);
        }
        children.put(key, dataset);
    }
    
    @Override
    public Dataset<Instance> getChild(String key){
        if(children == null){
            return null;
        }
        return children.get(key);
    }
}
