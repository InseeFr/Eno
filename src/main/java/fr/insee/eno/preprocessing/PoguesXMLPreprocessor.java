package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Deprecated
 * See PoguesXMLPreprocessorGoToTreatment for new implementation
 * A PoguesXML specific preprocessor.
 */
@Deprecated
public class PoguesXMLPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PoguesXMLPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File inputFile, byte[] parametersFile, String surveyName, String in2out) throws Exception {
		logger.info("PoguesXMLPreprocessing Target : START");

		String outputPreprocessSuppGoto = null;

		outputPreprocessSuppGoto = FilenameUtils.removeExtension(inputFile.getAbsolutePath())
				+ Constants.TEMP_EXTENSION;

		logger.debug("Supp GOTO : -Input : " + inputFile + " -Output : " + outputPreprocessSuppGoto + " -Stylesheet : "
				+ Constants.UTIL_POGUES_XML_SUPP_GOTO_XSL + " -Parameters : "
				+ (parametersFile == null ? "Default parameters" : "Provided parameters"));

		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		InputStream isUTIL_POGUES_XML_SUPP_GOTO_XSL = Constants
				.getInputStreamFromPath(Constants.UTIL_POGUES_XML_SUPP_GOTO_XSL);
		OutputStream osSUPP_GOTO = FileUtils.openOutputStream(new File(outputPreprocessSuppGoto));
		saxonService.transform(isInputFile, isUTIL_POGUES_XML_SUPP_GOTO_XSL, osSUPP_GOTO);
		isInputFile.close();
		isUTIL_POGUES_XML_SUPP_GOTO_XSL.close();
		osSUPP_GOTO.close();

		String outputPreprocessMergeITE = null;

		outputPreprocessMergeITE = FilenameUtils.removeExtension(inputFile.getAbsolutePath())
				+ Constants.FINAL_EXTENSION;

		logger.debug("MERGE_ITE : -Input : " + outputPreprocessSuppGoto + " -Output : " + outputPreprocessMergeITE
				+ " -Stylesheet : " + Constants.UTIL_POGUES_XML_MERGE_ITE_XSL + " -Parameters : "
				+ (parametersFile == null ? "Default parameters" : "Provided parameters"));

		InputStream isSuppGoto = FileUtils.openInputStream(new File(outputPreprocessSuppGoto));
		InputStream isUTIL_POGUES_XML_MERGE_ITE_XSL = Constants
				.getInputStreamFromPath(Constants.UTIL_POGUES_XML_MERGE_ITE_XSL);
		OutputStream osMergeITE = FileUtils.openOutputStream(new File(outputPreprocessMergeITE));
		saxonService.transform(isSuppGoto, isUTIL_POGUES_XML_MERGE_ITE_XSL, osMergeITE);
		isSuppGoto.close();
		isUTIL_POGUES_XML_MERGE_ITE_XSL.close();
		osMergeITE.close();

		logger.debug("PoguesXMLPreprocessing : END");
		return new File(outputPreprocessMergeITE);
	}
}
