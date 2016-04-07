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
	 *            file name to check
	 * @return true if the file exists, false otherwise
	 */
	public static boolean exists(String fileName) {
		return (new File(fileName)).exists();
	}

	/**
	 * Stores the supplied object in a file with the supplied name.
	 * 
	 * @param p
	 *            object to store
	 * @param fileName
	 *            file to store object in
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
	 *            the file to load the object from
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
	 *            the file to load the object from
	 * @return the object that was in the file, or <code>null</code> if the
	 *         operation failed
	 */
	public static Object load(String fileName) {
		return load(new File(fileName));
	}
}
