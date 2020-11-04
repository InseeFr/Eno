package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

public class PoguesXmlInsertFilterLoopIntoQuestionTree implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PoguesXMLPreprocessorGoToTreatment.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File inputFile, byte[] parametersFile, String surveyName, String in2out) throws Exception {

		logger.info("PoguesXMLPreprocessing Target : START");

		String outputPreprocessINSERTLOOPFILTERTREE = null;

		outputPreprocessINSERTLOOPFILTERTREE = FilenameUtils.removeExtension(inputFile.getAbsolutePath())
				+ Constants.TEMP_EXTENSION;

		logger.debug("INSERT LOOP FILTER TREE : -Input : " + inputFile + " -Output : " + outputPreprocessINSERTLOOPFILTERTREE + " -Stylesheet : "
				+ Constants.UTIL_POGUES_XML_LOOP_FILTER_INTO_QUESTION_TREE_XSL + " -Parameters : "
				+ (parametersFile == null ? "Default parameters" : "Provided parameters"));

		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		InputStream isUTIL_POGUES_XML_LOOP_FILTER_INTO_QUESTION_TREE_XSL = Constants
				.getInputStreamFromPath(Constants.UTIL_POGUES_XML_LOOP_FILTER_INTO_QUESTION_TREE_XSL);
		OutputStream osINSERTLOOPFILTERTREE = FileUtils.openOutputStream(new File(outputPreprocessINSERTLOOPFILTERTREE));

		try {
			saxonService.transform(isInputFile, isUTIL_POGUES_XML_LOOP_FILTER_INTO_QUESTION_TREE_XSL, osINSERTLOOPFILTERTREE);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		
		isInputFile.close();
		isUTIL_POGUES_XML_LOOP_FILTER_INTO_QUESTION_TREE_XSL.close();
		osINSERTLOOPFILTERTREE.close();


		logger.debug("PoguesXMLPreprocessing : END");
		return new File(outputPreprocessINSERTLOOPFILTERTREE);

	}

	public String toString() {
		return PreProcessing.POGUES_XML_INSERT_FILTER_LOOP_INTO_QUESTION_TREE.name();
	}

}
