package fr.insee.eno.postprocessing.fr;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

public class FRInsertEndPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FRInsertEndPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFOFile = new File(
				input.getPath().replace(Constants.INSERT_WELCOME_FR_EXTENSION, Constants.INSERT_END_FR_EXTENSION));
		System.out.println(input.getPath());
		String surveyName = survey;
		String formName = getFormName(input);

		InputStream FO_XSL = Constants.getInputStreamFromPath(Constants.UTIL_FR_INSERT_END_XSL);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);
		InputStream metadoneesStream = FileUtils.openInputStream(null);//FIXME pettre en argument les metadonnees

		saxonService.transformWithMetadonnee(inputStream, outputStream, FO_XSL, parameters ,metadoneesStream);
		
		inputStream.close();
		outputStream.close();
		FO_XSL.close();
		metadoneesStream.close();
		logger.info("End of insert welcome post-processing " + input.getAbsolutePath());

		return outputForFOFile;
	}

	private String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}

}
