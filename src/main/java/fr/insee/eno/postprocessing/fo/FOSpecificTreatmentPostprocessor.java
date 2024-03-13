package fr.insee.eno.postprocessing.fo;

import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * A PDF post processing
 */
public class FOSpecificTreatmentPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOSpecificTreatmentPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public ByteArrayOutputStream process(InputStream input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, String survey) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		InputStream specificTreatmentXslIS = null;

		if(specificTreatmentXsl!=null) {
			specificTreatmentXslIS = new ByteArrayInputStream(specificTreatmentXsl);

			try (inputStream){
				saxonService.transformWithPDFSpecificTreatment(inputStream, byteArrayOutputStream, specificTreatmentXslIS, parametersFile);

			} catch(Exception e) {
				String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
						toString(),
						e.getMessage(),
						Utils.getErrorLocation("specific file passed in params",e));
				logger.error(errorMessage);
				throw new EnoGenerationException(errorMessage);
			}
		}
		else {
			logger.info("Not specific treatment in params : simply return input");
			byteArrayOutputStream.write(inputStream.readAllBytes());
			inputStream.close();
		}
		return byteArrayOutputStream;
	}

	public String toString() {
		return PostProcessing.FO_SPECIFIC_TREATMENT.name();
	}

}
