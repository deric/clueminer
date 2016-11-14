package eu.medsea.mimeutil;

import eu.medsea.mimeutil.detector.DetectedMimeType;

/**
 * This strategy determines whether the content or the file name shall be used as base for
 * mime type detection.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public final class DetectionStrategy
{
	// TODO when we once switch to Java 5 or 6, we should make this an enum!

	/**
	 * Only analyse the content (ignore the file name).
	 */
	public static final DetectionStrategy ONLY_CONTENT = new DetectionStrategy(1);

	/**
	 * Only analyse the file name (ignore the content).
	 */
	public static final DetectionStrategy ONLY_FILE_NAME = new DetectionStrategy(2);

	/**
	 * Analyse both and prioritize the file-name-based result higher than the content-based result (in case they are not the same).
	 * The {@link DetectedMimeType#getQuality()} property is used to prioritize.
	 */
	public static final DetectionStrategy FILE_NAME_AND_CONTENT = new DetectionStrategy(3);

	/**
	 * Analyse both and prioritize the file-name-based result lower than the content-based result (in case they are not the same).
	 * The {@link DetectedMimeType#getQuality()} property is used to prioritize.
	 */
	public static final DetectionStrategy CONTENT_AND_FILE_NAME = new DetectionStrategy(4);

	private int detectStrategyID;

	private DetectionStrategy(int detectStrategyID) {
		this.detectStrategyID = detectStrategyID;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + detectStrategyID;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetectionStrategy other = (DetectionStrategy) obj;
		if (detectStrategyID != other.detectStrategyID)
			return false;
		return true;
	}

}
