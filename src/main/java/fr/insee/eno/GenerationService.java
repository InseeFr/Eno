package fr.insee.eno;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

	private final Preprocessor[] preprocessors;
	private final Generator generator;
	private final Postprocessor[] postprocessors;

	private byte[] parameters;

	@Inject
	public GenerationService(final Preprocessor[] preprocessors, final Generator generator,
			final Postprocessor[] postprocessors) {
		this.preprocessors = preprocessors;
		this.generator = generator;
		this.postprocessors = postprocessors;
	}

	@Inject
	public GenerationService(final Preprocessor preprocessor, final Generator generator,
			final Postprocessor[] postprocessors) {
		this.preprocessors = new Preprocessor[] { preprocessor };
		this.generator = generator;
		this.postprocessors = postprocessors;
	}

	@Inject
	public GenerationService(final Preprocessor preprocessor, final Generator generator,
			final Postprocessor postprocessor) {
		this.preprocessors = new Preprocessor[] { preprocessor };
		this.generator = generator;
		this.postprocessors = new Postprocessor[] { postprocessor };
	}

	/**
	 * Launch every step needed in order to generate the target questionnaire.
	 * 
	 * @param inputFile
	 *            The source file
	 * 
	 * @return The generated file
	 * @throws Exception
	 *             bim
	 */
	public File generateQuestionnaire(File inputFile, String surveyName) throws Exception {
		logger.info("Generating questionnaire for: " + surveyName);

		String tempFolder = System.getProperty("java.io.tmpdir") + "/" + surveyName;
		logger.debug("Temp folder: " + tempFolder);
		cleanTempFolder(surveyName);

		File preprocessResultFileName = this.preprocessors[0].process(inputFile, parameters, surveyName,
				generator.in2out());
		for (int i = 1; i < preprocessors.length; i++) {
			preprocessResultFileName = this.preprocessors[0].process(preprocessResultFileName, parameters, surveyName,
					generator.in2out());
		}

		File generatedForm = this.generator.generate(preprocessResultFileName, parameters, surveyName);

		File outputForm = this.postprocessors[0].process(generatedForm, parameters, surveyName);
		for (int i = 1; i < postprocessors.length; i++) {
			outputForm = this.postprocessors[i].process(outputForm, parameters, surveyName);
		}
		logger.debug("Path to generated questionnaire: " + outputForm.getAbsolutePath());

		return outputForm;
	}

	public void setParameters(InputStream parametersIS) throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int n = 0;
		while ((n = parametersIS.read(buf)) >= 0) {
			baos.write(buf, 0, n);
		}
		this.parameters = baos.toByteArray();

	}

	public byte[] getParameters() {
		return parameters;
	}

	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 * 
	 */
	public void cleanTempFolder(String name) throws IOException {
		FolderCleaner cleanService = new FolderCleaner();
		if (Constants.TEMP_FOLDER_PATH != null) {
			File folderTemp = new File(Constants.TEMP_FOLDER_PATH + "/" + name);
			cleanTempFolder(folderTemp);
		} else {
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
		if (Constants.TEMP_FOLDER_PATH != null) {
			File folderTemp = new File(Constants.TEMP_FOLDER_PATH);
			cleanTempFolder(folderTemp);
		} else {
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
		if (folder != null) {
			cleanService.cleanOneFolder(folder);
		} else {
			logger.debug("Temp Folder is null");
		}
	}

}
