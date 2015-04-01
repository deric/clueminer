package org.clueminer.processor;

import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractProcessor implements Processor {

    protected Workspace workspace;
    protected ContainerLoader container;

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void setContainer(ContainerLoader container) {
        this.container = container;
    }

    @Override
    public void process() {
        //Workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        if (workspace == null) {
            workspace = pc.newWorkspace(pc.getCurrentProject());
            pc.openWorkspace(workspace);
        }
        if (container.getSource() != null) {
            pc.setSource(workspace, container.getSource());
        }
        run();
    }

    protected abstract void run();

    protected AttributeType getType(Object klass) {
        BasicAttrType type = BasicAttrType.NUMERIC;
        if (klass instanceof String) {
            type = BasicAttrType.STRING;
        }
        return type;
    }

}
