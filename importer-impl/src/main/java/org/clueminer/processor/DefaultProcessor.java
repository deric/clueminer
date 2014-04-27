package org.clueminer.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.attributes.BasicAttrRole;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.InstanceDraft;
import org.clueminer.processor.spi.Processor;
import org.clueminer.project.api.ProjectController;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Tomas Barton
 */
@ServiceProvider(service = Processor.class)
public class DefaultProcessor extends AbstractProcessor implements Processor {

    private Dataset<Instance> dataset;
    private static final Logger logger = Logger.getLogger(DefaultProcessor.class.getName());

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(DefaultProcessor.class, "DefaultProcessor.displayName");
    }

    @Override
    public void process() {
        logger.log(Level.INFO, "importing dataset");
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
        logger.log(Level.INFO, "allocating space: {0} x {1}", new Object[]{container.getInstanceCount(), container.getAttributeCount()});
        dataset = new ArrayDataset(container.getInstanceCount(), container.getAttributeCount());

        ArrayList<AttributeDraft> inputAttr = new ArrayList<AttributeDraft>(container.getAttributeCount());
        //scan attributes
        int metaCnt = 0;
        for (AttributeDraft attrd : container.getAttributes()) {
            if (attrd.getRole().equals(BasicAttrRole.INPUT)) {
                inputAttr.add(attrd);
            } else if (attrd.getRole().equals(BasicAttrRole.META)) {
                metaCnt++;
            }
        }
        //sort attributes by index
        Collections.sort(inputAttr, new AttributeComparator());

        logger.log(Level.INFO, "found {0} meta attributes, and input attributes {1}", new Object[]{metaCnt, inputAttr.size()});

        //set attributes
        int index = 0;
        Map<Integer, Integer> inputMap = new HashMap<Integer, Integer>();
        for (AttributeDraft attrd : inputAttr) {
            //create just input attributes
            Attribute attr = dataset.attributeBuilder().create(attrd.getName(), getType(attrd.getType()), attrd.getRole());
            attr.setIndex(index);
            dataset.setAttribute(index, attr);
            logger.log(Level.INFO, "setting attr {0} at pos {1}", new Object[]{attr.getName(), attr.getIndex()});
            inputMap.put(attrd.getIndex(), index);
            index++;
        }

        Instance<? extends Double> inst;
        //create real instances
        int i = 0;
        AttributeDraft attr;
        for (InstanceDraft instd : container.getInstances()) {
            //TODO allocate only numerical attributes
            inst = dataset.builder().create(dataset.attributeCount());
            /**
             * attribute count in container might differ some attributes
             * (class/label/id) are treated specially
             *
             */
            int realIdx;
            for (int j = 0; j < container.getAttributeCount(); j++) {
                //right now we support only double attributes
                try {
                    attr = container.getAttribute(j);
                    if (attr.getRole().equals(BasicAttrRole.INPUT)) {
                        if (attr.isNumerical()) {
                            realIdx = inputMap.get(j);
                            inst.set(realIdx, (Double) instd.getValue(j));
                        } else {
                            logger.log(Level.INFO, "skipping setting value {0}, {1}: {2}", new Object[]{j, i, instd.getValue(j)});
                        }
                    } else if (attr.getRole().equals(BasicAttrRole.CLASS) || attr.getRole().equals(BasicAttrRole.LABEL)) {
                        inst.setClassValue(instd.getValue(j));
                        inst.setId((String) instd.getValue(j));
                        inst.setName((String) instd.getValue(j));
                    } else if (attr.getRole().equals(BasicAttrRole.ID)) {
                        inst.setId((String) instd.getValue(j));
                        inst.setName((String) instd.getValue(j));
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "failed to set value [{0}, {1}] =  {2}, due to {3}", new Object[]{i, j, instd.getValue(j), e.toString()});
                    Exceptions.printStackTrace(e);
                }
                if (instd.getId() != null) {
                    inst.setId(instd.getId());
                }
                //dataset.setAttributeValue(i, j, (Double) instd.getValue(i));
            }
            dataset.add(inst);
            logger.log(Level.ALL, inst.toString());
            i++;
        }
        logger.log(Level.INFO, "loaded {0} instances", i);
        container.setDataset(dataset);
    }

    private AttributeType getType(Object klass) {
        BasicAttrType type = BasicAttrType.NUMERIC;
        if (klass instanceof String) {
            type = BasicAttrType.STRING;
        }
        return type;
    }

    private class AttributeComparator implements Comparator<AttributeDraft> {

        @Override
        public int compare(AttributeDraft attr1, AttributeDraft attr2) {

            int id1 = attr1.getIndex();
            int id2 = attr2.getIndex();

            if (id1 > id2) {
                return 1;
            } else if (id1 < id2) {
                return -1;
            } else {
                return 0;
            }
        }

    }

}
