package org.clueminer.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.plugin.ArrayDataset;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.processor.spi.Processor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Converts preloaded data into actual dataset structure.
 *
 * @author Tomas Barton
 * @param <E>
 */
@ServiceProvider(service = Processor.class)
public class DefaultProcessor<E extends Instance> extends AbstractProcessor<E> implements Processor<E> {

    private static final Logger LOGGER = Logger.getLogger(DefaultProcessor.class.getName());

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(DefaultProcessor.class, "DefaultProcessor.displayName");
    }

    @Override
    protected Dataset<E> createDataset(ArrayList<AttributeDraft> inputAttr) {
        return new ArrayDataset(container.getInstanceCount(), inputAttr.size());
    }

    @Override
    protected Map<Integer, Integer> attributeMapping(ArrayList<AttributeDraft> inputAttr) {
        //set attributes
        int index = 0;
        Map<Integer, Integer> inputMap = new HashMap<>();

        for (AttributeDraft attrd : inputAttr) {
            //create just input attributes
            Attribute attr = dataset.attributeBuilder().build(attrd.getName(), getType(attrd.getJavaType()), attrd.getRole());
            attr.setIndex(index);
            dataset.setAttribute(index, attr);
            LOGGER.log(Level.INFO, "setting attr {0} at pos {1}", new Object[]{attr.getName(), attr.getIndex()});
            inputMap.put(attrd.getIndex(), index);
            index++;
        }
        return inputMap;
    }

}
