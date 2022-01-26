package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.UglyXslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDITitlingPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDITitlingPreprocessor.class);

	private UglyXslTransformation saxonService = new UglyXslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_TITLING_XSL;

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");

		String outputTitling = null;
		String titlingInput = inputFile.getAbsolutePath();

		outputTitling = titlingInput.replace(Constants.CLEANED_EXTENSION, Constants.FINAL_EXTENSION);

		logger.debug("Titling : -Input : " + titlingInput + " -Output : " + outputTitling + " -Stylesheet : "
				+ styleSheetPath + " -Parameters : "
				+ (parametersFile == null ? "Default parameters" : "Provided parameters"));

		InputStream isCleaningTitling = FileUtils.openInputStream(new File(titlingInput));
		InputStream isUTIL_DDI_TITLING_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		OutputStream osTitling = FileUtils.openOutputStream(new File(outputTitling));

		try {
			saxonService.transformWithParameters(isCleaningTitling, isUTIL_DDI_TITLING_XSL, osTitling, parametersFile);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		isCleaningTitling.close();
		isUTIL_DDI_TITLING_XSL.close();
		osTitling.close();
		logger.debug("DDIPreprocessing titling: END");
		return new File(outputTitling);
	}

	public String toString() {
		return PreProcessing.DDI_TITLING.name();
	}

}
