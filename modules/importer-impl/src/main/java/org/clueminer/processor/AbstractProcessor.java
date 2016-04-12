package org.clueminer.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.InstanceBuilder;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.Container;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 * Processor is responsible of unloading data from draft objects into objects
 * with efficient representation of input data.
 *
 * @author Tomas Barton
 * @param <D>
 */
public abstract class AbstractProcessor<D extends InstanceDraft, E extends Instance> implements Processor<D> {

    protected Workspace workspace;
    protected Container<D> container;
    protected Dataset<E> dataset;
    private static final Logger logger = Logger.getLogger(AbstractProcessor.class.getName());

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void setContainer(Container<D> container) {
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

    /**
     * User appropriate structure for storing given dataset.
     *
     * @param inputAttr
     * @return
     */
    protected abstract Dataset<E> createDataset(ArrayList<AttributeDraft> inputAttr);

    /**
     * Create attribute definition from drafts
     *
     * @param inputAttr
     * @return
     */
    protected abstract Map<Integer, Integer> attributeMapping(ArrayList<AttributeDraft> inputAttr);

    /**
     * Method which can be run during tests without workspace
     */
    protected void run() {
        logger.log(Level.INFO, "importing dataset");
        //basic numeric dataset

        ArrayList<AttributeDraft> inputAttr = new ArrayList<>(container.getAttributeCount());
        //scan attributes
        int metaCnt = 0;
        for (AttributeDraft attrd : container.getAttrIter()) {
            if (attrd.getRole().equals(BasicAttrRole.INPUT)) {
                inputAttr.add(attrd);
            } else {
                metaCnt++;
            }
        }
        //sort attributes by index
        Collections.sort(inputAttr, new AttributeComparator());

        logger.log(Level.INFO, "found {0} meta attributes, and input attributes {1}", new Object[]{metaCnt, inputAttr.size()});

        dataset = createDataset(inputAttr);

        logger.log(Level.INFO, "allocating space: {0} x {1}", new Object[]{container.getInstanceCount(), inputAttr.size()});

        //set attributes
        Map<Integer, Integer> inputMap = attributeMapping(inputAttr);

        //actual data import
        processInstances(inputMap);

        if (dataset.getName() == null) {
            FileObject f = container.getFile();
            if (f != null && f.getName() != null) {
                dataset.setName(f.getName());
            }

        }
        container.setDataset(dataset);
        //import finished - clean preloaded data
        container.reset();
        container.resetAttributes();
    }

    protected void processInstances(Map<Integer, Integer> inputMap) {
        Instance<? extends Double> inst;
        //create real instances
        int i = 0;
        AttributeDraft attr;
        InstanceBuilder<E> builder = dataset.builder();
        for (InstanceDraft instd : container.getInstances()) {
            //TODO allocate only numerical attributes
            inst = builder.build(dataset.attributeCount());
            //parent is needed to build a map of classes
            inst.setParent(dataset);
            /**
             * attribute count in container might differ some attributes
             * (class/label/id) are treated specially
             *
             */
            int realIdx;
            for (int j = 0; j < container.getAttributeCount(); j++) {
                //right now we support only double attributes
                // try {
                attr = container.getAttribute(j);
                if (attr.getRole().equals(BasicAttrRole.INPUT)) {
                    if (attr.isNumerical()) {
                        //realIdx = inputMap.get(j);
                        //inst.set(realIdx, (Double) instd.getObject(j));
                        //delegate type conversion to builders
                        builder.set(instd.getObject(j), attr, (E) inst);
                    } else {
                        logger.log(Level.INFO, "skipping setting value {0}, {1}: {2}", new Object[]{j, i, instd.getObject(j)});
                    }
                } else if (attr.getRole().equals(BasicAttrRole.CLASS) || attr.getRole().equals(BasicAttrRole.LABEL)) {
                    inst.setClassValue(instd.getObject(j));
                    inst.setId((String) instd.getObject(j));
                    inst.setName((String) instd.getObject(j));
                    logger.log(Level.FINEST, "setting class {0}: {1}", new Object[]{i, instd.getObject(j)});
                } else if (attr.getRole().equals(BasicAttrRole.ID)) {
                    inst.setId((String) instd.getObject(j));
                    inst.setName((String) instd.getObject(j));
                }

                /* } catch (RuntimeException e) {
                    logger.log(Level.SEVERE, "failed to set value [{0}, {1}] =  {2}, due to {3}", new Object[]{i, j, instd.getObject(j), e.toString()});
                    Exceptions.printStackTrace(e);
                }*/
                //dataset.setAttributeValue(i, j, (Double) instd.getValue(i));
            }
            if (instd.getId() != null) {
                inst.setId(instd.getId());
            }

            if (inst.getName() == null && inst.classValue() != null) {
                inst.setName(inst.classValue().toString());
            }
            dataset.add((E) inst);
            logger.log(Level.ALL, inst.toString());
            i++;
        }
        logger.log(Level.INFO, "loaded {0} instances", i);
    }

    protected AttributeType getType(Object klass) {
        BasicAttrType type = BasicAttrType.NUMERIC;
        if (klass instanceof String) {
            type = BasicAttrType.STRING;
        }
        return type;
    }

}
