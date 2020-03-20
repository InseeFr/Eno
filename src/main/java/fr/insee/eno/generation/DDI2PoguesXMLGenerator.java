package fr.insee.eno.generation;

import java.io.File;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDI2PoguesXMLGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2PoguesXMLGenerator.class);

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		logger.warn("Functionnality not yet implemented !");
		return null;
	}

	public String in2out() {
		return "xml-pogues2ddi";
	}
}
