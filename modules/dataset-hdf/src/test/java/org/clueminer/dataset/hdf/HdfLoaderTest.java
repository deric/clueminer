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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class HdfLoaderTest {

    private HdfLoader subject;

    @Before
    public void setUp() {
        subject = new HdfLoader();
    }

    @Test
    public void testLoad_File_Dataset() throws Exception {
        File file = new File("/home/deric/_bench/the_matrix_scenes.hdf5");
        Dataset<Instance> output = new ArrayDataset(100, 50);
        boolean ret = subject.load(file, output);
        assertTrue("expected dataset load to be successful", ret);
    }

}
