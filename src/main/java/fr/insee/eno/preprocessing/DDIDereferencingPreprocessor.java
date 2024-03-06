package fr.insee.eno.preprocessing;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;
import fr.insee.eno.utils.FolderCleaner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

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

		// UTIL_DDI_DEREFERENCING_XSL produced n files inside "output-folder" and there is no output...
		// We have to retrieve generated files to put them inside ByteArrayOutputStream and delete files
		File tempFolderWhereAreDDI = Constants.createTempEnoFolder();

		InputStream isDDI_DEREFERENCING_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try(inputFile;
			isDDI_DEREFERENCING_XSL;
			byteArrayOutputStream) {
			saxonService.transformDereferencing(inputFile, isDDI_DEREFERENCING_XSL, byteArrayOutputStream, tempFolderWhereAreDDI);
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
		File[] matchCleaningInput = tempFolderWhereAreDDI.listFiles((dir, name) -> !(name.startsWith("null")||name.contains("-modal")) && name.endsWith(".tmp"));

		String cleaningInput = null;

		logger.debug("Searching matching files in : " + tempFolderWhereAreDDI.getAbsolutePath());
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
		ByteArrayOutputStream finalOutputStream = new ByteArrayOutputStream();

		byte[] bytesDDI = FileUtils.readFileToByteArray(outputFile);
		finalOutputStream.write(bytesDDI);

		// Remove generated files
		FolderCleaner.cleanOneFolder(tempFolderWhereAreDDI);
		logger.debug("DDIPreprocessing Dereferencing : END");
		return finalOutputStream;
	}

	public String toString() {
		return PreProcessing.DDI_DEREFERENCING.name();
	}


}
