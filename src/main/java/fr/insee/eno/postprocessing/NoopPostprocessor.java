package fr.insee.eno.postprocessing;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No-op postprocessor.
 */
public class NoopPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(NoopPostprocessor.class);

	@Override
	public File process(File input, byte[] parameters, String survey) {
		logger.info("No-op postprocessor, simply returning generated file.");
		// Identity
		return input;
	}

}
