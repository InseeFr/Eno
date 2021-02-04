package fr.insee.eno.postprocessing.fo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Customization of FO postprocessor.
 */
public class FOMailingPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOMailingPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2;

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputForFOFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.MAILING_FO_EXTENSION);
		logger.debug("Output folder for basic-form : " + outputForFOFile.getAbsolutePath());	
		

		InputStream FO_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		
		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);
		try {
			saxonService.transformFOToStep1FO(inputStream, outputStream, FO_XSL);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		
		inputStream.close();
		outputStream.close();
		FO_XSL.close();
		logger.info("End of Mailing post-processing : ");

		return outputForFOFile;
	}

	public String toString() {
		return PostProcessing.FO_MAILING.name();
	}

}
