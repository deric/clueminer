package org.clueminer.processor;

import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.Workspace;
import org.clueminer.types.ContainerLoader;
import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class DefaultProcessor implements Processor {

    @Override
    public void process() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContainer(ContainerLoader container) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(DefaultProcessor.class, "DefaultProcessor.displayName");
    }

}
