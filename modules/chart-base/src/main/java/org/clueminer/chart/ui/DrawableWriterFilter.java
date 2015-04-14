/*
 * Copyright (C) 2011-2015 clueminer.org
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

import java.io.File;
import java.text.MessageFormat;
import javax.swing.filechooser.FileFilter;
import org.openide.util.NbBundle;

/**
 * File filter that extracts files that can be read with a certain set of
 * {@link IOCapabilities}.
 */
public class DrawableWriterFilter extends FileFilter {

    /**
     * Capabilities that describe the data formats that can be processed by this
     * filter.
     */
    private final IOCapabilities capabilities;

    /**
     * Creates a new instance and initializes it with an
     * {@link de.erichseifert.gral.io.IOCapabilities} object.
     *
     * @param capabilities writer capabilities.
     */
    public DrawableWriterFilter(IOCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }
        String ext = getExtension(f).toLowerCase();
        for (String extension : capabilities.getExtensions()) {
            if (extension.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return MessageFormat.format(NbBundle.getMessage(DrawableWriterFilter.class, "IO.formatDescription"), 
                capabilities.getFormat(), capabilities.getName());
    }

    /**
     * Returns the capabilities filtered by this instance.
     *
     * @return writer capabilities.
     */
    public IOCapabilities getWriterCapabilities() {
        return capabilities;
    }

    private static String getExtension(File f) {
        String name = f.getName();
        int lastDot = name.lastIndexOf('.');
        if ((lastDot <= 0) || (lastDot == name.length() - 1)) {
            return ""; //$NON-NLS-1$
        }
        return name.substring(lastDot + 1);
    }
}
