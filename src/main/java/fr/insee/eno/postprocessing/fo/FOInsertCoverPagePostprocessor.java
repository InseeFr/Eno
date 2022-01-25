package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

/**
 * A PDF post processing
 */
public class FOInsertCoverPagePostprocessor extends FOPostProcessor {

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, survey, Constants.TRANSFORMATIONS_COVER_PAGE_FO_4PDF, Constants.COVER_PAGE_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_INSERT_COVER_PAGE.name();
	}

}
