package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Customization of FO postprocessor.
 */
@Deprecated
public class PDFMailingPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFMailingPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputForFOFile = new File(
				FilenameUtils.removeExtension(input.getPath()) + Constants.MAILING_FO_EXTENSION);
		InputStream FO_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2);

		saxonService.transformFOToStep1FO(FileUtils.openInputStream(input),
				FileUtils.openOutputStream(outputForFOFile), FO_XSL);
		FO_XSL.close();
		logger.info("End of Mailing post-processing : ");

		return outputForFOFile;
	}

}
