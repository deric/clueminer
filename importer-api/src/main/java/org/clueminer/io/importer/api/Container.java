package org.clueminer.io.importer.api;

import java.io.File;
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
public interface Container {

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
    void setFile(File source);

    FileObject getFile();

    /**
     * Get containers loading interface. The <b>loader</b> is used by modules
     * which put data in the container
     *
     * @return the containers loading interface
     */
    ContainerLoader getLoader();

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
}
