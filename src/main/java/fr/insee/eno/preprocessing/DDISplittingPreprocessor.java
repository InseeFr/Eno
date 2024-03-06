package fr.insee.eno.preprocessing;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.transform.xsl.XslTransformation;
import fr.insee.eno.utils.FolderCleaner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A DDI specific preprocessor.
 */
public class DDISplittingPreprocessor  {

	private static final Logger LOGGER = LoggerFactory.getLogger(DDISplittingPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_SPLITTING_XSL;

	public Map<String, ByteArrayOutputStream> splitDDI(InputStream inputFile) throws Exception {
		LOGGER.info("DDI splitting preprocessing Target : START");

		// UTIL_DDI_SPLITTING_XSL produced n files inside "output-folder" and there is no output...
		// We have to retrieve generated files to put them inside ByteArrayOutputStream and delete files
		File tempFolderWhereAreDDI = Constants.createTempEnoFolder();

		InputStream splitting_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		ByteArrayOutputStream emptyByteArrayOutputStream = new ByteArrayOutputStream();
		
		try(splitting_XSL; splitting_XSL) {
			saxonService.transformDereferencing(inputFile, splitting_XSL, emptyByteArrayOutputStream, tempFolderWhereAreDDI);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			LOGGER.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		emptyByteArrayOutputStream.close();

		// Transfomration produce N files, but none is inside outputStream, we have to build OutputStreamFrom File
		// ----- Cleaning
		LOGGER.debug("Cleaning target");
		File[] matchCleaningInput = tempFolderWhereAreDDI.listFiles((dir, name) -> !name.startsWith("null"));

		LOGGER.debug("Searching matching files in : " + tempFolderWhereAreDDI.getAbsolutePath());

		HashMap<String, ByteArrayOutputStream> outputStreamHashMap = new HashMap<>();
		for (File file : matchCleaningInput) {
			if(!file.isDirectory()) {
				String modelName = FilenameUtils.removeExtension(file.getName());
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				outputStream.write(FileUtils.readFileToByteArray(file));
				outputStreamHashMap.put(modelName,outputStream);
			}
		}
		if(outputStreamHashMap.isEmpty()) {
			throw new EnoGenerationException("DDI Splitting produced no file.");
		}
		// Remove generated files
		FolderCleaner.cleanOneFolder(tempFolderWhereAreDDI);
		LOGGER.debug("DDI splitting preprocessing : END");
		return outputStreamHashMap;
	}

	public String toString() {
		return "DDI splitting preprocessor";
	}


}
