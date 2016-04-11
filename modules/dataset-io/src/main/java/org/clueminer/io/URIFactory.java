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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

/**
 *
 * @author Thomas Abeel
 *
 */
public class URIFactory {

    public static URL url(String s) throws MalformedURLException, URISyntaxException {
        return uri(s).toURL();
    }

    /**
     * @param string
     * @return
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public static URI uri(String s) throws MalformedURLException, URISyntaxException {
        try {
            /* Make sure that encoding does not happen multiple times */
            s = URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // This should not happen, I'm fairly certain UTF-8 exists.
            System.err.println("Apparently I was wrong.");
            throw new RuntimeException(e);

        }
        URL url = new URL(s);
        String host = url.getHost();
        String protocol = url.getProtocol();
        String path = url.getPath();
        String query = url.getQuery();
        String user = url.getUserInfo();
        int port = url.getPort();
        String anchor = url.getRef();
        return new URI(protocol, user, host, port, path, query, anchor);
    }
}
