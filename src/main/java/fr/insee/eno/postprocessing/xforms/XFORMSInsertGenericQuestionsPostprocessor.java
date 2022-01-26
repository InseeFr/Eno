package fr.insee.eno.postprocessing.xforms;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

public class XFORMSInsertGenericQuestionsPostprocessor extends XFORMSPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInsertGenericQuestionsPostprocessor.class);


	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSERT_GENERIC_QUESTIONS_XSL;


	public File process(File input, byte[] parameters, String surveyName) throws Exception {
		return this.process(input, parameters, surveyName,  styleSheetPath, Constants.INSERT_GENERIC_QUESTIONS_LUNATIC_XML_EXTENSION);
		
	}
	
}
