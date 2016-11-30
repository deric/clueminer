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
package org.clueminer.flow.api;

import org.clueminer.utils.Props;

/**
 * Basic element of data processing pipeline.
 *
 * @author deric
 */
public interface FlowNode {

    String getName();

    Object[] getInputs();

    Object[] getOutputs();

    /**
     * Execute node's operation
     *
     * @param inputs array of input data
     * @param props  key-value configuration
     * @return
     */
    Object[] execute(Object[] inputs, Props props);
}
