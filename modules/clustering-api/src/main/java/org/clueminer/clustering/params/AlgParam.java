package org.clueminer.clustering.params;

import org.clueminer.clustering.api.config.Parameter;

/**
 *
 * @author Tomas Barton
 * @param <T>
 */
public class AlgParam<T> implements Parameter<T> {

    private final String name;
    private String description;
    private final ParamType type;

    public AlgParam(String name, ParamType type) {
        this.name = name;
        this.type = type;
    }

    public AlgParam(String name, ParamType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    @Override
    public ParamType getType() {
        return type;
    }

    @Override
    public T getValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(T value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
