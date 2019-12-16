package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDICleaningPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDICleaningPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");

		String cleaningOutput=null;
		String cleaningInput = inputFile.getAbsolutePath();
		cleaningOutput = FilenameUtils.removeExtension(cleaningInput) + Constants.CLEANED_EXTENSION;

		logger.debug("Cleaned output file to be created : " + cleaningOutput);
		logger.debug("Cleaning : -Input : " + cleaningInput + " -Output : " + cleaningOutput + " -Stylesheet : "
				+ Constants.UTIL_DDI_CLEANING_XSL);

		InputStream isCleaningIn = FileUtils.openInputStream(new File(cleaningInput));
		OutputStream osCleaning = FileUtils.openOutputStream(new File(cleaningOutput));
		InputStream isUTIL_DDI_CLEANING_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI_CLEANING_XSL);

		try {
			saxonService.transformCleaning(isCleaningIn, isUTIL_DDI_CLEANING_XSL, osCleaning, in2out);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		isCleaningIn.close();
		isUTIL_DDI_CLEANING_XSL.close();
		osCleaning.close();

		logger.debug("DDIPreprocessing Cleaning: END");
		return new File(cleaningOutput);
	}

	public String toString() {
		return PreProcessing.DDI_CLEANING.name();
	}


}
