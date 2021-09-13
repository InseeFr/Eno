package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * A PDF post processing
 */
public class FOEditStructurePagesPostprocessor implements Postprocessor {

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_EDIT_STRUCTURE_PAGES_FO_4PDF;

	private FOPostProcessor foPostProcessor = new FOPostProcessor();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return foPostProcessor.process(input, parameters, survey, styleSheetPath, Constants.EDIT_STRUCTURE_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_EDIT_STRUCTURE_PAGES.name();
	}

}
