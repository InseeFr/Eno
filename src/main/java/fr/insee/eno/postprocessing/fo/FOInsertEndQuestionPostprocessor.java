package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * A PDF post processing
 */
public class FOInsertEndQuestionPostprocessor extends FOPostProcessor {

	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, survey, Constants.TRANSFORMATIONS_END_QUESTION_FO_4PDF, Constants.END_QUESTION_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_INSERT_END_QUESTION.name();
	}

}
