package fr.insee.eno.postprocessing.lunaticxml;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

public class LunaticXMLInsertGenericQuestionsPostprocessor extends LunaticXMLPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLInsertGenericQuestionsPostprocessor.class);

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_INSERT_GENERIC_QUESTIONS_LUNATIC_XML;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {
		return this.process(input, parameters, surveyName,  styleSheetPath, Constants.INSERT_GENERIC_QUESTIONS_LUNATIC_XML_EXTENSION);
		
	}
	
	@Override
	public String toString() {
		return PostProcessing.LUNATIC_XML_INSERT_GENERIC_QUESTIONS.name();
	}

}
