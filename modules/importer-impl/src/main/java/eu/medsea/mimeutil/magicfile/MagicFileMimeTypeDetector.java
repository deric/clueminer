package eu.medsea.mimeutil.magicfile;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;

import eu.medsea.mimeutil.DetectionStrategy;
import eu.medsea.mimeutil.detector.AbstractMimeTypeDetector;
import eu.medsea.mimeutil.detector.DetectedMimeTypeSet;
import eu.medsea.mimeutil.detector.DetectionContext;

public class MagicFileMimeTypeDetector extends AbstractMimeTypeDetector
{
	public void detectMimeTypes(DetectionContext detectionContext) {
		if (detectionContext.getInputStream() != null) {
			detectMimeTypes(
					detectionContext.getInputStream(),
					detectionContext.getFileName(),
					detectionContext.getFileExtensions(),
					detectionContext.getDetectionStrategy(),
					detectionContext.getDetectedMimeTypeSet()
			);
		}
		else {
			detectMimeTypes(
					detectionContext.getRandomAccessFile(),
					detectionContext.getFileName(),
					detectionContext.getFileExtensions(),
					detectionContext.getDetectionStrategy(),
					detectionContext.getDetectedMimeTypeSet()
			);
		}
	}

	private void detectMimeTypes(
			InputStream inputStream, String fileName,
			String[] fileExtensions, DetectionStrategy detectionStrategy,
			DetectedMimeTypeSet detectedMimeTypeSet
	)
	{
		if (inputStream == null || DetectionStrategy.ONLY_FILE_NAME == detectionStrategy)
			return;

		List l = Helper.getMimeTypes(inputStream);
		applyMatchingMimeEntries(l, detectedMimeTypeSet);
	}

	private void detectMimeTypes(
			RandomAccessFile randomAccessFile,
			String fileName, String[] fileExtensions,
			DetectionStrategy detectionStrategy,
			DetectedMimeTypeSet detectedMimeTypeSet
	)
	{
		if (randomAccessFile == null || DetectionStrategy.ONLY_FILE_NAME == detectionStrategy)
			return;

		List l = Helper.getMagicMimeTypes(randomAccessFile);
		applyMatchingMimeEntries(l, detectedMimeTypeSet);
	}

	private void applyMatchingMimeEntries(List matchingMimeEntries, DetectedMimeTypeSet detectedMimeTypeSet)
	{
		double divisor = 1d;
		for (Iterator it = matchingMimeEntries.iterator(); it.hasNext();) {
			MatchingMagicMimeEntry matchingMagicMimeEntry = (MatchingMagicMimeEntry) it.next();

			double quality;
			do {
				quality = matchingMagicMimeEntry.getSpecificity() / divisor;
				if (quality > 1)
					divisor *= 10d;
			} while (quality > 1);
		}

		for (Iterator it = matchingMimeEntries.iterator(); it.hasNext();) {
			MatchingMagicMimeEntry matchingMagicMimeEntry = (MatchingMagicMimeEntry) it.next();
			double quality = matchingMagicMimeEntry.getSpecificity() / divisor;
			detectedMimeTypeSet.addDetectedMimeType(matchingMagicMimeEntry.getMimeType(), null, quality);
		}
	}

}
