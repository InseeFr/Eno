package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

@Deprecated
public class PDFInsertAccompanyingMailsPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFInsertAccompanyingMailsPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFOFile = new File(
				input.getPath().replace(Constants.COVER_PAGE_FO_EXTENSION, Constants.FINAL_PDF_EXTENSION));
		System.out.println(input.getPath());
		String surveyName = survey;
		String formName = getFormName(input);

		InputStream FO_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_ACCOMPANYING_MAILS_FO_4PDF);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);

		saxonService.transformFOToStep4FO(inputStream, outputStream, FO_XSL, surveyName, formName, parameters);

		inputStream.close();
		outputStream.close();
		FO_XSL.close();
		logger.info("End of InsertAccompanyingMails post-processing " + input.getAbsolutePath());

		return outputForFOFile;
	}

	private String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}

}
