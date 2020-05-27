package fr.insee.eno.postprocessing.lunaticxml;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Customization of JS postprocessor.
 */
public class LunaticXMLUselessQuotePostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLUselessQuotePostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputForJSFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
						Constants.FINAL_LUNATIC_XML_EXTENSION);
		logger.debug("Output folder for basic-form : " + outputForJSFile.getAbsolutePath());

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForJSFile);

		InputStream JS_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_USELESS_QUOTE_TREATMENT_LUNATIC_XML);
		try {
			saxonService.transformLunaticXMLToLunaticXMLSimplePost(inputStream, outputStream, JS_XSL, parameters);
		} catch (Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. " + e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		inputStream.close();
		outputStream.close();
		JS_XSL.close();
		logger.info("End of Useless-quote-treatment post-processing.");

		return outputForJSFile;
	}

	@Override
	public String toString() {
		return PostProcessing.LUNATIC_XML_USELESS_QUOTE_TREATMENT.name();
	}
}

