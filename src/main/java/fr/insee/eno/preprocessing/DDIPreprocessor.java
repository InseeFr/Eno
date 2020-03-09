package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
@Deprecated
public class DDIPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);
		// ----- Dereferencing
		logger.debug("Dereferencing : -Input : " + inputFile + " -Output : " + Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER)
				+ " -Stylesheet : " + Constants.UTIL_DDI_DEREFERENCING_XSL + " -Parameters : " + sUB_TEMP_FOLDER);

		InputStream isDDI_DEREFERENCING_XSL = Constants.getInputStreamFromPath(Constants.DDI_DEREFERENCING_XSL);
		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		OutputStream osTEMP_NULL_TMP = FileUtils.openOutputStream(Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER));
		saxonService.transformDereferencing(isInputFile, isDDI_DEREFERENCING_XSL, osTEMP_NULL_TMP,
				Constants.sUB_TEMP_FOLDER_FILE(survey)); // FIXME 4th param
															// should be a
															// parameters file
															// (?!!?).
		isInputFile.close();
		isDDI_DEREFERENCING_XSL.close();
		osTEMP_NULL_TMP.close();
		// ----- Cleaning
		logger.debug("Cleaning target");
		File f = Constants.sUB_TEMP_FOLDER_FILE(survey);
		File[] matchCleaningInput = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith("null");
			}
		});
		String cleaningInput = null;
		String cleaningOutput = null;

		logger.debug("Searching matching files in : " + sUB_TEMP_FOLDER);
		for (File file : matchCleaningInput) {
			cleaningInput = file.getAbsolutePath();
			logger.debug("Found : " + cleaningInput);
		}

		cleaningOutput = FilenameUtils.removeExtension(cleaningInput) + Constants.CLEANED_EXTENSION;
		logger.debug("Cleaned output file to be created : " + cleaningOutput);
		logger.debug("Cleaning : -Input : " + cleaningInput + " -Output : " + cleaningOutput + " -Stylesheet : "
				+ Constants.UTIL_DDI_CLEANING_XSL);
		InputStream isCleaningIn = FileUtils.openInputStream(new File(cleaningInput));
		OutputStream osCleaning = FileUtils.openOutputStream(new File(cleaningOutput));
		InputStream isUTIL_DDI_CLEANING_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI_CLEANING_XSL);

		saxonService.transformCleaning(isCleaningIn, isUTIL_DDI_CLEANING_XSL, osCleaning, in2out);

		isCleaningIn.close();
		isUTIL_DDI_CLEANING_XSL.close();
		osCleaning.close();
		// ----- Titling
		// titlinginput = cleaningoutput

		String outputTitling = null;

		outputTitling = FilenameUtils.removeExtension(cleaningInput) + Constants.FINAL_EXTENSION;

		logger.debug("Titling : -Input : " + cleaningOutput + " -Output : " + outputTitling + " -Stylesheet : "
				+ Constants.UTIL_DDI_TITLING_XSL + " -Parameters : "
				+ (parametersFile == null ? "Default parameters" : "Provided parameters"));

		InputStream isCleaningTitling = FileUtils.openInputStream(new File(cleaningOutput));
		InputStream isUTIL_DDI_TITLING_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI_TITLING_XSL);
		OutputStream osTitling = FileUtils.openOutputStream(new File(outputTitling));
		saxonService.transformTitling(isCleaningTitling, isUTIL_DDI_TITLING_XSL, osTitling, parametersFile);
		isCleaningTitling.close();
		isUTIL_DDI_TITLING_XSL.close();
		osTitling.close();
		logger.debug("DDIPreprocessing : END");
		return new File(outputTitling);
	}

}
