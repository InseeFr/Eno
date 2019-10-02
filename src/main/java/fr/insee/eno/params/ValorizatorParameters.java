package fr.insee.eno.params;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import fr.insee.eno.parameters.ENOParameters;

public interface ValorizatorParameters {

	ByteArrayOutputStream mergeParameters(ENOParameters enoParameters) throws Exception;
	
	ENOParameters mergeEnoParameters(ENOParameters enoParameters) throws Exception;

	ENOParameters getParameters(StreamSource xmlStream) throws Exception;

	ENOParameters getParameters(String xmlString) throws Exception;

	ENOParameters getParameters(InputStream inputStream) throws Exception;

	File mergeParameters(File enoParameters) throws Exception;
	
}
