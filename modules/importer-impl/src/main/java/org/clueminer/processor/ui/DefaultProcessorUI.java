package org.clueminer.processor.ui;

import javax.swing.JPanel;
import org.clueminer.io.importer.api.Container;
import org.clueminer.processor.DefaultProcessor;
import org.clueminer.processor.spi.Processor;
import org.clueminer.processor.spi.ProcessorUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = ProcessorUI.class)
public class DefaultProcessorUI implements ProcessorUI {

    private DefaultProcessor defaultProcessor;
    private final DefaultProcessorSettings settings = new DefaultProcessorSettings();
    private DefaultProcessorPanel panel;

    @Override
    public void setup(Processor processor) {
        defaultProcessor = (DefaultProcessor) processor;
        settings.load(defaultProcessor);
        //panel.setup(defaultProcessor);
    }

    @Override
    public JPanel getPanel() {
        panel = new DefaultProcessorPanel();
        return DefaultProcessorPanel.createValidationPanel(panel);
    }

    @Override
    public void unsetup() {
        panel.unsetup(defaultProcessor);
        settings.save(defaultProcessor);
        panel = null;
        defaultProcessor = null;
    }

    @Override
    public boolean isUIFoProcessor(Processor processor) {
        return processor instanceof DefaultProcessor;
    }

    @Override
    public boolean isValid(Container container) {
        //TODO validate
        return true;
    }

    private static class DefaultProcessorSettings {

        private void save(DefaultProcessor dynamicProcessor) {

        }

        private void load(DefaultProcessor dynamicProcessor) {

        }
    }
}
