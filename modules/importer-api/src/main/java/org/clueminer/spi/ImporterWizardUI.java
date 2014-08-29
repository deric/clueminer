package org.clueminer.spi;

import org.openide.WizardDescriptor;


/**
 * Define importer settings wizard user interface.
 * <p>
 * Declared in the system as services (i.e. singleton), the role of UI classes
 * is to provide user interface to configure importers and remember last used
 * settings if needed. This service is designed to provide the different panels
 * part of a spigot import wizard.
 * <p>
 * To be recognized by the system, implementations must just add the following annotation:
 * <pre>@ServiceProvider(service=ImporterWizardUI.class)</pre>
 *
 * @author Mathieu Bastian
 * @see SpigotImporter
 */
public interface ImporterWizardUI {

    /**
     * Returns the importer display name
     * @return          the importer display name
     */
    public String getDisplayName();

    /**
     * There are two levels for wizard UIs, the category and then the display name.
     * Returns the importer category.
     * @return          the importer category
     */
    public String getCategory();

    /**
     * Returns the description for this importer
     * @return          the description test
     */
    public String getDescription();

    /**
     * Returns wizard panels.
     * @return          panels of the current importer
     */
    public WizardDescriptor.Panel[] getPanels();

    /**
     * Configure <code>panel</code> with previously remembered settings. This method
     * is called after <code>getPanels()</code> to push settings.
     *
     * @param panel     the panel that settings are to be set
     */
    public void setup(WizardDescriptor.Panel panel);

    /**
     * Notify UI the settings panel has been closed and that new values can be
     * written. Settings can be read in <code>panel</code> and written
     * <code>importer</code>.
     * @param importer  the importer that settings are to be written
     * @param panel     the panel that settings are read
     */
    public void unsetup(Importer importer, WizardDescriptor.Panel panel);

    /**
     * Returns <code>true</code> if this UI belongs to the given importer.
     *
     * @param importer  the importer that has to be tested
     * @return          <code>true</code> if the UI is matching with <code>importer</code>,
     *                  <code>false</code> otherwise.
     */
    public boolean isUIForImporter(Importer importer);
}
