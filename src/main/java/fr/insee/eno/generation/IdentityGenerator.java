package fr.insee.eno.generation;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentityGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(IdentityGenerator.class);

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		logger.info("Indentity generation : START");
		logger.info("The output file is the input file.");
		return finalInput;
	}

	public String in2out() {
		return "identity";
	}
}
