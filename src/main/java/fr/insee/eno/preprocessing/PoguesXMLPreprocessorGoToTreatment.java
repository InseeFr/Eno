package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.UglyXslTransformation;

/**
 * A PoguesXML specific preprocessor : other goto2ite (rc version).
 */
public class PoguesXMLPreprocessorGoToTreatment implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PoguesXMLPreprocessorGoToTreatment.class);

	private UglyXslTransformation saxonService = new UglyXslTransformation();

	private static final String styleSheetPath = Constants.UTIL_POGUES_XML_GOTO_ITE_XSL;

	@Override
	public File process(File inputFile, byte[] parametersFile, String surveyName, String in2out) throws Exception {

		logger.info("PoguesXMLPreprocessing Target : START");

		String outputPreprocessGOT2ITE = null;

		outputPreprocessGOT2ITE = FilenameUtils.removeExtension(inputFile.getAbsolutePath())
				+ Constants.TEMP_EXTENSION;

		logger.debug("GOTO 2 ITE : -Input : " + inputFile + " -Output : " + outputPreprocessGOT2ITE + " -Stylesheet : "
				+ styleSheetPath + " -Parameters : "
				+ (parametersFile == null ? "Default parameters" : "Provided parameters"));

		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		InputStream isUTIL_POGUES_XML_GOTO_ITE_XSL = Constants
				.getInputStreamFromPath(styleSheetPath);
		OutputStream osGOTO2ITE = FileUtils.openOutputStream(new File(outputPreprocessGOT2ITE));

		try {
			saxonService.transform(isInputFile, isUTIL_POGUES_XML_GOTO_ITE_XSL, osGOTO2ITE);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		
		isInputFile.close();
		isUTIL_POGUES_XML_GOTO_ITE_XSL.close();
		osGOTO2ITE.close();


		logger.debug("PoguesXMLPreprocessing : END");
		return new File(outputPreprocessGOT2ITE);

	}

	public String toString() {
		return PreProcessing.POGUES_XML_GOTO_2_ITE.name();
	}


}
