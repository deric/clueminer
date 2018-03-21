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
package org.clueminer.chart.ui;

import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author deric
 */
public class ExportChooser extends JFileChooser {

    /**
     * Creates a new instance and initializes it with an array of
     * {@link IOCapabilities}.
     *
     * @param strict       Determines whether this dialog allows only the file
     *                     formats specified in {@code capabilities}.
     * @param capabilities List of objects describing the file formats that are
     *                     supported by this dialog.
     */
    public ExportChooser(boolean strict, List<IOCapabilities> capabilities) {
        setAcceptAllFileFilterUsed(!strict);
        for (IOCapabilities c : capabilities) {
            addChoosableFileFilter(new DrawableWriterFilter(c));
        }
    }
}
