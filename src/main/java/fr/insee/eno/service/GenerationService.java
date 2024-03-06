package fr.insee.eno.service;

import com.google.inject.Inject;
import fr.insee.eno.generation.Generator;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIMappingPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
	 * @param input
	 *            The source file
	 * 
	 * @return The generated file
	 * @throws Exception
	 *             bim
	 */
	public ByteArrayOutputStream generateQuestionnaire(ByteArrayInputStream input, String surveyName) throws Exception {
		logger.info(this.toString());
		logger.info("Generating questionnaire for: " + surveyName);

		// Pre-processing
		ByteArrayOutputStream outputStream = null;
		for (int i = 0; i < preprocessors.length; i++) {
			ByteArrayInputStream inputProcessor = i == 0 ? input : new ByteArrayInputStream(outputStream.toByteArray());
			if(this.preprocessors[i].getClass() == DDIMappingPreprocessor.class){
				ByteArrayOutputStream mappingOS = this.preprocessors[0].process(input, parameters, surveyName, generator.in2out());
				setMapping(new ByteArrayInputStream(mappingOS.toByteArray()));
				mappingOS.close();
			} else {
				outputStream = this.preprocessors[i].process(inputProcessor, parameters, surveyName, generator.in2out());
			}
		}

		// Core-processing
		outputStream = this.generator.generate(new ByteArrayInputStream(outputStream.toByteArray()), parameters, surveyName);

		// Post-processings
		for (int i = 0; i < postprocessors.length; i++) {
			outputStream = this.postprocessors[i].process(new ByteArrayInputStream(outputStream.toByteArray()), parameters, metadata, specificTreatment, mapping, surveyName);
		}
		return outputStream;
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

	@Override
	public String toString() {
		return "GenerationService [preprocessors=" + Arrays.toString(preprocessors) + ", generator=" + generator.in2out()
				+ ", postprocessors=" + Arrays.toString(postprocessors) + "]";
	}
	
	

}
