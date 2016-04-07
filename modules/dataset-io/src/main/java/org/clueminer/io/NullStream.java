/**
 * %HEADER%
 */
package org.clueminer.io;

import java.io.OutputStream;


/**
 * An OutputStream to nowhere
 * 
 * A /dev/null replacement for Java
 * 
 * @author Thomas Abeel
 *
 */
public class NullStream extends OutputStream {

	@Override
	public void write(int b) {
	}

}
