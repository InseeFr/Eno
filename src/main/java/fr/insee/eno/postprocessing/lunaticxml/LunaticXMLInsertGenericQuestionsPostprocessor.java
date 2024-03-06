package fr.insee.eno.postprocessing.lunaticxml;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class LunaticXMLInsertGenericQuestionsPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLInsertGenericQuestionsPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_INSERT_GENERIC_QUESTIONS_LUNATIC_XML;

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 inputStream;){

			saxonService.transformLunaticXMLToLunaticXMLSimplePost(inputStream, byteArrayOutputStream, xslIS, parameters);

		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		return byteArrayOutputStream;
	}

	@Override
	public String toString() {
		return PostProcessing.LUNATIC_XML_INSERT_GENERIC_QUESTIONS.name();
	}

}
