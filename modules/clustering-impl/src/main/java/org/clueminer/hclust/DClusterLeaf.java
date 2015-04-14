package org.clueminer.hclust;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Bruna
 */
public class DClusterLeaf extends DLeaf {

    private List<Instance> data;

    public DClusterLeaf(int id, List<Instance> data) {
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

    public List<Instance> getInstances() {
        return data;
    }

    public void setInstances(List<Instance> data) {
        this.data = data;
    }

    @Override
    public boolean containsCluster() {
        return true;
    }

}
