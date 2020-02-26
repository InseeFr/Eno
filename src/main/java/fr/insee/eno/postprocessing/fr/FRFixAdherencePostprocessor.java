package fr.insee.eno.postprocessing.fr;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

public class FRFixAdherencePostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FRFixAdherencePostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {


		File outputForFRFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.FIX_ADHERENCE_FR_EXTENSION);

		logger.debug("Output folder for basic-form : " + outputForFRFile.getAbsolutePath());

		InputStream FR_XSL = Constants.getInputStreamFromPath(Constants.UTIL_FR_FIX_ADHERENCE_XSL);

		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFRFile);

		try {
			saxonService.transformSimple(inputStream, outputStream, FR_XSL);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		inputStream.close();
		outputStream.close();
		FR_XSL.close();
		logger.info("End of fix adherence treatment post-processing " + outputForFRFile.getAbsolutePath());

		return outputForFRFile;
	}

	@Override
	public String toString() {
		return PostProcessing.FR_FIX_ADHERENCE.name();
	}
}
