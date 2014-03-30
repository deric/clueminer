package org.clueminer.types;

import java.io.File;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 *
 * @author Tomas Barton
 */
public interface ContainerLoader {

    public void setDataset(Dataset<? extends Instance> dataset);

    public Dataset<? extends Instance> getDataset();

    /**
     * Text representation of source
     *
     * @return
     */
    public String getSource();

    public void setFile(File file);

    public File getFile();

    /**
     * Number of lines with data
     *
     * @param count
     */
    public void setNumberOfLines(int count);

    /**
     * Return number of readable lines in file
     *
     * @return
     */
    public int getNumberOfLines();

    /* public void addInstance(Instance instance);

     public void addAttribute(Attribute attr);*/
}
