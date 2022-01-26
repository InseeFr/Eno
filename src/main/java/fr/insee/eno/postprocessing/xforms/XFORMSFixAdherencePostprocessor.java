package fr.insee.eno.postprocessing.xforms;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

public class XFORMSFixAdherencePostprocessor extends XFORMSPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSFixAdherencePostprocessor.class);

	private static final String styleSheetPath = Constants.UTIL_XFORMS_FIX_ADHERENCE_XSL;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {
		return this.process(input, parameters, surveyName,  styleSheetPath, Constants.FIX_ADHERENCE_XFORMS_EXTENSION);
	}
	
}
