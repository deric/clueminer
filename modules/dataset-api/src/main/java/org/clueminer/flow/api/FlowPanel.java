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
package org.clueminer.flow.api;

import javax.swing.JPanel;
import org.clueminer.utils.Props;

/**
 * UI for FlowNode configuration
 *
 * @author deric
 */
public interface FlowPanel {

    /**
     * Parameters configured by user (or default ones)
     *
     * @return
     */
    Props getParams();

    /**
     * GUI which will be embedded into another dialog (should not contain any
     * OK/Cancel buttons)
     *
     * @return
     */
    JPanel getPanel();

}
