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
	public ByteArrayOutputStream generate(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String surveyName) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(byteArrayInputStream.readAllBytes());
		byteArrayInputStream.close();
		return byteArrayOutputStream;
	}

	public String in2out() {
		return "identity";
	}	
}
