package fr.insee.eno.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * No-op preprocessor.
 */
public class NoopPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(NoopPreprocessor.class);

	@Override
	public File process(File inputFile, byte[] parameters, String survey, String in2out) throws Exception {
		logger.info("No-op preprocessor, simply returning generated file.");
		// Identity
		return inputFile;
	}

	@Override
	public String toString() {
		return "No-op preprocessor";
	}
}
