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
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI preprocessor to select only relevant components for the target output.
 */

public class DDIMultimodalSelectionPreprocessor implements Preprocessor {
	
	private static final Logger logger = LoggerFactory.getLogger(DDIMultimodalSelectionPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_MULTIMODAL_SELECTION_XSL;

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");

		String modalSelectionOutput=null;
		String multimodalInput = inputFile.getAbsolutePath();
		modalSelectionOutput = FilenameUtils.removeExtension(multimodalInput) + Constants.MULTIMODAL_EXTENSION;

		logger.debug("Modal DDI output file to be created : " + modalSelectionOutput);
		logger.debug("Multimodal Selection : -Input : " + multimodalInput + " -Output : " + modalSelectionOutput + " -Stylesheet : "
				+ styleSheetPath);

		InputStream isMultimodalIn = FileUtils.openInputStream(new File(multimodalInput));
		OutputStream osModalSelection = FileUtils.openOutputStream(new File(modalSelectionOutput));
		InputStream isUTIL_DDI_MULTIMODAL_SELECTION_XSL = Constants.getInputStreamFromPath(styleSheetPath);

		try {
			saxonService.transformCleaning(isMultimodalIn, isUTIL_DDI_MULTIMODAL_SELECTION_XSL, osModalSelection, in2out);
		}catch(Exception e) {
			String errorMessage = String.format("An error has occurred during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		isMultimodalIn.close();
		isUTIL_DDI_MULTIMODAL_SELECTION_XSL.close();
		osModalSelection.close();

		logger.debug("DDIPreprocessing Multimodal Selection: END");
		return new File(modalSelectionOutput);
	}

	public String toString() {
		return PreProcessing.DDI_MULTIMODAL_SELECTION.name();
	}

	
}
