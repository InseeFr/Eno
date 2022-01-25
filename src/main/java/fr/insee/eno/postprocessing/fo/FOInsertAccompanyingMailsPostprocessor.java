package fr.insee.eno.postprocessing.fo;

import java.io.File;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;


/**
 * A PDF post processing
 */
public class FOInsertAccompanyingMailsPostprocessor extends FOPostProcessor {

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, survey, Constants.TRANSFORMATIONS_ACCOMPANYING_MAILS_FO_4PDF, Constants.FINAL_PDF_EXTENSION);
	}
	
	public String toString() {
		return PostProcessing.FO_INSERT_ACCOMPANYING_MAILS.name();
	}

}
