package org.clueminer.importer.impl;

import org.clueminer.dataset.api.AttributeRole;
import org.clueminer.importer.parser.DoubleParser;
import org.clueminer.io.importer.api.AttributeDraft;
import org.clueminer.io.importer.api.AttributeParser;
import org.clueminer.io.importer.api.AttributeParserFactory;

/**
 *
 * @author Tomas Barton
 */
public class AttributeDraftImpl implements AttributeDraft {

    private int index;
    private String name;
    private boolean meta;
    private boolean unique;
    private boolean numerical;
    private Class<?> type;
    private Object defaultValue;
    private AttributeRole role;
    private boolean skipped;

    public AttributeDraftImpl() {

    }

    public AttributeDraftImpl(String name) {
        this.name = name;
    }

    @Override
    public void setType(Class<?> t) {
        this.type = t;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isMeta() {
        return meta;
    }

    @Override
    public void setMeta(boolean b) {
        this.meta = b;
    }

    @Override
    public boolean isNumerical() {
        if (type == Double.class || type.equals(Integer.class) || type.equals(Float.class) || type.equals(Long.class)) {
            return true;
        }
        return numerical;
    }

    @Override
    public void setNumerical(boolean b) {
        this.numerical = b;
        throw new RuntimeException("rather set directly type");
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public void setUnique(boolean b) {
        this.unique = b;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object value) {
        this.defaultValue = value;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public AttributeRole getRole() {
        return role;
    }

    @Override
    public void setRole(AttributeRole role) {
        this.role = role;
    }

    @Override
    public boolean isSkipped() {
        return skipped;
    }

    @Override
    public void setSkipped(boolean b) {
        this.skipped = b;
    }

    @Override
    public AttributeParser getParser() {
        AttributeParserFactory factory = AttributeParserFactory.getInstance();
        String typeName = type.toString();
        String parserName = typeName.substring(typeName.lastIndexOf('.') + 1) + "Parser";
        if (factory.hasProvider(parserName)) {
            return factory.getProvider(parserName);
        }
        /*  Logger.getLogger(AttributeDraftImpl.class.getName()).log(Level.SEVERE,                "attribute type {0} is not supproted yet. parser: ''{1}'' was not found",
         new Object[]{typeName, parserName});*/
        return new DoubleParser();
    }

}
