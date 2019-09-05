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

public class FRBrowsingPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FRBrowsingPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFRFile = new File(
				input.getPath().replace(Constants.INSERT_GENERIC_QUESTIONS_FR_EXTENSION, Constants.BROWSING_FR_EXTENSION));
		System.out.println(input.getPath());

		InputStream FO_XSL = Constants.getInputStreamFromPath(Constants.UTIL_FR_BROWSING_XSL);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFRFile);

		saxonService.transformBrowsingFr(inputStream, outputStream, FO_XSL);

		inputStream.close();
		outputStream.close();
		FO_XSL.close();
		logger.info("End of Browsing post-processing " + input.getAbsolutePath());

		return outputForFRFile;
	}


}
