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
package org.clueminer.io.importer.api;

import org.clueminer.graph.api.EdgeType;

/**
 *
 * @author deric
 */
public interface EdgeDraft {

    String getLabel();

    void setSource(NodeDraft node);

    void setTarget(NodeDraft node);

    void setWeight(double weight);

    void setLabel(String label);

    void setDirection(EdgeType type);

    void setValue(String key, Object value);

}
