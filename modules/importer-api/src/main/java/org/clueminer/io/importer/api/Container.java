package org.clueminer.io.importer.api;

import java.util.HashSet;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.DataType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.openide.filesystems.FileObject;

/**
 * A container is created each time data are imported by <b>importers</b>. Its
 * role is to host all data collected by importers during import process. After
 * pushing data in the container, its content can be analyzed to verify its
 * validity and then be processed by <b>processors</b>. Thus containers are
 * <b>loaded</b> by importers and <b>unloaded</b> by processors.
 * <p>
 * See {@link ContainerLoader} for how to push data and attributes in the
 * container.
 *
 * @author Tomas Barton
 */
public interface Container<E extends InstanceDraft> {

    /**
     * Set the source of the data put in the container. Could be a file name.
     *
     * @param source the original source of data.
     * @throws NullPointerException if <code>source</code> is <code>null</code>
     */
    void setSource(String source);

    /**
     * If exists, returns the source of the data.
     *
     * @return the source of the data, or <code>null</code> if source is not
     *         defined.
     */
    String getSource();

    /**
     * Sets source as a File
     *
     * @param source
     */
    void setFile(FileObject source);

    FileObject getFile();

    /**
     * Set a report this container can use to report issues detected when
     * loading the container. Report are used to log info and issues during
     * import process. Only one report can be associated to a container.
     *
     * @param report set <code>report</code> as the default report for this
     *               container
     * @throws NullPointerException if <code>report</code> is <code>null</code>
     */
    void setReport(Report report);

    /**
     * Returns the report associated to this container, if exists.
     *
     * @return the report set for this container or <code>null</code> if no
     *         report is defined
     */
    Report getReport();

    /**
     * Close the current loading and clean content before unloading.
     */
    void closeLoader();

    /**
     * This method must be called after the loading is complete and before
     * unloading. Its aim is to verify data consistency as a whole.
     *
     * @return <code>true</code> if container data is * * * consistent,
     *         <code>false</code> otherwise
     */
    boolean verify();

    /**
     *
     * @return MD5 fingerprint
     */
    String getMD5();

    /**
     * Computed fingerprint for this file
     *
     * @param md5
     */
    void setMD5(String md5);

    /**
     * Set name for this dataset
     *
     * @param name
     */
    void setName(String name);

    /**
     *
     * @return the name of loaded dataset
     */
    String getName();

    /**
     * Number of parsed data lines (instances)
     *
     * @return
     */
    int getInstanceCount();

    Iterable<E> getInstances();

    /**
     * Return instance by index
     *
     * @param index
     * @return
     */
    E getInstance(int index);

    /**
     * Return number of detected attributes in parsed file
     *
     * @return
     */
    int getAttributeCount();

    /**
     * Create attribute draft with given name
     *
     * @param index - position in future dataset
     * @param name  - unique name
     * @return
     */
    AttributeDraft createAttribute(int index, String name);

    /**
     * Return attribute at given index
     *
     * @param index
     * @return
     */
    AttributeDraft getAttribute(int index);

    /**
     * Check whether attribute with given name already exists
     *
     * @param key
     * @return
     */
    boolean hasAttribute(String key);

    /**
     * Check if we have attribute on that index
     *
     * @param index
     * @return
     */
    boolean hasAttributeAtIndex(int index);

    /**
     * Basically we check for unique ID column
     *
     * @return true when any of attributes could be a primary key
     */
    boolean hasPrimaryKey();

    /**
     *
     * @return attribute drafts
     */
    Iterable<AttributeDraft> getAttrIter();

    /**
     * Adds new Instance draft
     *
     * @param instance
     * @param row      number of row (or other hint like PK)
     */
    void addInstance(E instance, int row);

    /**
     * Parse columns into types supported by storage backend.
     *
     * @param num     index of line or primary key
     * @param columns
     */
    void createInstance(int num, Object[] columns);

    AttributeBuilder getAttributeBuilder();

    void setAttributeBuilder(AttributeBuilder builder);

    void setDataset(Dataset<? extends Instance> dataset);

    /**
     * Loaded dataset
     *
     * @return
     */
    Dataset<? extends Instance> getDataset();

    /**
     * Number of lines with data
     *
     * @param count
     */
    void setNumberOfLines(int count);

    /**
     * Return number of readable lines in file
     *
     * @return
     */
    int getNumberOfLines();

    /**
     * Default type for all numeric attributes
     *
     * @return
     */
    Object getDefaultNumericType();

    /**
     * Sets default type for all numeric attributes
     *
     * @param type
     */
    void setDefaultNumericType(Class<?> type);

    /**
     * Fetches attribute by a key
     *
     * @param key
     * @param typeClass
     * @return
     */
    AttributeDraft getAttribute(String key, Class typeClass);

    /**
     * Set required data type (imported dataset has special requirement). Could
     * be used for optimization of inner data structure representation
     *
     * @param dataType
     */
    void setDataType(DataType dataType);

    /**
     * Data type is usually either discrete or continuous
     *
     * @return type of data
     */
    DataType getDataType();

    /**
     * Should clear already pre-loaded instances
     */
    void reset();

    /**
     * Will remove all attributes (should be triggered when number of attributes
     * changes)
     */
    void resetAttributes();

    /**
     * List of strings which are considered as missing values
     *
     * @return
     */
    HashSet<String> getMissing();

    void setMissing(HashSet<String> missing);
}
