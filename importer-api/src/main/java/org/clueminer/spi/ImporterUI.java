package org.clueminer.spi;

import javax.swing.JPanel;

/**
 * Define importer settings user interface.
 * <p>
 * Declared in the system as services (i.e. singleton), the role of UI classes
 * is to provide user interface to configure importers and remember last used
 * settings if needed.
 * <p>
 * To be recognized by the system, implementations must just add the following annotation:
 * <pre>@ServiceProvider(service=ImporterUI.class)</pre>
 *
 * @author Mathieu Bastian
 * @author Tomas Barton
 *
 * @see Importer
 */
public interface ImporterUI {

    /**
     * Link the UI to the importer and therefore to settings values. This method
     * is called after <code>getPanel()</code> to push settings.
     *
     * @param importer  the importer that settings is to be set
     */
    void setup(Importer importer);

    /**
     * Returns the importer settings panel.
     *
     * @return a settings panel, or <code>null</code>
     */
    JPanel getPanel();

    /**
     * Notify UI the settings panel has been closed and that new values can be
     * written.
     *
     * @param update    <code>true</code> if user clicked OK or <code>false</code>
     *                  if CANCEL.
     */
    void unsetup(boolean update);

    /**
     * Returns the importer display name
     * @return          the importer display name
     */
    String getDisplayName();

    /**
     * Returns <code>true</code> if this UI belongs to the given importer.
     *
     * @param importer  the importer that has to be tested
     * @return          <code>true</code> if the UI is matching with <code>importer</code>,
     *                  <code>false</code> otherwise.
     */
    boolean isUIForImporter(Importer importer);

    /**
     * Adds import listener
     *
     * @param listener
     */
    void addListener(ImportListener listener);

    /**
     * Removes import listener
     *
     * @param listener
     */
    void removeListener(ImportListener listener);
}
