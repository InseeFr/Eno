package fr.insee.eno.postprocessing.lunaticxml;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

/**
 * Customization of JS postprocessor.
 */
public class LunaticXMLPaginationPostprocessor extends LunaticXMLPostProcessor {

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_PAGINATION_LUNATIC_XML;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {
		return this.process(input, parameters, surveyName,  styleSheetPath, Constants.PAGINATION_LUNATIC_XML_EXTENSION);
		
	}

}
