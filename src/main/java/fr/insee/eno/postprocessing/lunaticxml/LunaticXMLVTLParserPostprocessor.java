package fr.insee.eno.postprocessing.lunaticxml;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.utils.Xpath2VTLParser;
/**
 * Customization of JS postprocessor.
 */
public class LunaticXMLVTLParserPostprocessor implements Postprocessor {



	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLVTLParserPostprocessor.class);

	public static final String XML_NODE_LABEL = "label";
	public static final String XML_NODE_CONDITIONFILTER = "conditionFilter";
	public static final String XML_NODE_VALUE = "value";
	public static final String XML_NODE_EXPRESSION = "expression";
	
	private String possibleNodes = "("+XML_NODE_LABEL+"|"+XML_NODE_CONDITIONFILTER+"|"+XML_NODE_VALUE+"|"+XML_NODE_EXPRESSION+")";

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputCustomFOFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.FINAL_LUNATIC_XML_EXTENSION);
		logger.info("Start JS parsing xpath to vtl post-processing");

		String inputString = FileUtils.readFileToString(input, StandardCharsets.UTF_8);
		try {
			FileUtils.writeStringToFile(outputCustomFOFile, Xpath2VTLParser.parseToVTLInNodes(inputString, possibleNodes), StandardCharsets.UTF_8);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s",
					toString(),
					e.getMessage());
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		logger.info("End JS parsing xpath to vtl post-processing");

		return outputCustomFOFile;
	}
	public String toString() {
		return PostProcessing.LUNATIC_XML_VTL_PARSER.name();
	}

}
