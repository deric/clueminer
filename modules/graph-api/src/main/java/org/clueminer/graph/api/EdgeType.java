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
package org.clueminer.graph.api;

/**
 * Edge (see {@link Edge}) direction between source and target node (see {@link Node}).
 *
 * @author deric
 */
public enum EdgeType {
    NONE(0), /* no direction */
    FORWARD(1),
    BACKWARD(2),
    BOTH(3);

    private final int value;

    private EdgeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
