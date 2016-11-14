package eu.medsea.mimeutil.detector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import eu.medsea.mimeutil.DetectionStrategy;
import eu.medsea.mimeutil.MimeUtil;

/**
 * An instance of this class is used to collect {@link DetectedMimeType} instances during the detection process.
 * <p>
 * Use {@link #getDetectedMimeTypes()} to get a {@link Collection} of all mime-types that were found or
 * the convenience method {@link #getMimeType()} for the single most probable mime-type.
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 *
 * @see MimeUtil#detectMimeTypes(java.io.File, DetectionStrategy)
 * @see MimeUtil#detectMimeTypes(java.io.InputStream, String, DetectionStrategy)
 * @see MimeTypeDetector#detectMimeTypes(DetectionContext)
 */
public class DetectedMimeTypeSet
{
	private SortedSet detectedMimeTypes = new TreeSet();

	/**
	 * key: String mimeType<br/>
	 * value: SortedSet&lt;DetectedMimeType&gt; detectedMimeTypes
	 */
	private Map mimeType2DetectedMimeTypes = new HashMap();

	/**
	 * Get the {@link DetectedMimeType}s in their natural order (they implement {@link Comparable}).
	 *
	 * @return the {@link DetectedMimeType}s.
	 */
	public Collection getDetectedMimeTypes() {
		return Collections.unmodifiableSet(detectedMimeTypes);
	}

	/**
	 * Get those {@link DetectedMimeType}s (in their natural order) that match the specified <code>mimeType</code>.
	 * If none matches, this method returns an empty <code>Set</code>.
	 *
	 * @param mimeType the mime-type to look for.
	 * @return those sorted {@link DetectedMimeType}s where {@link DetectedMimeType#getMimeType()} equals the specified mime-type.
	 */
	public Collection getDetectedMimeTypes(String mimeType) {
		Set dmts = (Set) mimeType2DetectedMimeTypes.get(mimeType);
		if (dmts == null)
			return Collections.EMPTY_SET;

		return Collections.unmodifiableSet(dmts);
	}

	private int nextDetectedMimeTypeSerial = Integer.MIN_VALUE;
	private synchronized int nextDetectedMimeTypeSerial()
	{
		return nextDetectedMimeTypeSerial++;
	}

	public DetectedMimeType addDetectedMimeType(String mimeType, String mimeEncoding, double quality)
	{
		DetectedMimeType detectedMimeType = new DetectedMimeType(mimeType, mimeEncoding, quality, nextDetectedMimeTypeSerial());
		addDetectedMimeType(detectedMimeType);
		return detectedMimeType;
	}

	private void addDetectedMimeType(DetectedMimeType detectedMimeType)
	{
		detectedMimeTypes.add(detectedMimeType);

		SortedSet dmts = (SortedSet) mimeType2DetectedMimeTypes.get(detectedMimeType.getMimeType());
		if (dmts == null) {
			dmts = new TreeSet();
			mimeType2DetectedMimeTypes.put(detectedMimeType.getMimeType(), dmts);
		}
		dmts.add(detectedMimeType);
	}

	public void removeDetectedMimeType(DetectedMimeType detectedMimeType)
	{
		detectedMimeTypes.remove(detectedMimeType);

		SortedSet dmts = (SortedSet) mimeType2DetectedMimeTypes.get(detectedMimeType.getMimeType());
		if (dmts != null) {
			dmts.remove(detectedMimeType);
			if (dmts.isEmpty())
				mimeType2DetectedMimeTypes.remove(detectedMimeType.getMimeType());
		}
	}

	/**
	 * Does this <code>DetectedMimeTypeSet</code> already contain at least one {@link DetectedMimeType} where
	 * the {@link DetectedMimeType#getMimeType() mime-type} property is the specified <code>mimeType</code>.
	 *
	 * @param mimeType the mime-type to look for.
	 * @return <code>true</code> if it already exists, <code>false</code> otherwise.
	 */
	public boolean containsMimeType(String mimeType)
	{
		return mimeType2DetectedMimeTypes.containsKey(mimeType);
	}

	/**
	 * Does this <code>DetectedMimeTypeSet</code> already contain at least one {@link DetectedMimeType} where
	 * the {@link DetectedMimeType#getMimeType() mime-type} property is the specified <code>mimeType</code>
	 * and the {@link DetectedMimeType#getQuality() quality} property is greater or equals the specified
	 * <code>minimumQuality</code>.
	 *
	 * @param mimeType the mime-type to look for.
	 * @return <code>true</code> if it already exists, <code>false</code> otherwise.
	 */
	public boolean containsMimeType(String mimeType, double minimumQuality)
	{
		SortedSet detectedMimeTypes = (SortedSet) mimeType2DetectedMimeTypes.get(mimeType);
		if (detectedMimeTypes == null)
			return false;

		for (Iterator it = detectedMimeTypes.iterator(); it.hasNext();) {
			DetectedMimeType detectedMimeType = (DetectedMimeType) it.next();
			if (detectedMimeType.getQuality() >= minimumQuality)
				return true;
		}

		return false;
	}

	/**
	 * Convenience method returning the best matching mime-type as simple <code>String</code>.
	 * If this <code>DetectedMimeTypeSet</code> is empty, it will return the value
	 * passed as argument <code>fallbackMimeType</code>. Otherwise, it returns the
	 * {@link DetectedMimeType#getMimeType() mimeType} property of the first entry in the
	 * {@link #getDetectedMimeTypes() detected mime types}.
	 *
	 * @param fallbackMimeType the mime-type to return, if this set is empty. This might be <code>null</code>.
	 * @return the mime-type, only <code>null</code>, if <code>fallbackMimeType</code> is <code>null</code> and this set is empty.
	 */
	public String getMimeType(String fallbackMimeType)
	{
		if (detectedMimeTypes.isEmpty())
			return fallbackMimeType;

		return (String) detectedMimeTypes.iterator().next();
	}

	/**
	 * Convenience method returning the best matching mime-type as simple <code>String</code>.
	 * If this <code>DetectedMimeTypeSet</code> is empty, it will return
	 * {@link MimeUtil#OCTET_STREAM_MIME_TYPE}. Otherwise, it returns the
	 * {@link DetectedMimeType#getMimeType() mimeType} property of the first entry in the
	 * {@link #getDetectedMimeTypes() detected mime types}.
	 *
	 * @return the mime-type, never <code>null</code>.
	 */
	public String getMimeType()
	{
		return getMimeType(MimeUtil.OCTET_STREAM_MIME_TYPE);
	}
}
