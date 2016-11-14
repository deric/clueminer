package eu.medsea.mimeutil.detector;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.medsea.mimeutil.DetectionStrategy;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.eclipse.MimeTypeDetectorExtensionPointProcessor;

/**
 * This registry manages plug-ins for detecting mime-types. To implement your own mime-type-detector,
 * you have to do the following:
 * <ul>
 * <li>Subclass {@link AbstractMimeTypeDetector}.</li>
 * <li>Subclass {@link AbstractMimeTypeDetector}.</li>
 * <li>Register your -implementation. You can do this in 3 ways:
 *		<ul>
 *			<li>When using an Eclipse runtime, specify an extension to the extension-point
 *				"eu.medsea.mimeutil.mimeTypeDetector".
 *			</li>
 *			<li>When you're not in an Eclipse environment (i.e. the bundle <code>org.eclipse.core.runtime</code> isn't there),
 *				you can create a file named <code>MimeTypeDetector.csv</code> and place it in the root of your JAR (no package).
 *				See the file <code>eu/medsea/mimeutil/detector/MimeTypeDetector.csv</code> for an example and for more details
 *				about this file.
 *				Note, that due to separated class-loaders, your CSV files won't usually be found in an OSGi-container! If your OSGi-container
 *				supports buddy-class-loading, you can register a buddy to this bundle (put "Eclipse-RegisterBuddy: eu.medsea.mimeutil"
 *				into your <code>MANIFEST.MF</code>) to get the CSV-based extension working.
 *			</li>
 *		</ul>
 * </li>
 * </ul>
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class MimeTypeDetectorRegistry
{
	private static Log log = LogFactory.getLog(MimeTypeDetectorRegistry.class);
	private static MimeTypeDetectorRegistry sharedInstance;

	public static MimeTypeDetectorRegistry sharedInstance() {
		if (sharedInstance == null) {
			sharedInstance = new MimeTypeDetectorRegistry();
		}

		return sharedInstance;
	}

	protected MimeTypeDetectorRegistry() {
		loadCSVRegistrations();
		loadExtensionsForExtensionPoint();
	}

	private void loadExtensionsForExtensionPoint()
	{
		try {
			new MimeTypeDetectorExtensionPointProcessor().process();
		} catch (NoClassDefFoundError x) {
			// ignore - obviously org.eclipse.core.runtime isn't there
		} catch (Throwable x) {
			log.error("loadExtensionsForExtensionPoint: " + x.getClass().getName() + ": " + x.getMessage(), x);
		}
	}

	private void loadCSVRegistrations()
	{
		String fileName = "MimeTypeDetector.csv";
		loadCSVRegistration(MimeTypeDetectorRegistry.class.getResource(fileName));
		try {
			for (Enumeration e = MimeTypeDetectorRegistry.class.getClassLoader().getResources(fileName); e.hasMoreElements(); ) {
				URL url = (URL) e.nextElement();
				loadCSVRegistration(url);
			}
		} catch (Throwable x) {
			log.error("loadCSVRegistrations: " + x.getClass().getName() + ": " + x.getMessage(), x);
		}
	}

	private void loadCSVRegistration(URL url)
	{
		if (url == null)
			return;

		int lineNo = -1;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			try {
				String line; lineNo = 0;
				while (null != (line = reader.readLine())) {
					++lineNo;
					line = line.trim();
					if (line.isEmpty() || line.startsWith("#"))
						continue;

					String[] fields = line.split("\t");
					if (fields.length < 1)
						throw new IllegalStateException("No field at all?! Shouldn't have line.isEmpty() returned true?!!!");

					try {
						String className = fields[0].trim();
						String detectorID = fields.length > 1 ? fields[1].trim() : className;
						String orderHintStr = fields.length > 2 ? fields[2].trim() : String.valueOf(7000);

						Class clazz = Class.forName(className);
						int orderHint = Integer.parseInt(orderHintStr);

						MimeTypeDetector mimeTypeDetector = (MimeTypeDetector) clazz.newInstance();
						mimeTypeDetector.setDetectorID(detectorID);
						mimeTypeDetector.setOrderHint(orderHint);

						addMimeTypeDetector(mimeTypeDetector);
					} catch (Throwable x) {
						log.error("loadCSVRegistration(" + url + ", lineNo " + lineNo + "): " + x.getClass().getName() + ": " + x.getMessage(), x);
					}
				}
			} finally {
				reader.close();
			}
		} catch (Throwable x) {
			log.error("loadCSVRegistration(" + url + ", lineNo " + lineNo + "): " + x.getClass().getName() + ": " + x.getMessage(), x);
		}
	}

	/**
	 * Keeps instances of {@link String} as key and {@link MimeTypeDetector} as value.
	 * <p>
	 * As soon as this registry is instantiated, this Map is populated. It might be additionally
	 * be populated from the outside via.
	 * </p>
	 */
	private Map detectorID2detector = new HashMap();

	private volatile List detectorSortedCache = null;
	private SortedSet detectorSorted = new TreeSet(new Comparator() {
		public int compare(Object o0, Object o1) {
			if (o0 == null)
				return -1;
			if (o1 == null)
				return 1;

			MimeTypeDetector d0 = (MimeTypeDetector) o0;
			MimeTypeDetector d1 = (MimeTypeDetector) o1;

			if (d0.getOrderHint() < d1.getOrderHint())
				return -1;
			else if (d0.getOrderHint() > d1.getOrderHint())
				return 1;
			else
				return d0.getDetectorID().compareTo(d1.getDetectorID());
		}
	});

	public synchronized void addMimeTypeDetector(MimeTypeDetector mimeTypeDetector)
	{
		if (mimeTypeDetector.getDetectorID() == null || mimeTypeDetector.getDetectorID().isEmpty())
			throw new IllegalArgumentException("mimeTypeDetector.getDetectorID() returned null or an empty String!");

		removeMimeTypeDetector(mimeTypeDetector);
		detectorID2detector.put(mimeTypeDetector.getDetectorID(), mimeTypeDetector);
		detectorSorted.add(mimeTypeDetector);
		detectorSortedCache = null;
	}

	public synchronized void removeMimeTypeDetector(MimeTypeDetector mimeTypeDetector)
	{
		String detectorID = mimeTypeDetector.getDetectorID();
		MimeTypeDetector removed = (MimeTypeDetector) detectorID2detector.remove(detectorID);
		detectorSorted.remove(removed);
		detectorSortedCache = null;
	}

	public List getMimeTypeDetectors()
	{
		List result = detectorSortedCache;
		if (result == null) {
			synchronized (this) {
				result = detectorSortedCache;
				if (result == null) { // DCL = double checked locking
					result = Collections.unmodifiableList(new ArrayList(detectorSorted));
					detectorSortedCache = result;
				}
			}
		}
		return result;
	}

	/**
	 * Try to determine the mime type(s) from the file name or the contents read from the {@link InputStream}
	 * (magic numbers).
	 *
	 * @param inputStream <code>null</code> or the stream to read the data from. It must support <code>mark</code> &amp; <code>reset</code> (see {@link InputStream#markSupported()}.
	 * @param fileName <code>null</code> or the simple name of the file (no path!).
	 * @param detectionStrategy how to proceed detection. If the detect strategy is not supported by your implementation or
	 *		it does not match the data specified (e.g. <code>randomAccessFile</code> is <code>null</code> but the strategy
	 *		is {@link DetectionStrategy#ONLY_CONTENT}) you should silently return and skip your checks.
	 * @return a collection into which you can add the mime types your implementation detected. It is also
	 *		possible to remove elements that another <code>MimeTypeDetector</code> implementation added before (if you can be
	 *		sure this result was wrong) and it is possible to take the previously detected mime-types into account for your own
	 *		check (for example, skip the check if a certain type is not in there).
	 */
	public DetectedMimeTypeSet detectMimeTypes(InputStream inputStream, String fileName, DetectionStrategy detectionStrategy)
	{
		return detectMimeTypes(null, inputStream, fileName, detectionStrategy);
	}

	private DetectedMimeTypeSet detectMimeTypes(RandomAccessFile randomAccessFile, InputStream inputStream, String fileName, DetectionStrategy detectionStrategy)
	{
		if (DetectionStrategy.ONLY_CONTENT == detectionStrategy)
			fileName = null;

		if (DetectionStrategy.ONLY_FILE_NAME == detectionStrategy) {
			randomAccessFile = null;
			inputStream = null;
		}

		DetectionContext detectionContext;
		if (inputStream != null)
			detectionContext = new DetectionContext(inputStream, fileName, detectionStrategy);
		else
			detectionContext = new DetectionContext(randomAccessFile, fileName, detectionStrategy);

		Collection detectors = getMimeTypeDetectors();
		for (Iterator it = detectors.iterator(); it.hasNext();) {
			MimeTypeDetector detector = (MimeTypeDetector) it.next();
			
			// @Steve: call MimeTypeDetectorInterceptor.preDetectMimeTypes(...) here? Marco.
			
			detector.detectMimeTypes(detectionContext);
			
			// @Steve: call MimeTypeDetectorInterceptor.postDetectMimeTypes(...) here? Marco.
			
			// @Steve: check a property like detectionContext.isDetectionAborted() here and break the loop??? Marco.
		}

		DetectedMimeTypeSet detectedMimeTypeSet = detectionContext.getDetectedMimeTypeSet();

		// The magic implementation currently returns MimeUtil.UNKNOWN_MIME_TYPE, which we don't really want
		// (the new policy is simply not to populate the DetectedMimeTypeSet). Hence, we filter it out.
		// I just checked and it seems this actually never happens (because we use other methods that return
		// List instances rather than a single String).
		// Still I think we should leave this code here (it's fast and guarantees we never have the
		// UNKNOWN_MIME_TYPE in the result - even if future extensions return it). Marco.
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes(MimeUtil.UNKNOWN_MIME_TYPE);
		if (!c.isEmpty()) {
			for (Iterator iterator = new ArrayList(c).iterator(); iterator.hasNext();) {
				DetectedMimeType detectedMimeType = (DetectedMimeType) iterator.next();
				detectedMimeTypeSet.removeDetectedMimeType(detectedMimeType);
			}
		}

		return detectedMimeTypeSet;
	}

	/**
	 * Try to determine the mime type(s) from the file name or the contents of the file (magic numbers).
	 *
	 * @param file <code>null</code> or the file to read the data from.
	 * @param fileName <code>null</code> or the simple name of the file (no path!).
	 * @param detectionStrategy how to proceed detection. If the detect strategy is not supported by your implementation or
	 *		it does not match the data specified (e.g. <code>randomAccessFile</code> is <code>null</code> but the strategy
	 *		is {@link DetectionStrategy#ONLY_CONTENT}) you should silently return and skip your checks.
	 * @return a collection into which you can add the mime types your implementation detected. It is also
	 *		possible to remove elements that another <code>MimeTypeDetector</code> implementation added before (if you can be
	 *		sure this result was wrong) and it is possible to take the previously detected mime-types into account for your own
	 *		check (for example, skip the check if a certain type is not in there).
	 */
	public DetectedMimeTypeSet detectMimeTypes(File file, String fileName, DetectionStrategy detectionStrategy)
	{
		if (DetectionStrategy.ONLY_FILE_NAME == detectionStrategy)
			return detectMimeTypes((RandomAccessFile)null, fileName, detectionStrategy);

		try {
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
			try {
				return detectMimeTypes(randomAccessFile, fileName, detectionStrategy);
			} finally {
				try {
					randomAccessFile.close();
				} catch (Throwable t) {
					log.warn(t.getClass().getName() + ": " + t.getMessage(), t);
				}
			}
		} catch (Throwable t) {
			log.error(t.getClass().getName() + ": " + t.getMessage(), t);
			return new DetectedMimeTypeSet(); // return empty result
		}
	}

	/**
	 * Try to determine the mime type(s) from the file name or the contents of the file (magic numbers).
	 *
	 * @param randomAccessFile <code>null</code> or the file to read the data from.
	 * @param fileName <code>null</code> or the simple name of the file (no path!).
	 * @param detectionStrategy how to proceed detection. If the detect strategy is not supported by your implementation or
	 *		it does not match the data specified (e.g. <code>randomAccessFile</code> is <code>null</code> but the strategy
	 *		is {@link DetectionStrategy#ONLY_CONTENT}) you should silently return and skip your checks.
	 * @return a collection into which you can add the mime types your implementation detected. It is also
	 *		possible to remove elements that another <code>MimeTypeDetector</code> implementation added before (if you can be
	 *		sure this result was wrong) and it is possible to take the previously detected mime-types into account for your own
	 *		check (for example, skip the check if a certain type is not in there).
	 */
	public DetectedMimeTypeSet detectMimeTypes(RandomAccessFile randomAccessFile, String fileName, DetectionStrategy detectionStrategy)
	{
		return detectMimeTypes(randomAccessFile, null, fileName, detectionStrategy);
	}

}
