package eu.medsea.mimeutil.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.medsea.mimeutil.DetectionStrategy;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.AbstractMimeTypeDetector;
import eu.medsea.mimeutil.detector.DetectedMimeType;
import eu.medsea.mimeutil.detector.DetectedMimeTypeSet;
import eu.medsea.mimeutil.detector.DetectionContext;

public class TextMimeTypeDetector extends AbstractMimeTypeDetector
{
	private static Log log = LogFactory.getLog(TextMimeTypeDetector.class);

	private void detectMimeTypesFromFileName(String fileName, String[] fileExtensions, DetectedMimeTypeSet detectedMimeTypeSet)
	{
		if (fileExtensions != null && fileExtensions.length > 0 && "txt".equalsIgnoreCase(fileExtensions[0]))
			addTextPlain(detectedMimeTypeSet, 0.75d);
	}

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
		if (!isDetectionNecessary(detectedMimeTypeSet))
			return;

		detectMimeTypesFromFileName(fileName, fileExtensions, detectedMimeTypeSet);
		if (inputStream == null)
			return;

		// read the first 1024 bytes of what may be a text file.
		byte[] content = new byte[1024];

		try {
			inputStream.mark(1024);
			try {
				int offset = 0;
				while (true) {
					int bytesToRead = content.length - offset;
					if (bytesToRead <= 0)
						break;

					int bytesRead = inputStream.read(content, offset, bytesToRead);
					if (bytesRead < 0)
						break;

					offset += bytesRead;
				}
				if (offset < content.length) {
					byte[] tmp = new byte[offset];
					System.arraycopy(content, 0, tmp, 0, tmp.length);
					content = tmp;
				}
			} finally {
				inputStream.reset();
			}

			detectMimeTypesFromContent(content, detectedMimeTypeSet);
		} catch (IOException e) {
			log.warn(e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	private boolean isDetectionNecessary(DetectedMimeTypeSet detectedMimeTypeSet)
	{
		Collection c = detectedMimeTypeSet.getDetectedMimeTypes();
		if (c.isEmpty())
			return true;

		DetectedMimeType detectedMimeType = (DetectedMimeType) c.iterator().next();
		String detectedMimeTypeString = detectedMimeType.getMimeType();
		if (MimeUtil.UNKNOWN_MIME_TYPE.equals(detectedMimeTypeString) || MimeUtil.OCTET_STREAM_MIME_TYPE.equals(detectedMimeTypeString))
			return true;

		return false;
	}


	private void detectMimeTypes(
			RandomAccessFile randomAccessFile,
			String fileName, String[] fileExtensions,
			DetectionStrategy detectionStrategy,
			DetectedMimeTypeSet detectedMimeTypeSet
	)
	{
		if (!isDetectionNecessary(detectedMimeTypeSet))
			return;

		detectMimeTypesFromFileName(fileName, fileExtensions, detectedMimeTypeSet);
		if (randomAccessFile == null)
			return;

		// read the first 1024 bytes of what may be a text file.
		byte[] content = new byte[1024];

		try {
			randomAccessFile.seek(0);
			if (randomAccessFile.length() < 1024)
				content = new byte[(int)randomAccessFile.length()];
			else
				content = new byte[1024];

			randomAccessFile.readFully(content);

			detectMimeTypesFromContent(content, detectedMimeTypeSet);
		} catch (IOException e) {
			log.warn(e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	private static void addTextPlain(DetectedMimeTypeSet detectedMimeTypeSet, double quality)
	{
		if (detectedMimeTypeSet.containsMimeType("text/plain", quality))
			return;

		detectedMimeTypeSet.addDetectedMimeType(
				"text/plain",
				null,
				quality
		);
	}


	private static void detectMimeTypesFromContent(byte[] content, DetectedMimeTypeSet detectedMimeTypeSet)
	{
		if (content.length == 0) {
			detectedMimeTypeSet.addDetectedMimeType(
					"application/x-empty",
					null,
					1d
			);
			return;
		}

		// TODO we should check for all valid encodings - or at least for UTF-8 - right now, we only check for ASCII

		for (int i = 0; i < content.length; i++) {
			int b = content[i] & 0xff;
			if (b < 9) {
				return;
			}

			if (b > 175) {
				return;
			}
		}

		addTextPlain(detectedMimeTypeSet, 1d);
	}
}
