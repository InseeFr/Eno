package fr.insee.eno.params;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import fr.insee.eno.parameters.ENOParameters;

public interface ValorizatorParameters {

	/**
	 * 
	 * @param enoParameters
	 * @return
	 * @throws Exception
	 */
	ByteArrayOutputStream mergeParameters(ENOParameters enoParameters) throws Exception;
	
	/**
	 * 
	 * @param enoParameters
	 * @return
	 * @throws Exception
	 */
	ENOParameters mergeEnoParameters(ENOParameters enoParameters) throws Exception;

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	ENOParameters getParameters(InputStream inputStream) throws Exception;

	/**
	 * 
	 * @param enoParameters
	 * @return
	 * @throws Exception
	 */
	File mergeParameters(File enoParameters) throws Exception;
	
}
