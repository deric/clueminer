package org.clueminer.processor.spi;

import javax.swing.JPanel;
import org.clueminer.io.importer.api.Container;

/**
 * Define processor settings user interface. <p> Declared in the system as
 * services (i.e. singleton), the role of UI classes is to provide user
 * interface to configure processors and remember last used settings if needed.
 * User interface for processors are shown when the import report is closed and
 * can access the container before the process started. <p> To be recognized by
 * the system, implementations must just add the following annotation:
 * <pre>@ServiceProvider(service=ProcessorUI.class)</pre>
 *
 *
 * @author Tomas Barton
 */
public interface ProcessorUI {

    /**
     * Link the UI to the processor and therefore to settings values. This
     * method is called after
     * <code>getPanel()</code> to push settings.
     *
     * @param processor the processor that settings is to be set
     */
    public void setup(Processor processor);

    /**
     * Returns the processor settings panel.
     *
     * @return a settings panel, or <code>null</code>
     */
    public JPanel getPanel();

    /**
     * Notify UI the settings panel has been closed and that new values can be
     * written.
     *
     */
    public void unsetup();

    /**
     * Returns
     * <code>true</code> if this UI belongs to the given processor.
     *
     * @param processor the processor that has to be tested
     * @return <code>true</code> if the UI is matching
     * with <code>processor</code>, <code>false</code> otherwise.
     */
    public boolean isUIFoProcessor(Processor processor);

    /**
     * Returns
     * <code>true</code> if the processor this UI represents is valid for the
     * <code>container</code>. Processors could be specific to some type of data
     * and this method can provide this information.
     *
     * @param container the container that is to be processed
     * @return          <code>true</code> if the processor this UI represents is valid
     * for <code>container</code>.
     */
    public boolean isValid(Container container);
}
