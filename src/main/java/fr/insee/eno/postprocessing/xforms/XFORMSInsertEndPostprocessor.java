package fr.insee.eno.postprocessing.xforms;

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

public class XFORMSInsertEndPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInsertEndPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSERT_END_XSL;

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, survey);
	}

	@Override
	public File process(File input, byte[] parameters, byte[] metadata, String survey) throws Exception {

		File outputForFRFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.INSERT_END_XFORMS_EXTENSION);

		logger.debug("Output folder for basic-form : " + outputForFRFile.getAbsolutePath());

		InputStream FO_XSL = Constants.getInputStreamFromPath(styleSheetPath);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFRFile);

		try {
			saxonService.transformWithMetadata(inputStream, outputStream, FO_XSL, parameters, metadata);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		inputStream.close();
		outputStream.close();
		FO_XSL.close();
		logger.info("End of insert end post-processing " + outputForFRFile.getAbsolutePath());

		return outputForFRFile;
	}

	@Override
	public String toString() {
		return PostProcessing.XFORMS_INSERT_END.name();
	}

}
