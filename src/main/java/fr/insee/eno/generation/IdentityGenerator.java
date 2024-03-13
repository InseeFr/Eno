package fr.insee.eno.generation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class IdentityGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(IdentityGenerator.class);

	@Override
	public ByteArrayOutputStream generate(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byteArrayOutputStream.write(inputStream.readAllBytes());
		inputStream.close();
		return byteArrayOutputStream;
	}

	public String in2out() {
		return "identity";
	}	
}
