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
package org.clueminer.importer.impl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.clueminer.graph.api.EdgeType;
import org.clueminer.io.importer.api.EdgeDraft;
import org.clueminer.io.importer.api.NodeDraft;

/**
 *
 * @author deric
 */
public class EdgeDraftImpl implements EdgeDraft {

    private NodeDraft source;
    private NodeDraft target;
    private double weight;
    private String label;
    private EdgeType direction;
    private Object2ObjectOpenHashMap<String, Object> map;

    public EdgeDraftImpl() {
        map = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public void setSource(NodeDraft node) {
        this.source = node;
    }

    @Override
    public void setTarget(NodeDraft node) {
        this.target = node;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void setDirection(EdgeType type) {
        this.direction = type;
    }

    @Override
    public void setValue(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public String getLabel() {
        return label;
    }

}
