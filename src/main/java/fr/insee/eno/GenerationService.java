package fr.insee.eno;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import fr.insee.eno.generation.Generator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

/**
 * Orchestrates the whole generation process.
 */
public class GenerationService {
	
	private static final Logger logger = LogManager.getLogger(GenerationService.class);
	
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
	 * @return The generated file
	 * @throws Exception
	 */
	// TODO finish implementation
	public File generateQuestionnaire(String inputFileName, String parametersFileName) throws Exception {
		logger.info("Generating questionnaire:" + inputFileName);
		String preprocessResultFileName = this.preprocessor.process(inputFileName, parametersFileName);
		File outputForm = this.generator.generate(preprocessResultFileName, "simpsons");
		logger.debug("Path to generated questionnaire: "+ outputForm.getAbsolutePath());
		//postprocessing
		return outputForm;
	}

}
