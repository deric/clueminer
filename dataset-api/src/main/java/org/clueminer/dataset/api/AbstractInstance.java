package org.clueminer.dataset.api;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 * @param <E>
 */
public abstract class AbstractInstance<E extends Number> implements Instance<E>, Serializable, Cloneable {

    private static final long serialVersionUID = -6423623520646880380L;
    protected String name;
    protected String id;
    protected Object classValue;
    /**
     * color might be part of GUI extension package, however is heavily used
     * when plotting and keeping same colors through all charts is quite
     * important
     */
    protected Color color;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId(){
        return this.id;
    }

    @Override
    public void setId(String id){
        this.id = id;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color c) {
        color = c;
    }

    @Override
    public Object classValue() {
        return classValue;
    }

    @Override
    public final void setClassValue(Object obj) {
        this.classValue = obj;
    }
}
