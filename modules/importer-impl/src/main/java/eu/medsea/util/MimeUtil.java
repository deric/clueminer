package eu.medsea.util;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteOrder;

import eu.medsea.mimeutil.MimeException;

/**
 * @author Steven McArdle
 * @author marco schulze - marco at nightlabs dot de
 * @deprecated Use {@link eu.medsea.mimeutil.magicfile.Helper} instead!
 */
public class MimeUtil
{
	/**
	 * Mime type used to identify no match
	 */
	public static String UNKNOWN_MIME_TYPE = eu.medsea.mimeutil.magicfile.Helper.UNKNOWN_MIME_TYPE;

	/**
	 * Mime type used to identify a directory
	 */
	public static final String DIRECTORY_MIME_TYPE = eu.medsea.mimeutil.magicfile.Helper.DIRECTORY_MIME_TYPE;

 	/**
	 * Get the native byte order of the OS on which you are running. It will be either big or little endian.
	 * This is used internally for the magic mime rules mapping.
	 *
	 * @return ByteOrder
	 */
	public static ByteOrder getNativeOrder() {
		return eu.medsea.mimeutil.magicfile.Helper.getNativeOrder();
	}

	/**
	 * Get the mime type of the data in the specified {@link InputStream}. Therefore,
	 * the <code>InputStream</code> must support mark and reset
	 * (see {@link InputStream#markSupported()}). If it does not support mark and reset,
	 * an {@link IllegalArgumentException} is thrown.
	 *
	 * @param the stream from which to read the data.
	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the specified <code>InputStream</code> does not support mark and reset (see {@link InputStream#markSupported()}).
	 */
	public static String getMimeType(InputStream in)
		throws MimeException
	{
		return eu.medsea.mimeutil.magicfile.Helper.getMimeType(in);
	}

    /**
     * Get the mime type of a file using a path which can be relative to the JVM
     * or an absolute path. The path can point to a file or directory location and if
     * the path does not point to an actual file or directory the {@link #UNKNOWN_MIME_TYPE}is returned.
     * <p>
     * Their is an exception to this and that is if the <code>fname</code> parameter does NOT point to a real file or directory
     * and extFirst is <code>true</code> then a match against the file extension could be found and would be returned.
     * </p>
     * @param fname points to a file or directory
     * @param extFirst if <code>true</code> will first use file extension mapping and then then <code>magic.mime</code> rules.
     * If <code>false</code> it will try to match the other way around i.e. <code>magic.mime</code> rules and then file extension.
 	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
     * @throws MimeException if while using the <code>magic.mime</code> rules there is a problem processing the file.
     */
    public static String getMimeType(String fname, boolean extFirst) throws MimeException {
    	return eu.medsea.mimeutil.magicfile.Helper.getMimeType(fname, extFirst);
    }

    /**
     * This is a convenience method where the order of lookup is set to extension mapping first.
     * @see #getMimeType(String fname, boolean extFirst)
     */
    public static String getMimeType(String fname) throws MimeException {
    	return eu.medsea.mimeutil.magicfile.Helper.getMimeType(fname);
    }

    /**
     * Get the mime type of a file using a <code>File</code> object which can be relative to the JVM
     * or an absolute path. The path can point to a file or directory location and if
     * the path does not point to an actual file or directory the {@link #UNKNOWN_MIME_TYPE}is returned.
     * <p>
     * Their is an exception to this and that is if the <code>file</code> parameter does NOT point to a real file or directory
     * and extFirst is <code>true</code> then a match against the file extension could be found and would be returned.
     * </p>
     * @param file points to a file or directory
     * @param extFirst if <code>true</code> will first use file extension mapping and then then <code>magic.mime</code> rules.
     * If <code>false</code> it will try to match the other way around i.e. <code>magic.mime</code> rules and then file extension.
 	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
     * @throws MimeException if while using the <code>magic.mime</code> rules there is a problem processing the file.
     */
	public static String getMimeType(File file, boolean extFirst) throws MimeException {
		return eu.medsea.mimeutil.magicfile.Helper.getMimeType(file, extFirst);
	}

    /**
     * This is a convenience method where the order of lookup is set to extension mapping first.
     * @see #getMimeType(File f, boolean extFirst)
     */
	public static String getMimeType(File f) throws MimeException {
		return eu.medsea.mimeutil.magicfile.Helper.getMimeType(f);
	}


	/**
	 * Gives you the best match for your requirements.
	 * <p>
	 * You can pass the accept header from a browser request to this method along with a comma separated
	 * list of possible mime types returned from say getExtensionMimeTypes(...) and the best match according
	 * to the accept header will be returned.
	 * </p>
	 * <p>
	 * The following is typical of what may be specified in an HTTP Accept header:
	 * </p>
	 * <p>
	 * Accept: text/xml, application/xml, application/xhtml+xml, text/html;q=0.9, text/plain;q=0.8, video/x-mng, image/png, image/jpeg, image/gif;q=0.2, text/css, *&#47;*;q=0.1
	 * </p>
	 * <p>
	 * The quality parameter (q) indicates how well the user agent handles the MIME type. A value of 1 indicates the MIME type is understood perfectly,
	 * and a value of 0 indicates the MIME type isn't understood at all.
	 * </p>
	 * <p>
	 * The reason the image/gif MIME type contains a quality parameter of 0.2, is to indicate that PNG & JPEG are preferred over GIF if the server is using
	 * content negotiation to deliver either a PNG or a GIF to user agents. Similarly, the text/html quality parameter has been lowered a little, to ensure
	 * that the XML MIME types are given in preference if content negotiation is being used to serve an XHTML document.
	 * </p>
	 * @param accept is a comma separated list of mime types you can accept including QoS parameters. Can pass the Accept: header directly.
	 * @param canProvide is a comma separated list of mime types that can be provided such as that returned from a call to getExtensionMimeTypes(...)
	 * @return the best matching mime type possible.
	 */
	public static String getPreferedMimeType(String accept, String canProvide) {
		return MimeUtil.getPreferedMimeType(accept, canProvide);
	}

    /**
    *
    * Utility method to get the quality part of a mime type.
    * If it does not exist then it is always set to q=1.0 unless
    * it's a wild card.
    * For the major component wild card the value is set to 0.01
    * For the minor component wild card the value is set to 0.02
    * <p>
    * Thanks to the Apache organisation or these settings.
    *
    * @param mimeType a valid mime type string with or without a valid q parameter
    * @return the quality value of the mime type either calculated from the rules above or the actual value defined.
    * @throws MimeException this is thrown if the mime type pattern is invalid.
    */
   public static double getMimeQuality(String mimeType) throws MimeException{
	   return eu.medsea.mimeutil.magicfile.Helper.getMimeQuality(mimeType);
   }

	/**
	 * Get the mime type of a file using the <code>magic.mime</code> rules files.
	 * @param f is a file object that points to a file or directory.
 	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the file cannot be parsed.
	 */
    public static String getMagicMimeType(File file) throws MimeException {
    	return eu.medsea.mimeutil.magicfile.Helper.getMagicMimeType(file);
    }

    /**
     * Utility method to get the major part of a mime type
     * i.e. the bit before the '/' character
     *
     * @param mimeType you want to get the major part from
     * @return major component of the mime type
     * @throws MimeException if you pass in an invalid mime type structure
     */
    public static String getMajorComponent(String mimeType) throws MimeException {
    	return eu.medsea.mimeutil.magicfile.Helper.getMajorComponent(mimeType);
    }

    /**
     * Utility method to get the minor part of a mime type
     * i.e. the bit after the '/' character
     *
     * @param mimeType you want to get the minor part from
     * @return minor component of the mime type
     * @throws MimeException if you pass in an invalid mime type structure
     */
    public static String getMinorComponent(String mimeType) throws MimeException{
    	return eu.medsea.mimeutil.magicfile.Helper.getMinorComponent(mimeType);
    }

    /**
     * Get the extension part of a file name defined by the file parameter.
     * @param file a file object
     * @return the file extension or null if it does not have one.
     */
    public static String getFileExtension(File file) {
    	return eu.medsea.mimeutil.magicfile.Helper.getFileExtension(file);
    }

    /**
     * Get the extension part of a file name defined by the fname parameter.
     * @param fileName a relative or absolute path to a file
     * @return the file extension or null if it does not have one.
     */
	public static String getFileExtension(String fileName) {
		return eu.medsea.mimeutil.magicfile.Helper.getFileExtension(fileName);
	}

	/**
	 * While all of the property files and magic.mime files are being loaded the utility keeps a list of mime types it's seen.
	 * You can add other mime types to this list using this method. You can then use the isMimeTypeKnown(...) utility method to see
	 * if a mime type you have matches one that the utility already understands.
	 * <p>
	 * For instance if you had a mime type of abc/xyz and passed this to isMimeTypeKnown(...) it would return false unless you specifically
	 * add this to the know mime types using this method.
	 * </p>
	 * @param mimeType a mime type you want to add to the known mime types. Duplicates are ignored.
	 * @see #isMimeTypeKnown(String mimetype)
	 */
	// Add a mime type to the list of known mime types.
	public static void addKnownMimeType(String mimeType) {
		eu.medsea.mimeutil.magicfile.Helper.addKnownMimeType(mimeType);
	}

	/**
	 * Check to see if this mime type is one of the types seen during initialisation
	 * or has been added at some later stage using addKnownMimeType(...)
	 * @param mimeType
	 * @return true if the mimeType is in the list else false is returned
	 * @see #addKnownMimeType(String mimetype)
	 */
	public static boolean isMimeTypeKnown(String mimeType) {
		return eu.medsea.mimeutil.magicfile.Helper.isMimeTypeKnown(mimeType);
	}

	/**
	 * Get the first in a comma separated list of mime types. Useful when using extension mapping
	 * that can return multiple mime types separate by commas and you only want the first one.
	 * Will return UNKNOWN_MIME_TYPE if the passed in list is null or empty.
	 *
	 * @param mimeTypes comma separated list of mime types
	 * @return the first in a comma separated list of mime types or the UNKNOWN_MIME_TYPE if the mimeTypes parameter is null or empty.
	 */
	public static String getFirstMimeType(String mimeTypes) {
		return eu.medsea.mimeutil.magicfile.Helper.getFirstMimeType(mimeTypes);
	}

	/**
	 * Get the mime type of a file using file extension mappings. The file path can be a relative or absolute or can be a completely
	 * non-existent file as only the extension is important.
	 * @param file is a <code>File</code> object that points to a file or directory. If the file or directory cannot be found
	 * {@link #UNKNOWN_MIME_TYPE} is returned.
 	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the file cannot be parsed.
	 */
    public static String getExtensionMimeTypes(File file) {
    	return eu.medsea.mimeutil.magicfile.Helper.getExtensionMimeTypes(file);
    }

	/**
	 * Get the mime type of a file using file extension mappings. The file path can be a relative or absolute or can be a completely
	 * non-existent file as only the extension is important.
	 * @param fname is a path that points to a file or directory. If the file or directory cannot be found
	 * {@link #UNKNOWN_MIME_TYPE} is returned.
 	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the file cannot be parsed.
	 */
    public static String getExtensionMimeTypes(String fname) {
    	return eu.medsea.mimeutil.magicfile.Helper.getExtensionMimeTypes(fname);
    }

	/**
	 * Get the mime type of a file using the <code>magic.mime</code> rules files.
	 * @param fname is a path location to a file or directory.
 	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the file cannot be parsed.
	 */
	public static String getMagicMimeType(String fname) throws MimeException {
		return eu.medsea.mimeutil.magicfile.Helper.getMagicMimeType(fname);
	}

//    /**
//     * The default mime type returned by a no match i.e. is not matched in either the extension mapping or magic.mime rules is
//     * application/octet-stream. However, applications may want to treat a no match different from a match that could return application/octet-stream.
//     * This method allows you to set a different mime type to represent a no match such as a custom mime type like application/unknown-mime-type
//     * @param mimeType set the default returned mime type for a no match.
//     */
//    public static void setUnknownMimeType(String mimeType) {
//    	eu.medsea.mimeutil.magicfile.Helper.setUnknownMimeType(mimeType);
//    }
}
