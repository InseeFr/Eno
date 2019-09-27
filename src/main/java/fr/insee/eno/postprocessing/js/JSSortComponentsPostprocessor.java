package fr.insee.eno.postprocessing.js;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.Postprocessor;
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

		File outputForJSFile = new File(input.getParent(),"form"+Constants.SORT_COMPONENTS_JS_EXTENSION);
		logger.debug("Output folder for basic-form : " + outputForJSFile.getAbsolutePath());
		
		InputStream JS_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_SORT_COMPONENTS_JS);
		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForJSFile);
		
		saxonService.transformJSToJSPost(inputStream,outputStream, JS_XSL);
		
		inputStream.close();
		outputStream.close();
		JS_XSL.close();
		logger.info("End JS sort component post-processing");

		return outputForJSFile;
	}

}
