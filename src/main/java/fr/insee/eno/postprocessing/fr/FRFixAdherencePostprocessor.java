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

public class FRFixAdherencePostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FRFixAdherencePostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFOFile = new File(
				input.getPath().replace(Constants.SPECIFIC_TREATMENT_FR_EXTENSION, Constants.FIX_ADHERENCE_FR_EXTENSION));
		System.out.println(input.getPath());
		String surveyName = survey;
		String formName = getFormName(input);
		
		InputStream FR_XSL = Constants.getInputStreamFromPath(Constants.UTIL_FR_FIX_ADHERENCE_XSL);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);

		saxonService.transformSimple(inputStream, outputStream, FR_XSL);
		
		inputStream.close();
		outputStream.close();
		FR_XSL.close();
		logger.info("End of specific treatment post-processing " + input.getAbsolutePath());

		return outputForFOFile;
	}

	private String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}

}
