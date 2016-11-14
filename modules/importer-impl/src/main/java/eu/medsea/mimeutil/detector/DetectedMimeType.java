package eu.medsea.mimeutil.detector;

/**
 * An instance of this class denotes a mime-type that might potentially match an
 * analyzed file. The probability that this instance matches the file correctly is indicated
 * in the {@link #getQuality() quality} property.
 * <p>
 * This class implements {@link Comparable}: The natural order of <code>DetectedMimeType</code>s is
 * determined by the properties {@link #getQuality() quality} and {@link #getSerial() serial}.
 * After sorting, the instance with the greatest <code>quality</code>
 * is first. If 2 or more instances have the same <code>quality</code>, the one with the smaller
 * <code>serial</code> comes before.
 * </p>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class DetectedMimeType
implements Comparable
{
	/**
	 * Create an instance of <code>DetectedMimeType</code>.
	 * Do not call this constructor directly! Use {@link DetectedMimeTypeSet#addDetectedMimeType(String, String, double)}
	 * instead.
	 *
	 * @param mimeType the mime-type (never <code>null</code>).
	 * @param mimeEncoding the encoding or <code>null</code>.
	 * @param quality the quality (a number between including 0 and 1).
	 * @param serial the serial.
	 */
	protected DetectedMimeType(String mimeType, String mimeEncoding, double quality, int serial) {
		if (mimeType == null)
			throw new IllegalArgumentException("mimeType must not be null!");

		if (quality > 1)
			throw new IllegalArgumentException("quality > 1");

		if (quality < 0)
			throw new IllegalArgumentException("quality > 0");

		this.mimeType = mimeType;
		this.mimeEncoding = mimeEncoding;
		this.quality = quality;
		this.serial = serial;
	}

	private String mimeType;
	private String mimeEncoding;
	private double quality;
	private int serial;

	/**
	 * Get the mime-type denoted by this <code>DetectedMimeType</code> instance. This
	 * is sth. like <code>text/plain</code>, <code>image/jpeg</code>, <code>application/octet-stream</code> or one
	 * of many other mime-types.
	 *
	 * @return the mime-type - never <code>null</code>.
	 */
	public String getMimeType() {
		return mimeType;
	}
	/**
	 * Get the encoding (if applicable) or <code>null</code>. This is sth. like <code>UTF-8</code>, <code>ISO-8859-1</code> or one of many
	 * other encodings. Note, that it can very well be <code>null</code>, even if the {@link #getMimeType() mime-type} usually
	 * should be specified with an encoding (like <code>text/plain</code>, for example), because depending on the detection strategy
	 * and on the file, it might simply not be possible to find out the encoding (e.g. it's impossible to know how "my-text-file.txt"
	 * is encoded - the extension "txt" doesn't say anything about it).
	 *
	 * @return the encoding or <code>null</code>.
	 */
	public String getMimeEncoding() {
		return mimeEncoding;
	}
	/**
	 * The quality of this <code>DetectedMimeType</code> (i.e. how specific and how probable is this result).
	 * This should range between 0 and 1 (including).
	 *
	 * @return the quality.
	 */
	public double getQuality() {
		return quality;
	}

	/**
	 * A serial number that is used to determine the priority, if the {@link #getQuality() quality} of 2 or more <code>DetectedMimeType</code>s is the same.
	 *
	 * @return the serial.
	 */
	public int getSerial() {
		return serial;
	}

	public int compareTo(Object other)
	{
		DetectedMimeType dmt0 = this;
		DetectedMimeType dmt1 = (DetectedMimeType) other;

		if (dmt0.getQuality() > dmt1.getQuality())
			return -1;

		if (dmt0.getQuality() < dmt1.getQuality())
			return 1;

		// If the calculated quality is the same, we order by instance-creation-order.
		if (dmt0.getSerial() < dmt1.getSerial())
			return -1;

		if (dmt0.getSerial() > dmt1.getSerial())
			return 1;

		return 0; // should never happen - serials should be unique
	}
}
