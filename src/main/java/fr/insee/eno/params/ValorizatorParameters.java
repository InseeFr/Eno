package fr.insee.eno.params;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import fr.insee.eno.parameters.ENOParameters;

public interface ValorizatorParameters {

	/**
	 * 
	 * @param enoParameters
	 * @return ByteArrayOutputStream which represents xml parameter file
	 * @throws Exception
	 */
	ByteArrayOutputStream mergeParameters(ENOParameters enoParameters) throws Exception;
	
	/**
	 * 
	 * @param enoParameters java object
	 * @return ENOParameters object which is the result of the merging of default ENOParameters object and the param
	 * @throws Exception
	 */
	ENOParameters mergeEnoParameters(ENOParameters enoParameters) throws Exception;

	/**
	 * 
	 * @param inputStream which represents xml parameter file
	 * @return ENOParameters object which represents xml file according to xsd schema
	 * @throws Exception
	 */
	ENOParameters getParameters(InputStream inputStream) throws Exception;

	/**
	 * 
	 * @param enoParameters xml file
	 * @return File (xml) which is the result of the merging of default xml parameter file and the param
	 * @throws Exception
	 */
	File mergeParameters(File enoParameters) throws Exception;
	
}
