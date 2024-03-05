package fr.insee.eno.postprocessing.fo;

import java.io.*;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A PDF post processing
 */
public class FOSpecificTreatmentPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOSpecificTreatmentPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, String survey) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		InputStream specificTreatmentXslIS = null;

		if(specificTreatmentXsl!=null) {
			specificTreatmentXslIS = new ByteArrayInputStream(specificTreatmentXsl);

			try (byteArrayInputStream){
				saxonService.transformWithPDFSpecificTreatment(byteArrayInputStream, byteArrayOutputStream, specificTreatmentXslIS, parametersFile);

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
			logger.info("Not specific treatment in params : simply return inputcopying this file");
			byteArrayOutputStream.write(byteArrayInputStream.readAllBytes());
			byteArrayInputStream.close();
		}
		return byteArrayOutputStream;
	}

	public String toString() {
		return PostProcessing.FO_SPECIFIC_TREATMENT.name();
	}

}
