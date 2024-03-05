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

public class XFORMSInsertGenericQuestionsPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInsertGenericQuestionsPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSERT_GENERIC_QUESTIONS_XSL;

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String surveyName) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 byteArrayInputStream;){

			saxonService.transformXformsToXformsSimplePost(byteArrayInputStream, byteArrayOutputStream, xslIS, parameters);

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
		return PostProcessing.XFORMS_INSERT_GENERIC_QUESTIONS.name();
	}

}
