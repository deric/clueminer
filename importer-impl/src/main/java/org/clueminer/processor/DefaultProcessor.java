package org.clueminer.processor;

import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.ProjectController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Processor.class)
public class DefaultProcessor extends AbstractProcessor implements Processor {

    private Dataset<? extends Instance> dataset;

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(DefaultProcessor.class, "DefaultProcessor.displayName");
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

        //basic numeric dataset
        dataset = new ArrayDataset(container.getInstanceCount(), container.getAttributeCount());

        //set attributes
        for (AttributeDraft attrd : container.getAttributes()) {
            //dataset.attributeBuilder().create(attrd.getName(), null, null);
        }

        Instance inst;
        //create real instances
        int j = 0;
        for (InstanceDraft instd : container.getInstances()) {
            inst = dataset.builder().create();
            for (int i = 0; i < dataset.attributeCount(); i++) {
                //right now we support only double attributes
                dataset.setAttributeValue(i, j, (Double) instd.getValue(i));
            }
            j++;
        }

    }

}
