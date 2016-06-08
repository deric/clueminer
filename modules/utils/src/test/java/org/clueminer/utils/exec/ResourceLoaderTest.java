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
package org.clueminer.utils.exec;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class ResourceLoaderTest {

    private final ResourceLoaderImpl subject = new ResourceLoaderImpl();

    @Test
    public void testSafeName() {
        String name = "aa bb cc";
        assertEquals("aa_bb_cc", ResourceLoaderImpl.safeName(name));

        name = "/foo/bar";
        assertEquals("-foo-bar", ResourceLoaderImpl.safeName(name));

        name = "\\foo\\bar";
        assertEquals("-foo-bar", ResourceLoaderImpl.safeName(name));
    }

    public class ResourceLoaderImpl extends ResourceLoader {

        public Enumeration<URL> searchURL(String path) throws IOException {
            return null;
        }

        public File resource(String path) {
            return null;
        }
    }

}
