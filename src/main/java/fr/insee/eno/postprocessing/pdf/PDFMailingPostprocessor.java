package fr.insee.eno.postprocessing.pdf;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Customization of FO postprocessor.
 */
@Service
public class PDFMailingPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFMailingPostprocessor.class);

	@Autowired
	private XslTransformation saxonService;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputForFOFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.MAILING_FO_EXTENSION);
		logger.debug("Output folder for basic-form : " + outputForFOFile.getAbsolutePath());
		
		InputStream FO_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2);

		saxonService.transformFOToStep1FO(FileUtils.openInputStream(input),
				FileUtils.openOutputStream(outputForFOFile), FO_XSL);
		FO_XSL.close();
		logger.info("End of Mailing post-processing : ");

		return outputForFOFile;
	}
	
	public String toString() {
		return PostProcessing.PDF_MAILING.name();
	}

}
