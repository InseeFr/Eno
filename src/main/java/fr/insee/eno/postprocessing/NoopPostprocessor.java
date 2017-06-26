package fr.insee.eno.postprocessing;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * No-op postprocessor.
 * */
public class NoopPostprocessor implements Postprocessor {
	
	private static final Logger logger = LogManager.getLogger(NoopPostprocessor.class);

	@Override
	public File process(File input) {
		logger.info("No-op postprocessor, simply returning generated file.");
		// Identity
		return input;
	}
	
	

}
