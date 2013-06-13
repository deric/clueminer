package org.clueminer.hts.api;

import java.util.Collection;
import java.util.Set;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Timeseries;

/**
 * High-throughput screening plate
 *
 * Plates for screening have rectagonal shape, typically we have plates with
 * 96, 384 or 1536 wells
 *
 * @author Tomas Barton
 */
public interface HtsPlate<E extends HtsInstance> extends Timeseries<E>, Dataset<E>, Collection<E>, Set<E> {

    /**
     * Plate unique identification in database
     *
     * @return unique ID
     */
    @Override
    public String getId();

    /**
     *
     * @return number of rows
     */
    public int getRowsCount();

    /**
     * Columns are usually marked with alphabetic letters, typically we have 12,
     * 24 or 32 columns
     *
     * @return number of columns
     */
    public int getColumnsCount();
}
