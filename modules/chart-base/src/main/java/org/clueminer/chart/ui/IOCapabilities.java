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

/**
 *
 * @author deric
 */
public class IOCapabilities {

    /**
     * Short format name.
     */
    private final String format;
    /**
     * Long format name.
     */
    private final String name;
    /**
     * MIME type of format.
     */
    private final String mimeType;
    /**
     * File extensions commonly used for this format.
     */
    private final String[] extensions;

    /**
     * Creates a new {@code IOCapabilities} object with the specified format,
     * name, MIME-Type and filename extensions.
     *
     * @param format     Format.
     * @param name       Name.
     * @param mimeType   MIME-Type
     * @param extensions Extensions.
     */
    public IOCapabilities(String format, String name, String mimeType,
            String[] extensions) {
        this.format = format;
        this.name = name;
        this.mimeType = mimeType;
        // TODO Check that there is at least one filename extension
        this.extensions = extensions;
    }

    /**
     * Returns the format.
     *
     * @return Format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the name of the format.
     *
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the MIME-Type of the format.
     *
     * @return Format.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Returns an array with Strings containing all possible filename
     * extensions.
     *
     * @return Filename Extensions.
     */
    public String[] getExtensions() {
        return extensions;
    }
}
