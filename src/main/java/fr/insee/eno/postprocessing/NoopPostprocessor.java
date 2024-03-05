package fr.insee.eno.postprocessing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import fr.insee.eno.parameters.PostProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No-op postprocessor.
 */
public class NoopPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(NoopPostprocessor.class);

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parametersFile, String survey) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(byteArrayInputStream.readAllBytes());
		byteArrayInputStream.close();
		logger.info("No-op postprocessor, simply returning input.");
		return byteArrayOutputStream;
	}

	@Override
	public String toString() {
		return "No-op postprocessor";
	}

}
