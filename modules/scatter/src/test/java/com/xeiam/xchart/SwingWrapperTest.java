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
package com.xeiam.xchart;

import java.util.Random;
import javax.swing.SwingUtilities;

/**
 *
 * @author deric
 */
public class SwingWrapperTest {

    public SwingWrapperTest() {
    }

    private static double[] randomData(Random rand, int size) {
        double[] res = new double[size];
        for (int i = 0; i < res.length; i++) {
            res[i] = rand.nextDouble();
        }
        return res;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Chart chart = new Chart(800, 600);
                int size = 100;
                Random rand = new Random();
                chart.addSeries("test", randomData(rand, size), randomData(rand, size));
                SwingWrapper sw = new SwingWrapper(chart);
                sw.displayChart();
            }
        });
    }

}
