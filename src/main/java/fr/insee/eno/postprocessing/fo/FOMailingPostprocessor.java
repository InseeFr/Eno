package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;

/**
 * Customization of FO postprocessor.
 */
public class FOMailingPostprocessor extends FOPostProcessor {

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, survey, Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2, Constants.MAILING_FO_EXTENSION);
	}

	public String toString() {
		return PostProcessing.FO_MAILING.name();
	}

}
