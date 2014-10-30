package org.clueminer.clustering.gui;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Tomas Barton
 */
public interface ClusteringExport {

    /**
     * Name of exported format
     *
     * @return
     */
    String getName();

    /**
     *
     * @return panel with format specific options
     */
    JPanel getOptions();

    /**
     * Extension of exported format
     *
     * @return
     */
    String getExtension();

    /**
     * Method is called after user confirms dialog with format specific settings
     *
     * @param p
     */
    void updatePreferences(Preferences p);

    /**
     *
     * @return filter for specific format(s)
     */
    FileFilter getFileFilter();

    /**
     * Creates Runnable object for performing the export
     *
     * @param file
     * @param analysis
     * @param pref
     * @param ph
     * @return
     */
    Runnable getRunner(File file, DendroViewer analysis, Preferences pref, final ProgressHandle ph);

    /**
     * Display exporting options
     */
    void showDialog();

    /**
     * Set graphical component displaying clustering
     *
     * @param analysis
     */
    void setViewer(DendroViewer analysis);

    /**
     * Perform export
     */
    void export();
}
