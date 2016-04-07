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
