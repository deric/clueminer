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
 * Implementing class must initialize <code>inputs</code> and <code>outputs</code>
 * in the constructor (or override appropriate getters).
 *
 * @author deric
 */
public abstract class AbsFlowNode implements FlowNode {

    protected Props props;
    protected FlowPanel panel;
    protected Class[] inputs;
    protected Class[] outputs;

    @Override
    public Props getProps() {
        if (props == null && panel != null) {
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

    @Override
    public Object[] getInputs() {
        return inputs;
    }

    @Override
    public Object[] getOutputs() {
        return outputs;
    }

    protected void checkInputs(Object[] in) {
        if (in.length != inputs.length) {
            throw new RuntimeException("expected " + inputs.length + " input(s), got " + in.length);
        }
        //type check
        int i = 0;
        for (Object obj : in) {
            if (!inputs[i].isInstance(obj)) {
                throw new RuntimeException("expected " + inputs[i].toString() + " input(s), got " + obj.getClass().toString());
            }
            i++;
        }
    }

}
