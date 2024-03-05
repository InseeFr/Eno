package fr.insee.eno.generation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;


import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDI2PoguesXMLGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2PoguesXMLGenerator.class);

	@Override
	public ByteArrayOutputStream generate(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String surveyName) throws Exception {
		logger.warn("Functionnality not yet implemented !");
		return null;
	}

	public String in2out() {
		return "xml-pogues2ddi";
	}
}
