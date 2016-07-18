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
package org.clueminer.clustering.params;

import org.clueminer.clustering.api.config.Parameter;

/**
 * Parameter properties that are typically set from annotations, e.g. Param:
 * {@link org.clueminer.clustering.api.config.annotation.Param}.
 *
 * See {@link org.clueminer.clustering.api.Algorithm}
 *
 * @author Tomas Barton
 * @param <T>
 */
public class AlgParam<T> implements Parameter<T> {

    private final String name;
    private String description;
    private final ParamType type;
    private String factory;
    private double min;
    private double max;
    private boolean required;

    public AlgParam(String name, ParamType type) {
        this.name = name;
        this.type = type;
    }

    public AlgParam(String name, ParamType type, String description, String factory) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.factory = factory;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(T value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getFactory() {
        return factory;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public void setMin(double min) {
        this.min = min;
    }

    @Override
    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean b) {
        this.required = b;
    }

}
