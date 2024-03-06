package fr.insee.eno.service;

import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.preprocessing.DDISplittingPreprocessor;
import fr.insee.eno.utils.FileArchiver;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class MultiModelService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultiModelService.class);

	private DDISplittingPreprocessor ddiSplitPreprocessor = new DDISplittingPreprocessor();

	/**
	 * It generates Zip file using parameterizedGenerationService
	 * @param inputFile : the xml input File which contains multiple ddi instrument in the same file (required)
	 * @param params : java object ENOParameter (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in XformsInseeModelPostProcessor (optional)
	 * @return the Zip file which contains all generated files
	 * @throws Exception
	 */
	public ByteArrayOutputStream generateQuestionnaire(ByteArrayInputStream inputFile, ENOParameters params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception{
		LOGGER.info("MultiModel Generation of questionnaire -- STARTED --");

		byte[] metadataBytes = metadata!=null ? IOUtils.toByteArray(metadata):null;
		byte[] specificTreatmentBytes = specificTreatment !=null ? IOUtils.toByteArray(specificTreatment):null;
		byte[] mappingBytes = mapping !=null ? IOUtils.toByteArray(mapping):null;

		String surveyName = params.getParameters()!=null?params.getParameters().getCampagne():"test";

		Map<String, ByteArrayOutputStream> ddiFilesOutputStream = ddiSplitPreprocessor.splitDDI(inputFile);

		ByteArrayOutputStream outputZipStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputZipStream);

		try {

			Map<String, ByteArrayInputStream> ddiFilesStream = new HashMap<>();
			ddiFilesOutputStream.forEach((key, outputStream) -> ddiFilesStream.put(key, new ByteArrayInputStream(outputStream.toByteArray())));

			for (Map.Entry<String, ByteArrayInputStream> entry : ddiFilesStream.entrySet()) {
				String modelName = entry.getKey();
				ByteArrayInputStream stream = entry.getValue();
				ByteArrayOutputStream outputStream = null;
				ParameterizedGenerationService parameterizedGenerationServiceThread = new ParameterizedGenerationService(modelName);
				outputStream = parameterizedGenerationServiceThread.generateQuestionnaire(
						stream,
						params,
						metadataBytes != null ? new ByteArrayInputStream(metadataBytes) : null,
						specificTreatmentBytes != null ? new ByteArrayInputStream(specificTreatmentBytes) : null,
						mappingBytes != null ? new ByteArrayInputStream(mappingBytes) : null);
				String fileName = NamingFileService.intoOutputZipFilename(surveyName, modelName, params);
				FileArchiver.writeToZipOS(fileName, outputStream, zipOutputStream);
			}
			zipOutputStream.close();
			LOGGER.info("Archive file initalized ! to");

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new EnoGenerationException("An error was occured during thread execution" + e.getMessage());
		}

		LOGGER.info("MultiModel Generation of questionnaire -- END --");
		return outputZipStream;

	}

	
	/**
	 * It generates a Zip file using parameterizedGenerationService
	 * @param inputFile : the xml input File which contains multiple ddi instrument in the same file (required)
	 * @param params : InputStream of parameters xml file (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in XFORMSInseeModelPostProcessor (optional)
	 * @return the Zip file which contains all generated files
	 * @throws Exception
	 */
	public ByteArrayOutputStream generateQuestionnaire(ByteArrayInputStream inputFile, InputStream params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception {
		LOGGER.info("MultiModel Generation of questionnaire -- STARTED --");
		ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();
		ENOParameters enoParameters = valorizatorParameters.getParameters(params);
		return this.generateQuestionnaire(inputFile, enoParameters, metadata, specificTreatment, mapping);

	}

}
