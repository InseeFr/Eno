package fr.insee.eno;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import fr.insee.eno.generation.Generator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.Preprocessor;
import fr.insee.eno.utils.FolderCleaner;

/**
 * Orchestrates the whole generation process.
 */
public class GenerationService {

	private static final Logger logger = LoggerFactory.getLogger(GenerationService.class);

	private final Preprocessor preprocessor;
	private final Generator generator;
	private final Postprocessor postprocessor;

	@Inject
	public GenerationService(Preprocessor preprocessor, Generator generator, Postprocessor postprocessor) {
		this.preprocessor = preprocessor;
		this.generator = generator;
		this.postprocessor = postprocessor;
	}

	/**
	 * Launch every step needed in order to generate the target questionnaire.
	 * 
	 * @param inputFile
	 *            The source file
	 * @param parametersFile
	 *            Custom parameters file, could be null
	 * 
	 * @return The generated file
	 * @throws Exception
	 *             bim
	 */
	// TODO finish implementation
	public File generateQuestionnaire(File inputFile, File parametersFile) throws Exception {
		logger.info("Generating questionnaire for: " + inputFile);
		logger.debug("Temp folder: "+ System.getProperty("java.io.tmpdir")); 
		
		cleanTempFolder();
		File preprocessResultFileName = this.preprocessor.process(inputFile, parametersFile);
		File generatedForm = this.generator.generate(preprocessResultFileName, "simpsons"); // FIXME
																							// get
																							// survey
																							// name
																							// dynamically
		File outputForm = this.postprocessor.process(generatedForm, parametersFile);
		logger.debug("Path to generated questionnaire: " + outputForm.getAbsolutePath());
		return outputForm;
	}

	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 *           
	 */
	public void cleanTempFolder() throws IOException {
		FolderCleaner cleanService = new FolderCleaner();
		if(Constants.TEMP_FOLDER_PATH !=null){
			File folderTemp = new File(Constants.TEMP_FOLDER_PATH);
			cleanService.cleanOneFolder(folderTemp);
		}
		else{
			logger.debug("Temp Folder is null");
		}
	}
	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 *           
	 */
	private void cleanTempFolder(File folder) throws IOException {
		FolderCleaner cleanService = new FolderCleaner();
		if(folder !=null){	
			cleanService.cleanOneFolder(folder);
		}
		else{
			logger.debug("Temp Folder is null");
		}
	}

}
