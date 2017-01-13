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
package org.clueminer.graph.fast;

/**
 *
 * @author deric
 */
public class FastGraphConfig {

    public static final int ELEMENT_ID_INDEX = 0;
    public static final int ELEMENT_LABEL_INDEX = 1;

    public static final int EDGESTORE_BLOCK_SIZE = 8192;
    public static final int EDGESTORE_DEFAULT_BLOCKS = 10;
    public static final int EDGESTORE_DEFAULT_TYPE_COUNT = 1;
    public static final int EDGESTORE_DEFAULT_DICTIONARY_SIZE = 500;
    public static final float EDGESTORE_DICTIONARY_LOAD_FACTOR = .7f;

    public final static int NODESTORE_DEFAULT_BLOCKS = 10;
    public static final int NODESTORE_DEFAULT_DICTIONARY_SIZE = 500;
    public final static int NODESTORE_BLOCK_SIZE = 2500;
    public final static float NODESTORE_DICTIONARY_LOAD_FACTOR = .7f;


}
