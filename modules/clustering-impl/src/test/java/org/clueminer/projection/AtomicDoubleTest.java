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
package org.clueminer.projection;

import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AtomicDoubleTest {

    @Test
    public void testAtomicIntAddition() {
        double d1 = ThreadLocalRandom.current().nextDouble();
        double d2 = ThreadLocalRandom.current().nextDouble();

        AtomicDouble al = new AtomicDouble();
        al.addAndGet(d1);
        al.addAndGet(d2);

        assertEquals(d1 + d2, al.get(), 0.000000001);
    }

}
