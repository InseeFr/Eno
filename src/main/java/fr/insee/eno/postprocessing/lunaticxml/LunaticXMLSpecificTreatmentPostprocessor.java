package fr.insee.eno.postprocessing.lunaticxml;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;



public class LunaticXMLSpecificTreatmentPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLSpecificTreatmentPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, null, survey);
	}

	@Override
	public File process(File input, byte[] parametersFile, byte[] metadata, String survey) throws Exception {
		return this.process(input, parametersFile, metadata, null, survey);
	}

	@Override
	public File process(File input, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, String survey) throws Exception {

		File outputForLunaticXMLFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.SPECIFIC_TREAT_LUNATIC_XML_EXTENSION);
		logger.debug("Output folder for basic-form : " + outputForLunaticXMLFile.getAbsolutePath());

		InputStream specificTreatmentXslIS = null;

		if(specificTreatmentXsl!=null) {
			specificTreatmentXslIS = new ByteArrayInputStream(specificTreatmentXsl);
			InputStream inputStream = FileUtils.openInputStream(input);
			OutputStream outputStream = FileUtils.openOutputStream(outputForLunaticXMLFile);

			try {
				saxonService.transformSimplePost(inputStream, outputStream, specificTreatmentXslIS, parametersFile,"ddi2lunaticXML");
			}catch(Exception e) {
				String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
						toString(),
						e.getMessage(),
						Utils.getErrorLocation("specific file passed in params",e));
				logger.error(errorMessage);
				throw new EnoGenerationException(errorMessage);
			}
			inputStream.close();
			outputStream.close();
			specificTreatmentXslIS.close();

		}
		else {
			logger.info("Not specific treatment in params : simply copying this file" + input.getAbsolutePath());
			FileUtils.copyFile(input, outputForLunaticXMLFile);
		}
		logger.info("End of specific treatment post-processing " + outputForLunaticXMLFile.getAbsolutePath());

		return outputForLunaticXMLFile;
	}

	public String toString() {
		return PostProcessing.LUNATIC_XML_SPECIFIC_TREATMENT.name();
	}

}

