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
package org.clueminer.utils;

import java.text.DecimalFormat;

/**
 * Simple measurement of execution time.
 *
 * @author deric
 */
public class StopWatch {

    private long startTime;
    private long endTime;
    private static final DecimalFormat integerFormat = new DecimalFormat("#,##0.0");
    private static final double BY_SECONDS = 1000000000.0;
    private static final double BY_MILISECONDS = 1000000.0;

    public StopWatch() {
        this.startTime = System.nanoTime();
    }

    public StopWatch(boolean start) {
        if (start) {
            this.startTime = System.nanoTime();
        }
    }

    public void startMeasure() {
        this.startTime = System.nanoTime();
    }

    public void endMeasure() {
        this.endTime = System.nanoTime();
    }

    public long total() {
        return endTime - startTime;
    }

    public int timeInMs() {
        return (int) (total() / BY_MILISECONDS);
    }

    /**
     *
     * @return time in seconds
     */
    public int timeInSec() {
        return (int) (total() / BY_SECONDS);
    }

    public String formatMs() {
        return integerFormat.format(timeInMs());
    }

    public String formatSec() {
        return integerFormat.format(timeInSec());
    }

}
