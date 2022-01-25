package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

/**
 * A PDF post processing
 */
public class FOEditStructurePagesPostprocessor extends FOPostProcessor {

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, survey, Constants.TRANSFORMATIONS_EDIT_STRUCTURE_PAGES_FO_4PDF, Constants.EDIT_STRUCTURE_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_EDIT_STRUCTURE_PAGES.name();
	}

}
