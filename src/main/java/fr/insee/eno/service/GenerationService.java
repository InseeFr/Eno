package fr.insee.eno.service;

import java.io.*;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;

import fr.insee.eno.Constants;
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
	private byte[] metadata;
	private byte[] specificTreatment;
	private byte[] mapping;
	
	private boolean cleaningFolder;

	@Inject
	public GenerationService(final Preprocessor[] preprocessors, final Generator generator,
			final Postprocessor[] postprocessors) {
		this.preprocessors = preprocessors;
		this.generator = generator;
		this.postprocessors = postprocessors;
		this.cleaningFolder = true;
	}

	@Inject
	public GenerationService(final Preprocessor preprocessor, final Generator generator,
			final Postprocessor[] postprocessors) {
		this.preprocessors = new Preprocessor[] { preprocessor };
		this.generator = generator;
		this.postprocessors = postprocessors;
		this.cleaningFolder = true;
	}

	@Inject
	public GenerationService(final Preprocessor preprocessor, final Generator generator,
			final Postprocessor postprocessor) {
		this.preprocessors = new Preprocessor[] { preprocessor };
		this.generator = generator;
		this.postprocessors = new Postprocessor[] { postprocessor };
		this.cleaningFolder = true;
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
	public ByteArrayOutputStream generateQuestionnaire(ByteArrayInputStream input, String surveyName) throws Exception {
		logger.info(this.toString());
		logger.info("Generating questionnaire for: " + surveyName);

		ByteArrayOutputStream outputStream = this.preprocessors[0].process(input, parameters, surveyName,generator.in2out());
		
		for (int i = 1; i < preprocessors.length; i++) {
			outputStream = this.preprocessors[i].process(new ByteArrayInputStream(outputStream.toByteArray()), parameters, surveyName,
					generator.in2out());
		}

		ByteArrayOutputStream generatedForm = this.generator.generate(new ByteArrayInputStream(outputStream.toByteArray()), parameters, surveyName);
		outputStream.close();
		ByteArrayOutputStream outputForm = this.postprocessors[0].process(new ByteArrayInputStream(generatedForm.toByteArray()), parameters, metadata, specificTreatment, mapping, surveyName);
		for (int i = 1; i < postprocessors.length; i++) {
			outputForm = this.postprocessors[i].process(new ByteArrayInputStream(outputForm.toByteArray()), parameters, metadata, specificTreatment, mapping,surveyName);
		}

		return outputForm;
	}
	
	public void setParameters(ByteArrayOutputStream parametersBAOS) {
		this.parameters = parametersBAOS.toByteArray();
	}	

	public void setParameters(InputStream parametersIS) throws IOException {
		if(parametersIS!=null) {
			this.parameters = IOUtils.toByteArray(parametersIS);
		}
	}
	
	public void setMetadata(InputStream metadataIS) throws IOException {
		if(metadataIS!=null) {
			this.metadata = IOUtils.toByteArray(metadataIS);
		}
	}
	
	public void setSpecificTreatment(InputStream specificTreatmentIS) throws IOException {
		if(specificTreatmentIS!=null) {
			this.specificTreatment = IOUtils.toByteArray(specificTreatmentIS);
		}
	}
	
	public void setMapping(InputStream mappingIS) throws IOException {
		if(mappingIS!=null) {
			this.mapping = IOUtils.toByteArray(mappingIS);
		}
	}

	public byte[] getParameters() {
		return parameters;
	}
	public byte[] getMetadata() {
		return metadata;
	}
	public byte[] getSpecificTreatment() {
		return specificTreatment;
	}
	public byte[] getMapping() {
		return mapping;
	}
	
	public void setCleaningFolder(boolean cleaning) {
		this.cleaningFolder = cleaning;
	}
	


	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 * 
	 */
	public void cleanTempFolder(String name) throws IOException {
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

	@Override
	public String toString() {
		return "GenerationService [preprocessors=" + Arrays.toString(preprocessors) + ", generator=" + generator.in2out()
				+ ", postprocessors=" + Arrays.toString(postprocessors) + "]";
	}
	
	

}
