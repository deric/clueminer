package org.clueminer.attributes;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.IAttributeType;
import org.clueminer.exception.UnsupportedAttributeType;

/**
 *
 * @author Tomas Barton
 */
public class AttributeFactoryImpl implements AttributeBuilder {

    /**
     * Creates a simple single attribute depending on the given value type.
     */
    @Override
    public Attribute create(String name, IAttributeType type) {    
            switch((AttributeType)type){
                case NUMERICAL:
                case NUMERIC:
                case INTEGER:
                case REAL: //right now it's handled the very same way
                    return new NumericalAttribute(name);
                default:
                    throw new RuntimeException("attribute type "+type+" is not supported");
            }
    }

    @Override
    public Attribute create(String name, String type) throws UnsupportedAttributeType {
        return create(name, AttributeType.valueOf(type));
    }
}
