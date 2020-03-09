package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * Customization of JS postprocessor.
 */
@Deprecated
public class JSExternalizeVariablesPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(JSExternalizeVariablesPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputCustomFOFile = new File(
				input.getPath().replace(Constants.SORT_COMPONENTS_JS_EXTENSION, Constants.EXTERNALIZE_VARIABLES_JS_EXTENSION));
		
		InputStream JS_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_EXTERNALIZE_VARIABLES_JS);
		
		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputCustomFOFile);

		saxonService.transformJSToJSPost(inputStream,outputStream, JS_XSL);
		inputStream.close();
		outputStream.close();
		JS_XSL.close();
		logger.info("End JS externalize codeLists post-processing");

		return outputCustomFOFile;
	}

}
