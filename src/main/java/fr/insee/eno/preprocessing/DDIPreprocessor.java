package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.transform.xsl.XslTransformation;
import fr.insee.eno.Constants;

/**
 * A DDI specific preprocessor.
 */
public class DDIPreprocessor implements Preprocessor {

	private static final Logger logger = LogManager.getLogger(DDIPreprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public String process(String inputFile, String parametersFile) throws Exception {
		logger.debug("DDIPreprocessing Target : START");

		logger.debug(
				"Dereferencing : -Input : " + inputFile + " -Output : " + Constants.TEMP_NULL_TMP + " -Stylesheet : "
						+ Constants.UTIL_DDI_DEREFERENCING_XSL + " -Parameters : " + Constants.TARGET_TEMP_FOLDER);
		saxonService.transformDereferencing(inputFile, Constants.UTIL_DDI_DEREFERENCING_XSL, Constants.TEMP_NULL_TMP,
				Constants.TARGET_TEMP_FOLDER);

		// CLEANING
		logger.debug("Cleaning target");
		File f = new File(Constants.TARGET_TEMP_FOLDER);
		File[] matchCleaningInput = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith("null");
			}
		});
		String cleaningInput = null;
		String cleaningOutput = null;

		logger.debug("Searching matching files in : " + Constants.TARGET_TEMP_FOLDER);
		for (File file : matchCleaningInput) {
			cleaningInput = file.getAbsolutePath();
			logger.debug("Found : " + cleaningInput);
		}

		cleaningOutput = FilenameUtils.removeExtension(cleaningInput) + Constants.CLEANED_EXTENSION;
		logger.debug("Cleaned output file to be created : " + cleaningOutput);
		logger.debug("Cleaning : -Input : " + cleaningInput + " -Output : " + cleaningOutput + " -Stylesheet : "
				+ Constants.UTIL_DDI_CLEANING_XSL);
		saxonService.transform(cleaningInput, Constants.UTIL_DDI_CLEANING_XSL, cleaningOutput);

		// TITLING
		// titlinginput = cleaningoutput

		String outputTitling = null;

		// If no parameters file was provided : loading the default one
		// Else : using the provided one
		if (parametersFile == null) {
			ClassLoader loader = DDIPreprocessor.class.getClassLoader();

			URL url = loader.getResource(Constants.PARAMETERS_FILE);
			parametersFile = url.toString();
		}

		logger.debug("Loading Parameters.xml located in : " + parametersFile);

		outputTitling = FilenameUtils.removeExtension(cleaningInput) + Constants.FINAL_EXTENSION;

		logger.debug("Titling : -Input : " + cleaningOutput + " -Output : " + outputTitling + " -Stylesheet : "
				+ Constants.UTIL_DDI_TITLING_XSL + " -Parameters : " + parametersFile);
		saxonService.transformTitling(cleaningOutput, Constants.UTIL_DDI_TITLING_XSL, outputTitling, parametersFile);

		logger.debug("DDIPreprocessing : END");
		return outputTitling;
	}

}
