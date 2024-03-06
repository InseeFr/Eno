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
 * Customization of FO postprocessor.
 */
public class FOMailingPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FOMailingPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2;

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 inputStream){

			saxonService.transformFOToStep1FO(inputStream, byteArrayOutputStream, xslIS);

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
		return PostProcessing.FO_MAILING.name();
	}

}
