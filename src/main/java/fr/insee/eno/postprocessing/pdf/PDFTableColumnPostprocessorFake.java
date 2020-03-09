package fr.insee.eno.postprocessing.pdf;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * PDF postprocessor.
 */
public class PDFTableColumnPostprocessorFake implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFTableColumnPostprocessorFake.class);

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFOFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.TABLE_COL_SIZE_PDF_EXTENSION);
		logger.debug("Output folder for basic-form : " + outputForFOFile.getAbsolutePath());

		FileUtils.copyFile(input, outputForFOFile);

		logger.debug("End of TableColumn post-processing (Fake)");
		return outputForFOFile;

	}

	public String toString() {
		return PostProcessing.PDF_TABLE_COLUMN.name();
	}
}
