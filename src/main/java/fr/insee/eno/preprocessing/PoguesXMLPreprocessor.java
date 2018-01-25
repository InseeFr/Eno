package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A PoguesXML specific preprocessor.
 */
public class PoguesXMLPreprocessor implements Preprocessor {

	private static final Logger logger = LogManager.getLogger(PoguesXMLPreprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();
	
	@Override
	public File process(File inputFile, File parametersFile) throws Exception {
		logger.info("PoguesXMLPreprocessing Target : START");
		
		String outputPreprocess = null;

		InputStream parametersFileStream;
		// If no parameters file was provided : loading the default one
		// Else : using the provided one
		if (parametersFile == null) {
			logger.debug("Using default parameters");
			parametersFileStream = Constants.getInputStreamFromPath(Constants.PARAMETERS_FILE);			
		} else {
			logger.debug("Using provided parameters");
			parametersFileStream = FileUtils.openInputStream(parametersFile);
		}
		
		outputPreprocess = FilenameUtils.removeExtension(inputFile.getAbsolutePath()) + Constants.FINAL_EXTENSION;

		logger.debug("Titling : -Input : " + inputFile + " -Output : " + outputPreprocess + " -Stylesheet : "
				+ Constants.UTIL_POGUES_XML_SUPP_GOTO_XSL + " -Parameters : " + (parametersFile == null ? "Default parameters": "Provided parameters"));
		
		InputStream isInputFile =  FileUtils.openInputStream(inputFile);
		InputStream isUTIL_POGUES_XML_SUPP_GOTO_XSL = Constants.getInputStreamFromPath(Constants.UTIL_POGUES_XML_SUPP_GOTO_XSL);
		OutputStream osSUPP_GOTO = FileUtils.openOutputStream(new File(outputPreprocess));
		saxonService.transformTitling(
				isInputFile,
				isUTIL_POGUES_XML_SUPP_GOTO_XSL,
				osSUPP_GOTO,
				parametersFileStream);
		isInputFile.close();
		isUTIL_POGUES_XML_SUPP_GOTO_XSL.close();
		osSUPP_GOTO.close();
		parametersFileStream.close();
		
		logger.debug("PoguesXMLPreprocessing : END");
		return new File(outputPreprocess);
	}

}
