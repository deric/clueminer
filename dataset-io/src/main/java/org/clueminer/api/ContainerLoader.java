package org.clueminer.api;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
/**
 *
 * @author Tomas Barton
 */
public interface ContainerLoader {

    public void setDataset(Dataset<Instance> dataset);

    public Dataset<Instance> getDataset();

    /**
     * Text representation of source
     *
     * @return
     */
    public String getSource();
    
    /* public void addInstance(Instance instance);
    
     public void addAttribute(Attribute attr);*/
}
