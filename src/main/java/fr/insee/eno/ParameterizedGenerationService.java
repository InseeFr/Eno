package fr.insee.eno;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.params.exception.EnoParametersException;
import fr.insee.eno.params.pipeline.PipeLineGeneratorImpl;
import fr.insee.eno.params.pipeline.PipelineGenerator;
import fr.insee.eno.params.validation.SchemaValidator;
import fr.insee.eno.params.validation.SchemaValidatorImpl;
import fr.insee.eno.params.validation.ValidationMessage;
import fr.insee.eno.params.validation.Validator;
import fr.insee.eno.params.validation.ValidatorImpl;

/**
 * Orchestrates the whole generation process.
 */
public class ParameterizedGenerationService {	

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterizedGenerationService.class);

	private PipelineGenerator pipelineGenerator = new PipeLineGeneratorImpl();
	
	private ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();
	
	private Validator validator = new ValidatorImpl();
	
	private SchemaValidator schemaValidator = new SchemaValidatorImpl();

	public File generateQuestionnaire(File inputFile, ENOParameters params, InputStream metadata, InputStream specificTreatment) throws Exception{
		File output=null;
		Pipeline pipeline = params.getPipeline();

		ValidationMessage valid = validator.validate(params);
		if(valid.isValid()) {
			GenerationService generationService = pipelineGenerator.setPipeLine(pipeline);
			ByteArrayOutputStream paramsFinal = valorizatorParameters.mergeParameters(params);
			LOGGER.info("Setting paramaters to the pipeline.");
			generationService.setParameters(paramsFinal);
			LOGGER.info("Setting metadata to the pipeline.");
			generationService.setMetadata(metadata);
			LOGGER.info("Setting specific treamtment to the pipeline.");
			generationService.setSpecificTreatment(specificTreatment);
			String survey = params.getParameters().getCampagne();
			output = generationService.generateQuestionnaire(inputFile, survey);
			paramsFinal.close();
		}
		else {
			LOGGER.error(valid.getMessage());
			throw new EnoParametersException(valid.getMessage());
		}

		return output;

	}

	public File generateQuestionnaire(File inputFile, InputStream params, InputStream metadata, InputStream specificTreatment) throws Exception {
		LOGGER.info("Parameterized Generation of questionnaire -- STARTED --");
		File output=null;
		
		if(params!=null) {
			byte[] paramsBytes = IOUtils.toByteArray(params);
			
			LOGGER.info("First validation ...");
			ValidationMessage validSchema = schemaValidator.validate(new ByteArrayInputStream(paramsBytes));
			
			if(validSchema.isValid()) {
				LOGGER.info(validSchema.getMessage());
				LOGGER.info("Parameters reading ...");
				ENOParameters enoParameters = null;
				enoParameters = valorizatorParameters.getParameters(new ByteArrayInputStream(paramsBytes));
				LOGGER.info("Parameters read.");			

				LOGGER.info("Second validation ...");
				ValidationMessage valid = validator.validate(enoParameters);

				if(valid.isValid()) {
					LOGGER.info(valid.getMessage());
					Pipeline pipeline = enoParameters.getPipeline();
					GenerationService generationService = pipelineGenerator.setPipeLine(pipeline);
					ByteArrayOutputStream paramsFinal =  valorizatorParameters.mergeParameters(enoParameters);
					LOGGER.info("Setting paramaters to the pipeline.");
					generationService.setParameters(paramsFinal);
					LOGGER.info("Setting metadata to the pipeline.");
					generationService.setMetadata(metadata);
					LOGGER.info("Setting specific treamtment to the pipeline.");
					generationService.setSpecificTreatment(specificTreatment);
					String survey = enoParameters.getParameters().getCampagne();
					output = generationService.generateQuestionnaire(inputFile, survey);
					paramsFinal.close();
				}
				else {
					LOGGER.error(valid.getMessage());
					throw new EnoParametersException(valid.getMessage());
				}
			}
			else {
				LOGGER.error(validSchema.getMessage());
				throw new EnoParametersException(validSchema.getMessage());
			}
		}
		else {
			String error = getClass().getName() + " needs the parameters file.";
			LOGGER.error(error);
			throw new EnoParametersException(error);
		}
		

		LOGGER.info("Parameterized Generation of questionnaire -- FINISHED --");
		return output;

	}

	public File generateQuestionnaire(File inputFile, File params, File metadata, File specificTreatment)  throws Exception{
		File output = null;

		InputStream parametersIS = null;
		InputStream metadataIS = null;
		InputStream specificTreatmentIS = null;
		parametersIS = params!=null ? FileUtils.openInputStream(params):null;
		metadataIS = metadata!=null ? FileUtils.openInputStream(metadata):null;
		specificTreatmentIS = specificTreatment!=null ? FileUtils.openInputStream(specificTreatment):null;
		output = generateQuestionnaire(inputFile, parametersIS, metadataIS, specificTreatmentIS);

		if(parametersIS!=null) {parametersIS.close();};
		if(metadataIS!=null) {metadataIS.close();};
		if(specificTreatmentIS!=null) {specificTreatmentIS.close();};

		return output;

	}
}
