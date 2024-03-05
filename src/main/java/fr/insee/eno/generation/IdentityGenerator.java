package fr.insee.eno.generation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentityGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(IdentityGenerator.class);

	@Override
	public ByteArrayOutputStream generate(ByteArrayInputStream finalInput, byte[] parameters, String surveyName) throws Exception {
		return null;
	}

	public String in2out() {
		return "identity";
	}	
}
