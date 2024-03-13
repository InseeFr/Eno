package fr.insee.eno.postprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * No-op postprocessor.
 */
public class NoopPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(NoopPostprocessor.class);

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parametersFile, String survey) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(inputStream.readAllBytes());
		inputStream.close();
		logger.info("No-op postprocessor, simply returning input.");
		return byteArrayOutputStream;
	}

	@Override
	public String toString() {
		return "No-op postprocessor";
	}

}
