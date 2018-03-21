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
package org.clueminer.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class with utility methods for serialization.
 *
 * @author Thomas Abeel
 *
 */
public class Serial {

    /**
     * Checks if file with the supplied file name exists.
     *
     * @param fileName
     *                 file name to check
     * @return true if the file exists, false otherwise
     */
    public static boolean exists(String fileName) {
        return (new File(fileName)).exists();
    }

    /**
     * Stores the supplied object in a file with the supplied name.
     *
     * @param p
     *                 object to store
     * @param fileName
     *                 file to store object in
     * @return true if operation succeeded, false otherwise
     */
    public static boolean store(Object p, String fileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    new GZIPOutputStream(new FileOutputStream(fileName)));
            out.writeObject(p);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load an object from the supplied file
     *
     * @param fileName
     *                 the file to load the object from
     * @return the object that was in the file, or <code>null</code> if the
     *         operation failed
     */
    public static Object load(File fileName) {
        try {
            ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
                    new FileInputStream(fileName)));
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load an object from the supplied file
     *
     * @param fileName
     *                 the file to load the object from
     * @return the object that was in the file, or <code>null</code> if the
     *         operation failed
     */
    public static Object load(String fileName) {
        return load(new File(fileName));
    }
}
