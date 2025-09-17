package fr.insee.eno.service;

import fr.insee.eno.exception.EnoParametersException;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.params.pipeline.PipeLineGeneratorImpl;
import fr.insee.eno.params.pipeline.PipelineGenerator;
import fr.insee.eno.params.validation.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Orchestrates the whole parameterized generation process.
 */
public class ParameterizedGenerationService {	

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterizedGenerationService.class);

	private PipelineGenerator pipelineGenerator;
	
	private ValorizatorParameters valorizatorParameters;
	
	private Validator validator;
	
	private SchemaValidator schemaValidator ;
	
	private String surveyName;

	public ParameterizedGenerationService() {
		this.pipelineGenerator = new PipeLineGeneratorImpl();
		this.valorizatorParameters = new ValorizatorParametersImpl();
		this.validator = new ValidatorImpl();
		this.schemaValidator = new SchemaValidatorImpl();
	}

	public ParameterizedGenerationService(String surveyName) {
		this.pipelineGenerator = new PipeLineGeneratorImpl();
		this.valorizatorParameters = new ValorizatorParametersImpl();
		this.validator = new ValidatorImpl();
		this.schemaValidator = new SchemaValidatorImpl();
		this.surveyName=surveyName;
	}

	/**
	 * It generates File using transformations defined in ENOParameters
	 * @param inputStream : the xml inputStream as ByteArrayInputStream (required)
	 * @param params : java object ENOParameter (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in XFORMSInseeModel (optional)
	 * @return the file resulting from the xslt transformations
	 * @throws Exception
	 */
	public ByteArrayOutputStream generateQuestionnaire(InputStream inputStream, ENOParameters params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception{
		ByteArrayOutputStream output=null;
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
			LOGGER.info("Setting mapping file to the pipeline.");
			generationService.setMapping(mapping);
			String survey = surveyName != null ? surveyName : params.getParameters() != null ? params.getParameters().getCampagne():"test";
			output = generationService.generateQuestionnaire(inputStream, survey);
			paramsFinal.close();
		}
		else {
			LOGGER.error(valid.getMessage());
			throw new EnoParametersException(valid.getMessage());
		}

		return output;

	}

	/**
	 * It generates File using transformations defined in ENOParameters
	 * @param inputStream : the xml inputStream as ByteArrayInputStream (required)
	 * @param params : InputStream of parameters xml file (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in FRModeleColtranePostProcessor (optional)
	 * @return the file resulting from the xslt transformations
	 * @throws Exception
	 */
	public ByteArrayOutputStream generateQuestionnaire(InputStream inputStream, InputStream params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception {
		LOGGER.info("Parameterized Generation of questionnaire -- STARTED --");
		ByteArrayOutputStream output=null;

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
				output = this.generateQuestionnaire(inputStream, enoParameters, metadata, specificTreatment, mapping);
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

}
