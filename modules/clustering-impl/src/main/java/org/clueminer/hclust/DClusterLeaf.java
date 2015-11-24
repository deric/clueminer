package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Bruna
 * @param <E>
 */
public class DClusterLeaf<E extends Instance> extends DLeaf {

    private List<E> data;

    public DClusterLeaf(int id, List<E> data) {
        super(id);
        this.data = data;
    }

    @Override
    protected void printNodeValue(OutputStreamWriter out) throws IOException {
        out.write("#" + getId());

        out.write(" - ");
        for (Instance instance : data) {
            out.write(instance.getName() + ", ");
        }

        out.write('\n');
    }

    @Override
    public int getIndex() {
        return data.get(0).getIndex();
    }

    public List<E> getInstances() {
        return data;
    }

    public void setInstances(List<E> data) {
        this.data = data;
    }

    @Override
    public boolean containsCluster() {
        return true;
    }

}
