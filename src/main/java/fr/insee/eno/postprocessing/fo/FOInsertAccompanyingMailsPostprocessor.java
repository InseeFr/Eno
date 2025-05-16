package fr.insee.eno.postprocessing.fo;

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

/**
 * A PDF post-processing
 */
public class FOInsertAccompanyingMailsPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOInsertAccompanyingMailsPostprocessor.class);

	private final XslTransformation saxonService = new XslTransformation();

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		if (logger.isInfoEnabled()) // (otherwise don't compute the toLowerCase method above)
			logger.info("{} Target : START", this.toString().toLowerCase());

		String styleSheetPath = Constants.TRANSFORMATIONS_ACCOMPANYING_MAILS_FO_4PDF;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 inputStream){

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

	public String toString() {
		return PostProcessing.FO_INSERT_ACCOMPANYING_MAILS.name();
	}

}
