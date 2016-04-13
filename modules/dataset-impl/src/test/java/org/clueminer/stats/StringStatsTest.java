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
package org.clueminer.stats;

import java.util.HashMap;
import org.clueminer.attributes.StringAttribute;
import org.clueminer.dataset.api.Attribute;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class StringStatsTest {

    private StringStats subject;
    private Attribute attr;

    @Before
    public void setUp() {
        attr = new StringAttribute("foo");
        subject = new StringStats();
        attr.registerStatistics(subject);
        for (int i = 0; i < 5; i++) {
            attr.updateStatistics("beer");
        }
    }

    @Test
    public void testClone() {
        StringStats clone = (StringStats) subject.clone();
        clone.valueAdded("beer");
        assertEquals(5, subject.getValueCnt("beer"));
        assertEquals(6, clone.getValueCnt("beer"));
    }

    @Test
    public void testReset() {
        subject.reset();
        assertEquals(0, subject.getValueCnt("beer"));
    }

    @Test
    public void testValueAdded() {
        attr.updateStatistics("wine");
        HashMap<String, Integer> stats = subject.getData();
        assertEquals(5, (int) stats.get("beer"));
        assertEquals(1, (int) stats.get("wine"));
    }

    @Test
    public void testValueRemoved() {
        subject.valueRemoved("beer");
        assertEquals(4, subject.getValueCnt("beer"));
    }

}
