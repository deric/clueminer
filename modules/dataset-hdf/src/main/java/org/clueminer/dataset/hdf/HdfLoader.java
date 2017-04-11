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
package org.clueminer.dataset.hdf;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParserError;
import org.clueminer.utils.DatasetLoader;

/**
 *
 * @author deric
 * @param <E>
 */
public class HdfLoader<E extends Instance> implements DatasetLoader<E> {

    @Override
    public boolean load(File file, Dataset<E> output) throws ParserError, ParseException, IOException {
        boolean success = false;
        try {
            H5.loadH5Lib();
            H5.H5open();

            //    file = H5.Hopen(file, H5F_ACC_RDONLY, H5P_DEFAULT);
        } catch (HDF5Exception ex) {
            //...
        }
        return success;
    }

    @Override
    public boolean load(Reader reader, Dataset<E> output) throws ParserError, ParseException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
