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
package org.clueminer.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ExtensionFileFilter implements FileFilter {

    private ArrayList<String> extension = new ArrayList<>();

    private void addExtension(String ext) {
        // Add a dot, it it doesn't start with one.
        if (!ext.startsWith("\\.")) {
            ext = "." + ext;
        }
        this.extension.add(ext.toLowerCase());
    }

    public ExtensionFileFilter(List<String> ext) {
        for (String s : ext) {
            addExtension(s.toLowerCase());
        }
    }

    public ExtensionFileFilter(String ext) {
        addExtension(ext.toLowerCase());
    }

    public boolean accept(File file) {
        boolean accept = false;
        for (String s : extension) {
            if (file.getName().endsWith(s)) {
                accept = true;
            }
        }
        return accept;
        // return extension.indexOf(file.getName().toLowerCase().e;
    }
}
