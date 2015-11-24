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
package org.clueminer.fastcommunity.orig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.utils.exec.ResourceLoader;
import org.openide.util.Exceptions;

/**
 *
 * @author deric
 */
public class FcLoader<E extends Instance> extends ResourceLoader {

    protected static final String prefix = "/org/clueminer/fastcommunity";
    protected static final String hintPackage = "fastcommunity";

    private static final String lineEnd = "\n";
    private static final String space = " ";

    /**
     * Resource packed in jar is not possible to open directly, this method uses
     * a .tmp file which should be on exit deleted
     *
     * @param path
     * @return
     */
    @Override
    public File resource(String path) {
        return resource(path, prefix, hintPackage);
    }

    @Override
    public Enumeration<URL> searchURL(String path) throws IOException {
        return FcLoader.class.getClassLoader().getResources(path);
    }

    /**
     * Write dataset into a file as space separated values
     *
     * @param dataset
     * @param file
     * @throws FileNotFoundException
     */
    public void exportDataset(Dataset<E> dataset, File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            sb.append(dataset.size()).append(space).append(dataset.attributeCount()).append(lineEnd);
            writer.write(sb.toString());

            for (E instance : dataset) {
                sb = new StringBuilder();
                for (int i = 0; i < instance.size(); i++) {
                    if (i > 0) {
                        sb.append(space);
                    }
                    sb.append(String.valueOf(instance.get(i)));
                }
                sb.append(lineEnd);
                writer.write(sb.toString());
            }

        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
