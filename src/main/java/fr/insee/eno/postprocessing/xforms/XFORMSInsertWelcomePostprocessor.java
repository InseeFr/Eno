package fr.insee.eno.postprocessing.xforms;

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

public class XFORMSInsertWelcomePostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInsertWelcomePostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSERT_WELCOME_XSL;

	@Override
	public ByteArrayOutputStream process(InputStream input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(InputStream inputStream, byte[] parameters, byte[] metadata, String survey) throws Exception {

		InputStream FO_XSL = Constants.getInputStreamFromPath(styleSheetPath);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try(inputStream; FO_XSL) {
			saxonService.transformWithMetadata(inputStream, outputStream, FO_XSL, parameters, metadata);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		FO_XSL.close();
		logger.info("End of EditPatron post-processing");
		return outputStream;
	}

	@Override
	public String toString() {
		return PostProcessing.XFORMS_INSERT_WELCOME.name();
	}

}
