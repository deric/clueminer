package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import org.clueminer.clustering.api.dendrogram.DendroLeaf;
import org.clueminer.clustering.api.dendrogram.DendroNode;
import org.clueminer.dataset.api.DataVector;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public class DLeaf<T extends DataVector> extends DTreeNode implements DendroLeaf<T>, DendroNode {

    private T data;

    public DLeaf(int id) {
        super(id);
    }

    public DLeaf(int id, DataVector data) {
        super(id);
        this.data = (T) data;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    protected void printNodeValue(OutputStreamWriter out) throws IOException {
        out.write("#" + getId());
        if (data != null) {
            out.write(" - " + data.getName());
        }
        out.write('\n');
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int getIndex() {
        if (data != null) {
            return data.getIndex();
        }
        return -1;
    }

    @Override
    public T getData() {
        return data;
    }

    public boolean containsCluster() {
        return false;
    }

}
