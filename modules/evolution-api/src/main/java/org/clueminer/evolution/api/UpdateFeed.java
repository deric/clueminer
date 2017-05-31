/*
 * Copyright (C) 2011-2017 clueminer.org
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
package org.clueminer.evolution.api;

import java.util.EventListener;

/**
 *
 * @author Tomas Barton
 */
public interface UpdateFeed<I extends Individual> extends EventListener {

    /**
     * Name should uniquely identify the type of storage/processing engine
     *
     * @return unique name of the provider
     */
    String getName();

    /**
     * Triggered when evolution starts
     *
     * @param evolution
     * @return ID of this run
     */
    int started(Evolution evolution);

    /**
     * Triggered when individual added to population
     *
     * @param runId
     * @param individual
     */
    void individualCreated(int runId, I individual);

}
