package fr.insee.eno;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Only for dev purposes.
 * */
public class DummyMain {
	
	private static final Logger logger = LogManager.getLogger(DummyMain.class);

	public static void main(String[] args) {
		logger.info("Starting generation program");
		cleanTempDirectory();		
		Injector injector = Guice.createInjector(new DDI2FRContext());
		GenerationService service = injector.getInstance(GenerationService.class);
		try {
			File generatedFile = service.generateQuestionnaire(
					new File("questionnaires/simpsons/ddi/simpsons.xml"),
					null);
			logger.info("Generation successful! >> " + generatedFile);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	// FIXME should the generation service handle the cleaning ?
	// Or should every client handle it ?
	// FIXME there is an existing class for that, FolderCleaner
	private static boolean cleanTempDirectory() {
		logger.debug("Cleaning temp directory at " + Constants.TEMP_FOLDER_PATH);
		File tempDir = new File(Constants.TEMP_FOLDER_PATH);
		if(!tempDir.exists()) {
			logger.debug("Temp dir does not exist.");
			return false;
		}
		try {
			FileUtils.cleanDirectory(tempDir);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return false;
		}
		
	}

}
