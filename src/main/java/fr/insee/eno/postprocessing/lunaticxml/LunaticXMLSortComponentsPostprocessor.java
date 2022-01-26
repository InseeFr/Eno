package fr.insee.eno.postprocessing.lunaticxml;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

/**
 * Customization of JS postprocessor.
 */
public class LunaticXMLSortComponentsPostprocessor extends LunaticXMLPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLSortComponentsPostprocessor.class);

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_SORT_COMPONENTS_LUNATIC_XML;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {
		return this.process(input, parameters, surveyName,  styleSheetPath, Constants.SORT_COMPONENTS_LUNATIC_XML_EXTENSION);
		
	}

}
