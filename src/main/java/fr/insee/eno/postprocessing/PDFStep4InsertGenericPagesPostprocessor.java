package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class PDFStep4InsertGenericPagesPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFStep4InsertGenericPagesPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, File parametersFile, String survey) throws Exception {

		File outputStep2FOFile = new File(
				input.getPath().replace(Constants.TABLE_COL_SIZE_PDF_EXTENSION, Constants.FINAL_PDF_EXTENSION));
		System.out.println(input.getPath());
		String surveyName = survey;
		String formName = getFormName(input);

		InputStream FO_STEP4_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_GENERIC_PAGES_FO_4PDF);

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);
		String parametersFileSurvey = sUB_TEMP_FOLDER + Constants.PARAMETERS_FILE;

		if (Constants.getInputStreamFromPath(parametersFileSurvey) == null) {
			parametersFileSurvey = Constants.PARAMETERS_FILE;
		}

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputStep2FOFile);

		saxonService.transformFOToStep4FO(inputStream, outputStream, FO_STEP4_XSL, surveyName, formName,
				Constants.CONFIG_DDI2PDF, parametersFileSurvey);

		inputStream.close();
		outputStream.close();
		FO_STEP4_XSL.close();
		logger.info("End of step 4 PDF post-processing " + input.getAbsolutePath());

		return outputStep2FOFile;
	}

	private String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}

}
