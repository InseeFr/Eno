package fr.insee.eno.preprocessing;

import java.io.File;

/**
 * Operates a set of transformation to prepare the generation.
 */
public interface Preprocessor {

	/**
	 * This method handles the preprocessing of an input file. TODO Exception is
	 * also weak, change to a more robust Exception
	 * 
	 * @param inputFile
	 *            The file to preprocess
	 * @param parameters
	 *            An optional parameters file
	 * @param surveyName
	 *            An optional parameters file
	 * @return the preprocessed file
	 * @throws Exception
	 *             when it goes wrong
	 */
	public File process(File inputFile, byte[] parameters, String survey, String in2out) throws Exception;

	public String toString();
}
