package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * Customization of FO postprocessor.
 */
public class FOMailingPostprocessor implements Postprocessor {

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2;
	
	private FOPostProcessor foPostProcessor = new FOPostProcessor();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return foPostProcessor.process(input, parameters, survey, styleSheetPath, Constants.MAILING_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_MAILING.name();
	}

}
