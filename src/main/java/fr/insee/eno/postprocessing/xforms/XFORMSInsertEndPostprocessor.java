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

public class XFORMSInsertEndPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInsertEndPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSERT_END_XSL;

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, byte[] metadata, String survey) throws Exception {

		InputStream FO_XSL = Constants.getInputStreamFromPath(styleSheetPath);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try(byteArrayInputStream; FO_XSL) {
			saxonService.transformWithMetadata(byteArrayInputStream, outputStream, FO_XSL, parameters, metadata);
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
		return PostProcessing.XFORMS_INSERT_END.name();
	}

}
