package fr.insee.eno.postprocessing.xforms;

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

public class XFORMSSpecificTreatmentPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSSpecificTreatmentPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream input, byte[] parametersFile, byte[] metadata, String survey) throws Exception {
		return this.process(input, parametersFile, metadata, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, String survey) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		InputStream specificTreatmentXslIS = null;

		if(specificTreatmentXsl!=null) {
			specificTreatmentXslIS = new ByteArrayInputStream(specificTreatmentXsl);

			try (byteArrayInputStream){
				saxonService.transformWithFRSpecificTreatment(byteArrayInputStream, byteArrayOutputStream, specificTreatmentXslIS, parametersFile);

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
			byteArrayOutputStream.write(byteArrayInputStream.readAllBytes());
			byteArrayInputStream.close();
		}
		logger.info("End of specific treatment post-processing ");
		return byteArrayOutputStream;
	}

	@Override
	public String toString() {
		return PostProcessing.XFORMS_SPECIFIC_TREATMENT.name();
	}

}
