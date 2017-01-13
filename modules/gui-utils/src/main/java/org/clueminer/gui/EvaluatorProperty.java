/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.gui;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author Tomas Barton
 */
public class EvaluatorProperty extends PropertySupport.ReadOnly<String> {

    private String value;


    public EvaluatorProperty(String name, double value) {
        super(name, String.class, name, "");
        this.value = String.format("%1$,.2f", value);
    }

    public EvaluatorProperty(String name, Class<String> type, String displayName, String shortDescription) {
        super(name, type, displayName, shortDescription);
    }

    @Override
    public String getValue() throws IllegalAccessException, InvocationTargetException {
        return value;
    }

}
