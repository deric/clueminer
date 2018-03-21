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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatrixUtils {

    public static double[][] simpleRead2DMatrix(File file) {
        return simpleRead2DMatrix(file, " ");
    }

    public static double[][] simpleRead2DMatrix(File file, String columnDelimiter) {
        List<double[]> rows = new ArrayList<>();

        try (FileReader fr = new FileReader(file); BufferedReader b = new BufferedReader(fr)) {
            String line;
            while ((line = b.readLine()) != null && !line.matches("\\s*")) {
                String[] cols = line.trim().split(columnDelimiter);
                double[] row = new double[cols.length];
                for (int j = 0; j < cols.length; j++) {
                    if (!(cols[j].length() == 0)) {
                        row[j] = Double.parseDouble(cols[j].trim());
                    }
                }
                rows.add(row);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        double[][] array = new double[rows.size()][];
        int currentRow = 0;
        for (double[] ds : rows) {
            array[currentRow++] = ds;
        }

        return array;
    }

    public static String[] simpleReadLines(File file) {
        List<String> rows = new ArrayList<>();

        try (FileReader fr = new FileReader(file);
             BufferedReader b = new BufferedReader(fr)) {
            String line;
            while ((line = b.readLine()) != null) {
                rows.add(line.trim());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        String[] lines = new String[rows.size()];
        int currentRow = 0;
        for (String line : rows) {
            lines[currentRow++] = line;
        }

        return lines;
    }
}
