package eu.medsea.mimeutil.detector;


/**
 * Interceptor to do things before, after or - if supported - inbetween the detection
 * process of one certain {@link MimeTypeDetector}. An instance of a class implementing
 * this interface can only be assigned to one <code>MimeTypeDetector</code>. It must be
 * thread-safe!
 * 
 * @author marco schulze - marco at nightlabs dot de
 */
public interface MimeTypeDetectorInterceptor
{
	MimeTypeDetector getMimeTypeDetector();
	void setMimeTypeDetector(MimeTypeDetector mimeTypeDetector);
	
	/**
	 * Intercept before the {@link MimeTypeDetector#detectMimeTypes(DetectionContext)} method is called.
	 *
	 * @param detectionContext the context for the detection process.
	 */
	void preDetectMimeTypes(DetectionContext detectionContext);

	/**
	 * Intercept sometimes inbetween the process of the {@link MimeTypeDetector#detectMimeTypes(DetectionContext)}
	 * method. This interceptor-method
	 * might never be called, because it is up to the specific <code>MimeTypeDetector</code>
	 * implementation whether or not it supports {@link MimeTypeDetectorInterceptor}s.
	 *
	 * @param detectionContext the context for the detection process.
	 */
	void inDetectMimeTypes(DetectionContext detectionContext);

	/**
	 * Intercept after the {@link MimeTypeDetector#detectMimeTypes(DetectionContext)} method was called.
	 *
	 * @param detectionContext the context for the detection process.
	 */
	void postDetectMimeTypes(DetectionContext detectionContext);
}
