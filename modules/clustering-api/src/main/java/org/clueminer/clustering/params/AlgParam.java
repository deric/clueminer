package org.clueminer.clustering.params;

import org.clueminer.clustering.api.config.Parameter;

/**
 *
 * @author Tomas Barton
 */
public class AlgParam implements Parameter {

    private final String name;
    private String description;
    private final Class<?> type;

    public AlgParam(String name, Class<?> type) {
        this.name = name;
        this.type = type;
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
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
