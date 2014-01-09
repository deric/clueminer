package org.clueminer.attributes;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeRole;
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
        return create(name, type, BasicAttrRole.INPUT);
    }

    @Override
    public Attribute create(String name, String type) {
        return create(name, BasicAttrType.valueOf(type));
    }

    @Override
    public Attribute create(String name, AttributeType type, AttributeRole role) {
        switch ((BasicAttrType) type) {
            case NUMERICAL:
            case NUMERIC:
            case INTEGER:
            case REAL: //right now it's handled the very same way
                return new NumericalAttribute(name, role);
            default:
                throw new RuntimeException("attribute type " + type + " is not supported");
        }
    }

    @Override
    public Attribute create(String name, String type, String role) {
        return create(name, BasicAttrType.valueOf(type), BasicAttrRole.valueOf(role));
    }
}
