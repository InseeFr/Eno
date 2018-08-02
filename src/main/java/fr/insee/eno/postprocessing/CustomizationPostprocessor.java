package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Customization of FO postprocessor.
 */
public class CustomizationPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(CustomizationPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, File parametersFile, String survey) throws Exception {

		File outputCustomFOFile = new File(
				FilenameUtils.removeExtension(input.getPath()) + Constants.CUSTOM_FO_EXTENSION);

		InputStream FO_CUSTOMIZATION_XSL = Constants
				.getInputStreamFromPath(Constants.TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF);

		saxonService.transformFOToCustomFO(FileUtils.openInputStream(input), 
				FileUtils.openOutputStream(outputCustomFOFile),FO_CUSTOMIZATION_XSL);

		logger.info("end of Customization of fo file : " + input.getAbsolutePath());

		return outputCustomFOFile;
	}

}
