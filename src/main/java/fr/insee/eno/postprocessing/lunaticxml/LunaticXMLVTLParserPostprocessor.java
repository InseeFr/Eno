package fr.insee.eno.postprocessing.lunaticxml;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.utils.Xpath2VTLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Customization of JS postprocessor.
 */
public class LunaticXMLVTLParserPostprocessor implements Postprocessor {



	private static final Logger logger = LoggerFactory.getLogger(LunaticXMLVTLParserPostprocessor.class);

	public static final String XML_NODE_LABEL = "label";
	public static final String XML_NODE_CONDITIONFILTER = "conditionFilter";
	public static final String XML_NODE_VALUE = "value";
	public static final String XML_NODE_EXPRESSION = "expression";
	


	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		Path outputCustomFOFile = input.toPath().getParent().resolve(Constants.BASE_NAME_FORM_FILE+Constants.FINAL_LUNATIC_XML_EXTENSION);
		logger.info("Start JS parsing xpath to vtl post-processing");

		Xpath2VTLParser xpath2VTLParser = new Xpath2VTLParser(Set.of(XML_NODE_LABEL,
				XML_NODE_CONDITIONFILTER,
				XML_NODE_VALUE,
				XML_NODE_EXPRESSION), StandardCharsets.UTF_8);

		try {
			InputStream inputStream=new FileInputStream(input);
			xpath2VTLParser.parseXPathToVTLFromInputStreamInNodes(inputStream, Files.newOutputStream(outputCustomFOFile)).close();
			inputStream.close();
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s",
					toString(),
					e.getMessage());
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		logger.info("End JS parsing xpath to vtl post-processing");

		return outputCustomFOFile.toFile();
	}
	public String toString() {
		return PostProcessing.LUNATIC_XML_VTL_PARSER.name();
	}

}
