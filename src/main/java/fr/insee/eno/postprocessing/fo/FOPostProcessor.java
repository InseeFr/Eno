package fr.insee.eno.postprocessing.fo;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.transform.xsl.XslTransformation;

public class FOPostProcessor  {

	private static final Logger logger = LoggerFactory.getLogger(FOPostProcessor.class);

	private XslTransformation saxonService = new XslTransformation();

	public File process(File input, byte[] parameters, String survey, String styleSheetPath, String extension) throws Exception {

		
		File outputForFOFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				extension);
		logger.debug("Output folder for basic-form : " + outputForFOFile.getAbsolutePath());

		String surveyName = survey;
		String formName = getFormName(input);

		InputStream FO_XSL = Constants.getInputStreamFromPath(styleSheetPath);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);

		try {
			saxonService.transformFOToStep4FO(inputStream, outputStream, FO_XSL, surveyName, formName, parameters);
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
		logger.info("End of EditStructurePages post-processing " + outputForFOFile.getAbsolutePath());

		return outputForFOFile;
	}

	private static String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}


}
