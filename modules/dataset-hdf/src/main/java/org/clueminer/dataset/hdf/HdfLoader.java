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
import java.util.Arrays;
import ncsa.hdf.hdf5lib.H5;
import static ncsa.hdf.hdf5lib.HDF5Constants.H5F_ACC_RDONLY;
import static ncsa.hdf.hdf5lib.HDF5Constants.H5F_OBJ_ALL;
import static ncsa.hdf.hdf5lib.HDF5Constants.H5P_DEFAULT;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.exception.ParserError;
import org.clueminer.utils.DatasetLoader;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 * @param <E>
 */
public class HdfLoader<E extends Instance> implements DatasetLoader<E> {

    private Logger LOG = LoggerFactory.getLogger(HdfLoader.class);

    @Override
    public boolean load(File file, Dataset<E> output) throws ParserError, ParseException, IOException {
        boolean success = false;
        String path = file.getPath();
        String name = file.getName();

        int file_id = -1;
        int dataspace_id = -1;
        int dataset_id = -1;
        int status = 0;

        try {
            H5.H5open();
            LOG.debug("opening file {}", path);
            file_id = H5.H5Fopen(path, H5F_ACC_RDONLY, H5P_DEFAULT);
            LOG.debug("data identifier {}", file_id);

            int nObjs = 0;
            try {
                nObjs = H5.H5Fget_obj_count(file_id, H5F_OBJ_ALL);
            } catch (final HDF5Exception ex) {
                LOG.error("H5.H5Fget_obj_count() failed. ", ex);
            }
            //status = H5.H5Fget_obj_count(file_id, H5P_DEFAULT);
            LOG.debug("objects {}", nObjs);

            if (nObjs > 0) {
                int plist = H5.H5Fget_access_plist(file_id);
                LOG.debug("plist {}", plist);

                int[] ids = new int[nObjs];
                status = H5.H5Fget_obj_ids(file_id, H5F_OBJ_ALL, nObjs, ids);
                LOG.debug("object ids {}", Arrays.toString(ids));

                //H5.H5Dget_space(0);
            } else {
                LOG.warn("Dataset {} seems to be empty", name);
            }

            //    file = H5.Hopen(file, H5F_ACC_RDONLY, H5P_DEFAULT);
            success = true;
        } catch (HDF5Exception ex) {
            if (ex instanceof HDF5LibraryException) {
                HDF5LibraryException hex = (HDF5LibraryException) ex;
                int err = hex.getMinorErrorNumber();
                System.err.println("ERROR: " + hex.getMinorError(err));
            } else {
                Exceptions.printStackTrace(ex);
            }
            success = false;
        }

        return success;
    }

    @Override
    public boolean load(Reader reader, Dataset<E> output) throws ParserError, ParseException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
