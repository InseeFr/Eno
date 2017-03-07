package fr.insee.eno.preprocessing;

/**
 * Operates a set of transformation to prepare the generation.
 */
public interface Preprocessor {

	/**
	 * This method handles the preprocessing of an input file.
	 * TODO String is weak typing, we should use nio interfaces (Path, Files, etc.)
	 * TODO Exception is also weak, change to a more robust Exception
	 * @throws Exception 
	 */
	public String process(String inputFile, String parametersFile) throws Exception;

}
