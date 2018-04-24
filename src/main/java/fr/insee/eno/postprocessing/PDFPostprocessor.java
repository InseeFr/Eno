package fr.insee.eno.postprocessing;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.CalculatorService;

/**
 * PDF postprocessor.
 */
public class PDFPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFPostprocessor.class);

	// FIXME Inject !
	private static CalculatorService serviceTableColumnSize = new CalculatorService();

	@Override
	public File process(File input, File parametersFile) throws Exception {

		String outputForFO = FilenameUtils.removeExtension(input.getPath()) + Constants.FINAL_PDF_EXTENSION;

		serviceTableColumnSize.tableColumnSizeProcessor(input.getAbsolutePath(), outputForFO,
				Constants.PDF_PLUGIN_XML_CONF);

		// Identity
		return new File(outputForFO);

	}

}
