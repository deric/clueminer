/*
 * Copyright (C) 2011-2018 clueminer.org
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
package org.clueminer.stats;

import org.clueminer.dataset.api.StatsString;
import java.util.HashMap;
import org.clueminer.dataset.api.Statistics;
import org.clueminer.dataset.api.Stats;

/**
 * Basic text related statistics.
 *
 * @author deric
 */
public class StringStats implements Statistics {

    private HashMap<String, Integer> data;

    public StringStats() {
        data = new HashMap<>();
    }

    public StringStats(HashMap<String, Integer> other) {
        data = (HashMap<String, Integer>) other.clone();
    }

    @Override
    public Object clone() {
        return new StringStats(this.data);
    }

    @Override
    public void reset() {
        data = new HashMap<>();
    }

    @Override
    public void valueAdded(Object value) {
        String val = (String) value;
        if (data.containsKey(val)) {
            int cnt = data.get(val);
            data.put(val, ++cnt);
        } else {
            data.put(val, 1);
        }
    }

    @Override
    public void valueRemoved(Object value) {
        String val = (String) value;
        if (data.containsKey(val)) {
            int cnt = data.get(val);
            if (cnt > 1) {
                data.put(val, --cnt);
            } else {
                data.remove(val);
            }
        }
    }

    /**
     * Occurrences counter
     *
     * @param key
     * @return
     */
    public int getValueCnt(String key) {
        if (data.containsKey(key)) {
            return data.get(key);
        }
        return 0;
    }

    @Override
    public Stats[] provides() {
        return StatsString.values();
    }

    @Override
    public double statistics(Stats name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public HashMap<String, Integer> getData() {
        return data;
    }

}
