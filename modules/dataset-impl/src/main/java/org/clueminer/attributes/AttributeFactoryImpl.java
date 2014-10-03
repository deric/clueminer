package org.clueminer.attributes;

import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.AttributeBuilder;
import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.dataset.api.AttributeType;
import org.clueminer.dataset.api.Dataset;

/**
 *
 * @author Tomas Barton
 */
public class AttributeFactoryImpl<E> implements AttributeBuilder {

    private Dataset<? extends E> target;

    public AttributeFactoryImpl(Dataset<? extends E> target) {
        this.target = target;
    }

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

    /**
     * Create attribute and add it to the dataset (if target is not null)
     *
     * @param name
     * @param type
     * @param role
     * @return
     */
    @Override
    public Attribute create(String name, AttributeType type, AttributeRole role) {
        Attribute ret = build(name, type, role);
        add(ret);
        return ret;
    }

    private void add(Attribute attr) {
        if (target != null) {
            target.addAttribute(attr);
        }
    }

    @Override
    public Attribute create(String name, String type, String role) {
        return create(name, BasicAttrType.valueOf(type), BasicAttrRole.valueOf(role));
    }

    @Override
    public Attribute build(String name, AttributeType type, AttributeRole role) {
        Attribute ret;
        switch ((BasicAttrType) type) {
            case NUMERICAL:
            case NUMERIC:
            case INTEGER:
            case REAL: //right now it's handled the very same way
                ret = new NumericalAttribute(name, role);
                break;
            default:
                throw new RuntimeException("attribute type " + type + " is not supported");
        }
        return ret;
    }

    @Override
    public Attribute build(String name, String type, String role) {
        return build(name, BasicAttrType.valueOf(type), BasicAttrRole.valueOf(role));
    }

    @Override
    public Attribute build(String name, String type) {
        return build(name, BasicAttrType.valueOf(type), BasicAttrRole.INPUT);
    }
}
