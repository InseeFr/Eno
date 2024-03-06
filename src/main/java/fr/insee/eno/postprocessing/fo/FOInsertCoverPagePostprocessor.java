package fr.insee.eno.postprocessing.fo;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * A PDF post processing
 */
public class FOInsertCoverPagePostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOInsertCoverPagePostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_COVER_PAGE_FO_4PDF;

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 inputStream;){

			saxonService.transformFOToStep4FO(inputStream, byteArrayOutputStream, xslIS, surveyName, surveyName, parameters);

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

	private String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}

	public String toString() {
		return PostProcessing.FO_INSERT_COVER_PAGE.name();
	}

}
