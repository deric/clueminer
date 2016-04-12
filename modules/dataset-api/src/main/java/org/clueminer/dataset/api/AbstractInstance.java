/*
 * Copyright (C) 2011-2016 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.dataset.api;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Tomas Barton
 * @param <T>
 * @param <E>
 */
public abstract class AbstractInstance<T extends Number> implements Instance<T>, Serializable, Cloneable {

    private static final long serialVersionUID = -6423623520646880380L;
    protected String name;
    protected String id;
    protected Object classValue;
    protected int index = -1;
    protected Dataset<? extends AbstractInstance> parent;
    /**
     * color might be part of GUI extension package, however is heavily used
     * when plotting and keeping same colors through all charts is quite
     * important
     */
    protected Color color;

    @Override
    public String getName() {
        if (name == null && classValue != null) {
            return classValue.toString();
        }
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
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
        if (getParent() != null) {
            //notify parent
            getParent().changedClass(classValue, obj, this);
        }
        this.classValue = obj;
    }

    /**
     * @{@inheritDoc }
     * @param dataset
     */
    @Override
    public void setParent(Dataset dataset) {
        this.parent = dataset;
    }

    /**
     * The dataset where this instance belong
     *
     * @return original dataset
     */
    @Override
    public Dataset<? extends Instance> getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     * {@inheritDoc}
     *
     * @param index
     */
    @Override
    public void setIndex(int index) {
        this.index = index;
    }

}
