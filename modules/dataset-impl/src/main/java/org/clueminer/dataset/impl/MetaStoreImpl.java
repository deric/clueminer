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
package org.clueminer.dataset.impl;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.util.Map;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.MetaStore;

/**
 * Store meta attributes associated with data rows. Meta data are typically used
 * for result evaluation.
 *
 * @author deric
 */
public class MetaStoreImpl implements MetaStore {

    private Table<Integer, Attribute, Object> table;
    private static final Object NULL = null;

    public MetaStoreImpl() {
        table = newTable();
    }

    public static Table<Integer, Attribute, Object> newTable() {
        return Tables.newCustomTable(
                Maps.<Integer, Map<Attribute, Object>>newHashMap(),
                new Supplier<Map<Attribute, Object>>() {
            @Override
                    public Map<Attribute, Object> get() {
                return Maps.newHashMap();
            }
        });
    }

    @Override
    public void set(Attribute attr, int index, Object value) {
        table.put(index, attr, value);
    }

    @Override
    public Object get(Attribute attr, int index) {
        if (table.contains(index, attr)) {
            return table.get(index, attr);
        } else {
            return NULL;
        }
    }

    @Override
    public double getDouble(Attribute attr, int index) {
        if (table.contains(index, attr)) {
            return (double) table.get(index, attr);
        } else {
            return Double.NaN;
        }
    }

    @Override
    public String getString(Attribute attr, int index) {
        if (table.contains(index, attr)) {
            return (String) table.get(index, attr);
        } else {
            return "";
        }
    }

}
