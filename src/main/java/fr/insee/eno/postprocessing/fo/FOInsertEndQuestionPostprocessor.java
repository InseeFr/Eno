package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * A PDF post processing
 */
public class FOInsertEndQuestionPostprocessor implements Postprocessor {
	
	private FOPostProcessor foPostProcessor = new FOPostProcessor();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_END_QUESTION_FO_4PDF;

	public File process(File input, byte[] parameters, String survey) throws Exception {
		return foPostProcessor.process(input, parameters, survey, styleSheetPath, Constants.END_QUESTION_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_INSERT_END_QUESTION.name();
	}

}
