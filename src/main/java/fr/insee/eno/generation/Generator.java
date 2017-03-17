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
	 * This method handles the preprocessing of an input file.
	 * TODO String is weak typing, we should use nio interfaces (Path, Files, etc.)
	 * TODO Exception is also weak, change to a more robust Exception
	 * 
	 * @param finalInput The input file, previously transformed in the preprocessing step
	 * @param surveyName The name of the survey for which we generate a questionnaire
	 * @throws Exception 
	 */
	public File generate(File finalInput, String surveyName) throws Exception;

}
