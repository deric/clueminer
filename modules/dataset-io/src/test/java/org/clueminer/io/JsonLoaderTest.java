/*
 * Copyright (C) 2011-2016 clueminer.org
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
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.ArrayDataset;
import org.clueminer.fixtures.CommonFixture;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class JsonLoaderTest {

    private final JsonLoader subject;
    private static CommonFixture tf;

    public JsonLoaderTest() {
        subject = new JsonLoader();
        tf = new CommonFixture();
    }

    @Test
    public void testLoad() throws Exception {
        File file = tf.simpleJson();
        Dataset<? extends Instance> output = new ArrayDataset(15, 4);
        subject.load(file, output);
    }

}
