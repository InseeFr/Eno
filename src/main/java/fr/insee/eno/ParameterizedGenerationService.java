package fr.insee.eno;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.exception.EnoParametersException;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.Pipeline;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.params.pipeline.PipeLineGeneratorImpl;
import fr.insee.eno.params.pipeline.PipelineGenerator;
import fr.insee.eno.params.validation.SchemaValidator;
import fr.insee.eno.params.validation.SchemaValidatorImpl;
import fr.insee.eno.params.validation.ValidationMessage;
import fr.insee.eno.params.validation.Validator;
import fr.insee.eno.params.validation.ValidatorImpl;

/**
 * Orchestrates the whole parameterized generation process.
 */
public class ParameterizedGenerationService {	

	private static final Logger LOGGER = LoggerFactory.getLogger(ParameterizedGenerationService.class);

	private PipelineGenerator pipelineGenerator = new PipeLineGeneratorImpl();
	
	private ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();
	
	private Validator validator = new ValidatorImpl();
	
	private SchemaValidator schemaValidator = new SchemaValidatorImpl();

	/**
	 * It generates File using transformations defined in ENOParameters
	 * @param inputFile : the xml input File (required)
	 * @param params : java object ENOParameter (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in FRModeleColtranePostProcessor (optional)
	 * @return the file resulting from the xslt transformations
	 * @throws Exception
	 */
	public File generateQuestionnaire(File inputFile, ENOParameters params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception{
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
			LOGGER.info("Setting mapping file to the pipeline.");
			generationService.setMapping(mapping);
			String survey = params.getParameters()!=null?params.getParameters().getCampagne():"test";
			output = generationService.generateQuestionnaire(inputFile, survey);
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
	 * @param inputFile : the xml input File (required)
	 * @param params : InputStream of parameters xml file (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in FRModeleColtranePostProcessor (optional)
	 * @return the file resulting from the xslt transformations
	 * @throws Exception
	 */
	public File generateQuestionnaire(File inputFile, InputStream params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception {
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
					LOGGER.info("Setting mapping file to the pipeline.");
					generationService.setMapping(mapping);
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

	/**
	 * It generates File using transformations defined in ENOParameters
	 * @param inputFile : the xml input File (required)
	 * @param params : xml File of ENOParameter (required)
	 * @param metadata : xml File of metadata (optional)
	 * @param specificTreatment : xsl file of the xsl sheet (optional)
	 * @param mapping : a xml File using in FRModeleColtranePostProcessor (optional)
	 * @return the file resulting from the xslt transformations
	 * @throws Exception
	 */
	public File generateQuestionnaire(File inputFile, File params, File metadata, File specificTreatment, File mapping)  throws Exception{
		File output = null;

		InputStream parametersIS = null;
		InputStream metadataIS = null;
		InputStream specificTreatmentIS = null;
		InputStream mappingIS = null;
		parametersIS = params!=null ? FileUtils.openInputStream(params):null;
		metadataIS = metadata!=null ? FileUtils.openInputStream(metadata):null;
		specificTreatmentIS = specificTreatment!=null ? FileUtils.openInputStream(specificTreatment):null;
		mappingIS = mapping!=null ? FileUtils.openInputStream(mapping):null;
		output = generateQuestionnaire(inputFile, parametersIS, metadataIS, specificTreatmentIS, mappingIS);

		if(parametersIS!=null) {parametersIS.close();};
		if(metadataIS!=null) {metadataIS.close();};
		if(specificTreatmentIS!=null) {specificTreatmentIS.close();};
		if(mappingIS!=null) {mappingIS.close();};

		return output;

	}
}
