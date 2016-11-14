package eu.medsea.mimeutil.detector;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.medsea.mimeutil.DetectionStrategy;

/**
 * The context of a mime-type-detection. One instance of this class is created by the
 * {@link MimeTypeDetectorRegistry} for each detection process. This same instance is
 * then passed to all {@link MimeTypeDetector}s.
 * <p>
 * Basically, this object serves for wrapping all parameters that are passed to the
 * {@link MimeTypeDetector#detectMimeTypes(DetectionContext)} method. This is to
 * prevent that implementations of the interface <code>MimeTypeDetector</code> break when the
 * contract is extended later.
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 *
 * @see MimeTypeDetector#detectMimeTypes(DetectionContext)
 */
public class DetectionContext
{
	private void checkArguments()
	{
		if (detectionStrategy == null)
			throw new IllegalArgumentException("detectionStrategy must not be null!");

//		// Our engine would work, but return an empty result - make it easier for the developer by telling
//		// him that he's passing only null to us. Shall we really do this?
//		if (inputStream == null && randomAccessFile == null && fileName == null)
//			throw new IllegalArgumentException("No data source has been specified - the detection cannot proceed since it has no information available at all. Specify one (or more) of the file's name, an InputStream or a file.");
	}

	/**
	 * Create the context for determining the mime type(s) from an {@link InputStream} or
	 * from the file name.
	 *
	 * @param inputStream <code>null</code> or the stream to read the data from. It must support <code>mark</code> &amp; <code>reset</code> (see {@link InputStream#markSupported()}.
	 * @param fileName <code>null</code> or the simple name of the file (no path!).
	 * @param detectionStrategy how to do the detection. See {@link #getDetectionStrategy()} for further details.
	 * @param detectedMimeTypeSet a collection into which you can add the mime types your implementation detected. It is also
	 *		possible to remove elements that another <code>MimeTypeDetector</code> implementation added before (if you can be
	 *		sure this result was wrong) and it is possible to take the previously detected mime-types into account for your own
	 *		check (for example, skip the check if a certain type is not in there).
	 */
	public DetectionContext(InputStream inputStream, String fileName, DetectionStrategy detectionStrategy)
	{
		this.inputStream = inputStream;
		this.fileName = fileName;
		this.detectionStrategy = detectionStrategy;

		checkArguments();
	}

	/**
	 * Create the context for determining the mime type(s) from a {@link RandomAccessFile} or
	 * from the file name.
	 *
	 * @param randomAccessFile <code>null</code> or the file to read the data from.
	 * @param fileName <code>null</code> or the simple name of the file (no path!).
	 * @param detectionStrategy how to do the detection. See {@link #getDetectionStrategy()} for further details.
	 * @param detectedMimeTypeSet a collection into which you can add the mime types your implementation detected. It is also
	 *		possible to remove elements that another <code>MimeTypeDetector</code> implementation added before (if you can be
	 *		sure this result was wrong) and it is possible to take the previously detected mime-types into account for your own
	 *		check (for example, skip the check if a certain type is not in there).
	 */
	public DetectionContext(RandomAccessFile randomAccessFile, String fileName, DetectionStrategy detectionStrategy)
	{
		this.randomAccessFile = randomAccessFile;
		this.fileName = fileName;
		this.detectionStrategy = detectionStrategy;

		checkArguments();
	}

	private RandomAccessFile randomAccessFile;
	private InputStream inputStream;
	private String fileName;
	private DetectionStrategy detectionStrategy;

	private String[] fileExtensions;
	
	private Map userObjects = new HashMap();

	private DetectedMimeTypeSet detectedMimeTypeSet = new DetectedMimeTypeSet();

	public RandomAccessFile getRandomAccessFile() {
		return randomAccessFile;
	}

	/**
	 * Get <code>null</code> or the {@link InputStream} from which to read the file's contents.
	 * <p>
	 * <b>You are not allowed to consume the <code>InputStream</code>!!!</b>
	 * That means, you must use {@link InputStream#mark(int)} and
	 * {@link InputStream#reset()} in order to guarantee that the stream is in the original
	 * state after your method returned. Thus, it's recommended to call {@link InputStream#reset()}
	 * in a finally block!
	 * </p>
	 * @return <code>null</code> or an {@link InputStream}.
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	/**
	 * Get <code>null</code> or the simple name of the file (no path!).
	 * @return <code>null</code> or the file name.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * Get the {@link DetectionStrategy} (never <code>null</code>). It describes how
	 * a {@link MimeTypeDetector} should proceed its detection. If the strategy is not supported by a certain implementation or
	 * it does not match the data specified (e.g. <code>randomAccessFile</code> is <code>null</code> but the strategy
	 * is {@link DetectionStrategy#ONLY_CONTENT}) the detector implementation should silently return and skip its checks.
	 *
	 * @return an instance of {@link DetectionStrategy} - never <code>null</code>.
	 */
	public DetectionStrategy getDetectionStrategy() {
		return detectionStrategy;
	}

	/**
	 * Get <code>null</code> or the file extensions of the file. This is an
	 * empty array, if the file name was specified but does not contain a dot
	 * ('.'). If it contains multiple dots, the first entry is the simple
	 * extension, the second entry is the composed extension of the last 2 parts
	 * and so on. For example, if the file name is "myfile.tar.gz.gpg", the
	 * first entry will be "gpg", the second "gz.gpg" and the third will be
	 * "tar.gz.gpg". Hence, this file is an encrypted tar ball (the mime-type
	 * should probably be "application/pgp-encrypted", since there doesn't exist
	 * a more specific content-type).
	 *
	 * @return <code>null</code> or an array with 0 or elements.
	 */
	public String[] getFileExtensions() {
		if (fileExtensions == null && fileName != null) {
			int fromIndex = Integer.MAX_VALUE;
			int dotIndex = -1;
			List l = new ArrayList();
			do {
				dotIndex = fileName.lastIndexOf('.', fromIndex);
				if (dotIndex >= 0) {
					fromIndex = dotIndex - 1;
					String s = fileName.substring(dotIndex + 1);
					l.add(s);
				}
			} while (dotIndex >= 0);
			fileExtensions = (String[]) l.toArray(new String[l.size()]);
		}

		return fileExtensions;
	}

	public DetectedMimeTypeSet getDetectedMimeTypeSet() {
		return detectedMimeTypeSet;
	}

	public Map getUserObjects() {
		return userObjects;
	}
}
