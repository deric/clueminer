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
package org.clueminer.flow;

import java.util.ArrayList;
import java.util.List;
import org.clueminer.flow.api.FlowNode;
import org.openide.nodes.ChildFactory;

/**
 * Factory is responsible for creating drag&drop objects
 *
 * @author deric
 */
public class FlowNodeFactory<T extends FlowNode> extends ChildFactory<T> {

    List<T> nodes = new ArrayList<>();

    @Override
    protected boolean createKeys(List<T> toPopulate) {
        boolean ret = nodes.addAll(nodes);
        if (ret) {
            refresh(true);
        }
        return ret;
    }

}
