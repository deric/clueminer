/*
 * Copyright (C) 2015 clueminer.org
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
package org.clueminer.evolution.mo;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author deric
 */
public class MinMaxComparatorTest {

    private MinMaxComparator subject;

    public MinMaxComparatorTest() {
        this.subject = new MinMaxComparator();
    }

    @Before
    public void setUp() {

    }

    @Test
    public void testCompare() {
        Solution solution1 = mock(Solution.class);
        Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2);
        Solution solution2 = mock(Solution.class);
        Mockito.when(solution2.getNumberOfObjectives()).thenReturn(2);
        Solution solution3 = mock(Solution.class);
        Mockito.when(solution3.getNumberOfObjectives()).thenReturn(2);

        //dominative
        Mockito.when(solution1.getObjective(0)).thenReturn(1.0);
        Mockito.when(solution1.getObjective(1)).thenReturn(3.0);

        //dominative
        Mockito.when(solution2.getObjective(0)).thenReturn(2.0);
        Mockito.when(solution2.getObjective(1)).thenReturn(1.0);

        //non-dominative
        Mockito.when(solution3.getObjective(0)).thenReturn(3.0);
        Mockito.when(solution3.getObjective(1)).thenReturn(4.0);

        //1 and 2 forms Pareto front
        assertEquals(0, subject.compare(solution1, solution2));
        //1 and 2 domninates over 3
        assertEquals(-1, subject.compare(solution1, solution3));
        assertEquals(-1, subject.compare(solution2, solution3));
        //3 does not dominate over 1 or 2
        assertEquals(1, subject.compare(solution3, solution1));
        assertEquals(1, subject.compare(solution3, solution2));
    }

}
