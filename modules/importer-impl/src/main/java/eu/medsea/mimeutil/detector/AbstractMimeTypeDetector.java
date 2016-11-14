package eu.medsea.mimeutil.detector;

/**
 * Abstract base implementation of {@link MimeTypeDetector}.
 * It's urgently recommended to subclass this class instead of implementing
 * the interface directly.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public abstract class AbstractMimeTypeDetector
implements MimeTypeDetector
{
	private String detectorID;
	private int orderHint = -1;

	public String getDetectorID() {
		return detectorID;
	}

	public void setDetectorID(String detectorID) {
		if (detectorID == null)
			throw new IllegalArgumentException("detectorID == null");

		if (this.detectorID != null)
			throw new IllegalStateException("Cannot modify the detectorID after it has been initialized!");

		this.detectorID = detectorID;
	}

	public int getOrderHint() {
		return orderHint;
	}

	public void setOrderHint(int orderHint) {
		if (orderHint < 0)
			throw new IllegalArgumentException("orderHint must not be negative!");

		if (this.orderHint >= 0)
			throw new IllegalStateException("Cannot modify the orderHint after it has been initialized!");

		this.orderHint = orderHint;
	}
}
