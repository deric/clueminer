package eu.medsea.mimeutil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Collection;

import eu.medsea.mimeutil.detector.DetectedMimeType;
import eu.medsea.mimeutil.detector.DetectedMimeTypeSet;
import eu.medsea.mimeutil.detector.MimeTypeDetectorRegistry;

/**
 * Central utility class for mime-type detection and mime-type handling
 * (e.g. in web servers, browsers and the like).
 *
 * @author Steven McArdle
 * @author marco schulze - marco at nightlabs dot de
 */
public class MimeUtil
{
	/**
	 * Mime type used to identify no match. In your application, you should normally handle it the same way as
	 * {@link #OCTET_STREAM_MIME_TYPE}.
	 *
	 * @see #OCTET_STREAM_MIME_TYPE
	 */
	public static final String UNKNOWN_MIME_TYPE = "application/x-unknown-mime-type";

	/**
	 * Mime type used to identify a generic octet-stream. This usually means the same as {@link #UNKNOWN_MIME_TYPE}.
	 * @see #UNKNOWN_MIME_TYPE
	 */
	public static final String OCTET_STREAM_MIME_TYPE = "application/octet-stream";

	/**
	 * Mime type used to identify a directory
	 */
	public static final String DIRECTORY_MIME_TYPE = "application/directory";

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
	 * @deprecated Use {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} or {@link #detectMimeTypes(File, DetectionStrategy)} instead!
	 */
	public static String getMimeType(InputStream in)
	throws MimeException
	{
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				in, null, DetectionStrategy.ONLY_CONTENT
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
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
	 * @deprecated Use {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} or {@link #detectMimeTypes(File, DetectionStrategy)} instead!
	 */
	public static String getMimeType(String fname, boolean extFirst) throws MimeException {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				(RandomAccessFile)null, fname, DetectionStrategy.ONLY_CONTENT
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
	}

	/**
	 * This is a convenience method where the order of lookup is set to extension mapping first.
	 * @see #getMimeType(String fname, boolean extFirst)
	 * @deprecated Use {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} or {@link #detectMimeTypes(File, DetectionStrategy)} instead!
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
	 * @deprecated Use {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} or {@link #detectMimeTypes(File, DetectionStrategy)} instead!
	 */
	public static String getMimeType(File file, boolean extFirst) throws MimeException {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				file, file.getName(), extFirst ? DetectionStrategy.FILE_NAME_AND_CONTENT : DetectionStrategy.CONTENT_AND_FILE_NAME
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
	}

	/**
	 * This is a convenience method where the order of lookup is set to extension mapping first.
	 * @see #getMimeType(File, boolean)
	 * @deprecated Use {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} or {@link #detectMimeTypes(File, DetectionStrategy)} instead!
	 */
	public static String getMimeType(File file) throws MimeException {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				file, file.getName(), DetectionStrategy.FILE_NAME_AND_CONTENT
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
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
		return eu.medsea.mimeutil.magicfile.Helper.getPreferedMimeType(accept, canProvide);
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
	 * @deprecated Use {@link #detectMimeTypes(File, DetectionStrategy)} or {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} instead!
	 */
	public static String getMagicMimeType(File file) throws MimeException {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				file, (String)null, DetectionStrategy.ONLY_CONTENT
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
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
	 * @deprecated Use {@link #detectMimeTypes(File, DetectionStrategy)} or {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} instead.
	 */
	public static String getExtensionMimeTypes(File file) {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				(RandomAccessFile)null, file.getName(), DetectionStrategy.ONLY_FILE_NAME
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
	}

	/**
	 * Get the mime type of a file using file extension mappings. The file path can be a relative or absolute or can be a completely
	 * non-existent file as only the extension is important.
	 * @param fname is a path that points to a file or directory. If the file or directory cannot be found
	 * {@link #UNKNOWN_MIME_TYPE} is returned.
	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the file cannot be parsed.
	 * @deprecated Use {@link #detectMimeTypes(File, DetectionStrategy)} or {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} instead!
	 */
	public static String getExtensionMimeTypes(String fname) {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				(RandomAccessFile)null, fname, DetectionStrategy.ONLY_FILE_NAME
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
	}

	/**
	 * Get the mime type of a file using the <code>magic.mime</code> rules files.
	 * @param fname is a path location to a file or directory.
	 * @return the mime type. Never returns <code>null</code> (if the mime type cannot be found, {@link #UNKNOWN_MIME_TYPE} is returned).
	 * @throws MimeException if the file cannot be parsed.
	 * @deprecated Use {@link #detectMimeTypes(File, DetectionStrategy)} or {@link #detectMimeTypes(InputStream, String, DetectionStrategy)} instead!
	 */
	public static String getMagicMimeType(String fname) throws MimeException {
		DetectedMimeTypeSet detectedMimeTypeSet = MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(
				(RandomAccessFile)null, fname, DetectionStrategy.ONLY_FILE_NAME
		);
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return UNKNOWN_MIME_TYPE;

		return ((DetectedMimeType)c.iterator().next()).getMimeType();
	}

	/**
	 * Try to determine the mime type(s) from the file name or the contents read from the {@link InputStream}
	 * (magic numbers).
	 * <p>
	 * If you pass a parameter combination that does not make sense (e.g. <code>null</code> as
	 * <code>inputStream</code> and the
	 * <code>DetectionStrategy</code> {@link DetectionStrategy#ONLY_CONTENT ONLY_CONTENT}), the method will still succeed,
	 * but the resulting {@link DetectedMimeTypeSet} will be empty.
	 * </p>
	 *
	 * @param inputStream <code>null</code> or the stream to read the data from. It must support <code>mark</code> &amp; <code>reset</code> (see {@link InputStream#markSupported()}.
	 * @param fileName <code>null</code> or the simple name of the file (no path!).
	 * @param detectionStrategy how to proceed detection. If the detect strategy is not supported by your implementation or
	 *		it does not match the data specified (e.g. <code>randomAccessFile</code> is <code>null</code> but the strategy
	 *		is {@link DetectionStrategy#ONLY_CONTENT}) you should silently return and skip your checks.
	 * @return a collection of all mime-types that were detected wrapped in a {@link DetectedMimeTypeSet} instance.
	 *
	 * @see #detectMimeTypes(File, DetectionStrategy)
	 */
	public DetectedMimeTypeSet detectMimeTypes(InputStream inputStream, String fileName, DetectionStrategy detectionStrategy)
	{
		return MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(inputStream, fileName, detectionStrategy);
	}

	/**
	 * Try to determine the mime type(s) from the file name or the contents of the file (magic numbers).
	 * Note, that the specified <code>file</code> instance does not need to point to an existing
	 * file. It can be used to solely pass the file's name, in order to determine the mime type from the
	 * file extension.
	 * <p>
	 * If you pass a parameter combination that does not make sense (e.g. a non-existing file and the
	 * <code>DetectionStrategy</code> {@link DetectionStrategy#ONLY_CONTENT ONLY_CONTENT}), the method will still succeed,
	 * but the resulting {@link DetectedMimeTypeSet} will be empty.
	 * </p>
	 *
	 * @param file <code>null</code> or the file to read the data from or a simple file name wrapper
	 *		(of a file that doesn't need to exist - maybe without path).
	 * @param detectionStrategy how to proceed detection. If the detect strategy is not supported by your implementation or
	 *		it does not match the data specified (e.g. <code>randomAccessFile</code> is <code>null</code> but the strategy
	 *		is {@link DetectionStrategy#ONLY_CONTENT}) you should silently return and skip your checks.
	 * @return a collection of all mime-types that were detected wrapped in a {@link DetectedMimeTypeSet} instance.
	 *
	 * @see #detectMimeTypes(InputStream, String, DetectionStrategy)
	 */
	public DetectedMimeTypeSet detectMimeTypes(File file, DetectionStrategy detectionStrategy)
	{
		String fileName = file == null ? null : file.getName();
		return MimeTypeDetectorRegistry.sharedInstance().detectMimeTypes(file, fileName, detectionStrategy);
	}
}
