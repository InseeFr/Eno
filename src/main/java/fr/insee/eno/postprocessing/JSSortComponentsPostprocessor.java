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
 * Customization of JS postprocessor.
 */
public class JSSortComponentsPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(JSSortComponentsPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputCustomFOFile = new File(
				FilenameUtils.removeExtension(input.getPath()) + Constants.SORT_COMPONENTS_JS_EXTENSION);
		InputStream JS_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_SORT_COMPONENTS_JS);

		saxonService.transformJSToJSPost(FileUtils.openInputStream(input),
				FileUtils.openOutputStream(outputCustomFOFile), JS_XSL);
		JS_XSL.close();
		logger.info("End JS sort component post-processing");

		return outputCustomFOFile;
	}

}
