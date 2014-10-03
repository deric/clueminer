package org.clueminer.stats;

import org.clueminer.dataset.api.IStats;

/**
 *
 * @author deric
 */
public enum AttrNumStats implements IStats {

    MIN,
    MAX,
    AVG,
    /**
     * variance
     */
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
     * @return sample standard deviation
     */
    STD_DEV, //standard deviation
    ABS_DEV, //mean absolute deviation
    STD_X, //without correction
}
