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
package org.clueminer.dataset.api;

/**
 * A set of common statistics that could be computed by various providers. Some
 * of them could be computed in quite efficient manner.
 *
 * @see e.g {@link NumericalStats} for an example implementation.
 *
 * @author deric
 */
public enum StatsNum implements Stats {

    MIN,
    MAX,
    RANGE, // values range: max - min
    AVG,
    MEAN, // same as AVG
    VARIANCE,
    SUM,
    SQSUM, //squared sum
    /**
     *
     * This correction (the use of N − 1 instead of N) is known as Bessel's
     * correction. Standard deviation is just square root of variance
     *
     * <math>s = \sqrt{\frac{1}{N-1} \sum_{i=1}^N (x_i -
     * \overline{x})^2},</math>
     *
     * return sample standard deviation
     */
    STD_DEV, //standard deviation
    ABS_DEV, //mean absolute deviation
    STD_SQ, //without correction
    STD_COR, //with correction
    Q1, //first quartile
    Q2, //second quartile
    Q3, //third quartile
    QCD, //the quartile coefficient of dispersion
    MEDIAN,
}
