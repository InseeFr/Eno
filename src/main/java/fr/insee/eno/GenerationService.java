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
	private final Postprocessor[] postprocessors;

	@Inject
	public GenerationService(final Preprocessor preprocessor, final Generator generator, final Postprocessor[] postprocessors) {
		this.preprocessor = preprocessor;
		this.generator = generator;
		this.postprocessors = postprocessors;
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
	public File generateQuestionnaire(File inputFile, File parametersFile, String surveyName) throws Exception {
		logger.info("Generating questionnaire for: " + surveyName);
		
		String tempFolder = System.getProperty("java.io.tmpdir")+ "/"+surveyName;
		logger.debug("Temp folder: "+ tempFolder); 
		
		cleanTempFolder(surveyName);
		File preprocessResultFileName = this.preprocessor.process(inputFile, parametersFile,surveyName);
		File generatedForm = this.generator.generate(preprocessResultFileName, surveyName); 
		
		//File generatedForm = new File("C:\\Users\\Tarik\\AppData\\Local\\Temp\\eno\\test\\instrument-i6vwid\\form\\form.fo");
		File outputForm = this.postprocessors[0].process(generatedForm, parametersFile, surveyName);
		for (int i = 1; i < postprocessors.length; i++) {
			outputForm = this.postprocessors[i].process(outputForm, parametersFile, surveyName);
		}
			
		logger.debug("Path to generated questionnaire: " + outputForm.getAbsolutePath());
		return outputForm;
	}

	
	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 *           
	 */
	public void cleanTempFolder(String name) throws IOException {
		FolderCleaner cleanService = new FolderCleaner();
		if(Constants.TEMP_FOLDER_PATH !=null){
			File folderTemp = new File(Constants.TEMP_FOLDER_PATH+"/"+name);
			cleanTempFolder(folderTemp);
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
	public void cleanTempFolder() throws IOException {
		if(Constants.TEMP_FOLDER_PATH !=null){
			File folderTemp = new File(Constants.TEMP_FOLDER_PATH);
			cleanTempFolder(folderTemp);
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
