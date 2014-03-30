package org.clueminer.types;

import java.io.File;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;

/**
 * ContainerLoader pre-loads data as a preview before final import is executed
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

    /**
     *
     * @param attrCnt
     */
    public void setNumberOfAttributes(int attrCnt);

    /**
     *
     * @return number of detected attributes
     */
    public int getNumberAttributes();

    /**
     * Default type for all numeric attributes
     *
     * @return
     */
    public Object getDefaultNumericType();

    /**
     *
     * @param type
     */
    public void setDefaultNumericType(Object type);

    /* public void addInstance(Instance instance);

     public void addAttribute(Attribute attr);*/
}
