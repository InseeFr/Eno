package fr.insee.eno.preprocessing;

import java.io.*;

import fr.insee.eno.exception.Utils;
import fr.insee.eno.utils.FolderCleaner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDIDereferencingPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIDereferencingPreprocessor.class);
	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_DEREFERENCING_XSL;

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);
		// ----- Dereferencing
		logger.debug("Dereferencing : -Input : " + inputFile + " -Output : " + Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER)
		+ " -Stylesheet : " + styleSheetPath + " -Parameters : " + sUB_TEMP_FOLDER);

		InputStream isDDI_DEREFERENCING_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try(inputFile;isDDI_DEREFERENCING_XSL; byteArrayOutputStream) {
			saxonService.transformDereferencing(inputFile, isDDI_DEREFERENCING_XSL, byteArrayOutputStream,
					Constants.SUB_TEMP_FOLDER_FILE(survey));
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		// ----- Cleaning
		logger.debug("Cleaning target");
		File f = Constants.SUB_TEMP_FOLDER_FILE(survey);
		File[] matchCleaningInput = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !(name.startsWith("null")||name.contains("-modal")) && name.endsWith(".tmp");
			}
		});

		String cleaningInput = null;

		logger.debug("Searching matching files in : " + sUB_TEMP_FOLDER);
		for (File file : matchCleaningInput) {
			if(!file.isDirectory()) {
				cleaningInput = file.getAbsolutePath();
				logger.debug("Found : " + cleaningInput);
			}
		}
		if(cleaningInput==null) {
			throw new EnoGenerationException("DDIDereferencing produced no file.");
		}

		File outputFile = new File(cleaningInput);
		ByteArrayOutputStream finalOutput = new ByteArrayOutputStream();
		byteArrayOutputStream.write(FileUtils.readFileToByteArray(outputFile));

		FolderCleaner.cleanOneFolder(f);

		logger.debug("DDIPreprocessing Dereferencing : END");
		return finalOutput;
	}

	public String toString() {
		return PreProcessing.DDI_DEREFERENCING.name();
	}


}
