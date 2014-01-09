package org.clueminer.attributes;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeType;

/**
 *
 * @author Tomas Barton
 */
public class AttributeFactoryImpl implements AttributeBuilder {

    /**
     * Creates a simple single attribute depending on the given value type.
     *
     * @param name
     * @param type
     * @return
     */
    @Override
    public Attribute create(String name, AttributeType type) {
        switch ((BasicAttrType) type) {
            case NUMERICAL:
            case NUMERIC:
            case INTEGER:
            case REAL: //right now it's handled the very same way
                return new NumericalAttribute(name);
            default:
                throw new RuntimeException("attribute type " + type + " is not supported");
        }
    }

    @Override
    public Attribute create(String name, String type) {
        return create(name, BasicAttrType.valueOf(type));
    }
}
