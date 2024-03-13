package fr.insee.eno.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class DDI2PoguesXMLGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2PoguesXMLGenerator.class);

	@Override
	public ByteArrayOutputStream generate(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		logger.warn("Functionnality not yet implemented !");
		return null;
	}

	public String in2out() {
		return "xml-pogues2ddi";
	}
}
