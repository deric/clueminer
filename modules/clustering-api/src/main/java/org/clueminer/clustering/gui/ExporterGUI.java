package org.clueminer.clustering.gui;

import java.util.prefs.Preferences;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Tomas Barton
 */
public interface ExporterGUI {

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
     * Perform export
     */
    void export();

}
