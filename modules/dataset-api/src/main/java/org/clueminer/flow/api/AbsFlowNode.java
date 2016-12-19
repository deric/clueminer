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
 *
 * @author deric
 */
public abstract class AbsFlowNode implements FlowNode {

    protected Props props;
    protected FlowPanel panel;

    public Props getProps() {
        if (props == null) {
            return panel.getParams();
        }
        return props;
    }

    @Override
    public void setProps(Props props) {
        this.props = props;
    }

    @Override
    public FlowPanel getPanel() {
        return panel;
    }


}
