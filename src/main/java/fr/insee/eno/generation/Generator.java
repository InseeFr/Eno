package fr.insee.eno.generation;

import java.io.File;

/**
 * This interface describes the API of a generator which main function is to
 * generate a implementation questionnaire from a formal specification. The
 * standard use case is to produce an XForms questionnaire from a DDI
 * specification.
 */
public interface Generator {

	/**
	 * This method handles the preprocessing of an input file. TODO Exception is
	 * also weak, change to a more robust Exception
	 * 
	 * @param finalInput
	 *            The input file, previously transformed in the preprocessing step
	 * @param parameters
	 *            The parameters of the survey for which we generate a questionnaire
	 * @param surveyName
	 *            The name of the survey for which we generate a questionnaire
	 * @return the generated file
	 * @throws Exception
	 *             Generic exception
	 */
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception;

	/**
	 * This method return in2out implementation
	 * 
	 * @return the in2out implementation
	 */
	public String in2out() ;

}
