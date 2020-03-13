package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

@Deprecated
public class PDFSpecificTreatmentPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFSpecificTreatmentPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFOFile = new File(
				input.getPath().replace(Constants.EDIT_STRUCTURE_FO_EXTENSION, Constants.SPECIFIC_TREAT_PDF_EXTENSION));

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);

		InputStream FO_XSL = Constants
				.getInputStreamFromPath(sUB_TEMP_FOLDER + Constants.TRANSFORMATIONS_SPECIF_TREATMENT_FO_4PDF);

		if (FO_XSL == null) {
			FO_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_SPECIF_TREATMENT_FO_4PDF);
		}

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);
		saxonService.transformFOToStep2FO(inputStream, outputStream, FO_XSL);
		inputStream.close();
		outputStream.close();
		FO_XSL.close();
		logger.info("End of SpecificTreatment post-processing " + input.getAbsolutePath());

		return outputForFOFile;
	}

}
