package eu.medsea.mimeutil.detector;

import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Implementations of this interface try to find out the mime-type (and the encoding if applicable and possible)
 * of a file based on either the file name (usually the extension) or the content - or both.
 * <p>
 * An instance of each {@link MimeTypeDetector} is kept as a singleton by the {@link MimeTypeDetectorRegistry}.
 * Therefore, your implementation must be thread-safe!
 * </p>
 * <p>
 * <b>Important:</b> Please do not implement this interface directly, but extend the class {@link AbstractMimeTypeDetector}
 * instead!
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public interface MimeTypeDetector
{
	void setDetectorID(String detectorID);

	/**
	 * Get the unique identifier for this detector-implementation.
	 *
	 * @return the detector-identifier.
	 */
	String getDetectorID();

	void setOrderHint(int orderHint);

	/**
	 * Get the order hint. The lower this number, the earlier the detector is
	 * triggered during analysis.
	 *
	 * @return the order hint.
	 */
	int getOrderHint();

	/**
	 * Try to determine the mime type(s) from a file's name, an {@link InputStream}
	 * or a {@link RandomAccessFile}.
	 * <p>
	 * Implementations should use the properties passed via the <code>detectionContext</code>
	 * (e.g. the {@link DetectionContext#getFileName() file name} or the {@link DetectionContext#getRandomAccessFile() file content})
	 * to find out what mime-types apply to a given file. The results should be populated into the
	 * {@link DetectionContext#getDetectedMimeTypeSet() DetectedMimeTypeSet}.
	 * </p>
	 * <p>
	 * Usually, implementations only add their findings to the <code>DetectedMimeTypeSet</code>,
	 * but it is possible for a <code>MimeTypeDetector</code> to remove {@link DetectedMimeType} instances from the
	 * <code>DetectedMimeTypeSet</code>, if it is sure that a mime-type found by another detector (before)
	 * is definitely wrong.
	 * </p>
	 *
	 * @param detectionContext the context for this operation (the same instance is passed
	 *		to all <code>MimeTypeDetector</code>s).
	 */
	void detectMimeTypes(DetectionContext detectionContext);
}
