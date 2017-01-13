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
package org.clueminer.importer.impl;

import java.io.IOException;
import java.util.Collection;
import org.clueminer.fixtures.ImageFixture;
import org.clueminer.fixtures.MLearnFixture;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class MimeHelperTest {

    private final MLearnFixture fixtures = new MLearnFixture();

    private final MimeHelper subject = new MimeHelper();

    public MimeHelperTest() {
    }

    @Test
    public void testDetectMIME() throws IOException {
        Collection col = subject.detectMIME(fixtures.iris());
        System.out.println("iris: " + col.toString());
        assertEquals(true, col.contains("application/octet-stream"));
        col = subject.detectMIME(fixtures.dermatology());
        System.out.println(col);
        //this might be platform dependent
        //assertEquals(true, col.contains("text/x-tex"));
        ImageFixture inf = new ImageFixture();
        col = subject.detectMIME(inf.insect3d());
        System.out.println("image: " + col);

    }

}
