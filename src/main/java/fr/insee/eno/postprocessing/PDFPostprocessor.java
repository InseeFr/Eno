package fr.insee.eno.postprocessing;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * PDF postprocessor.
 */
public class PDFPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PDFPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, File parametersFile) throws Exception {
		// Identity
		return input;

	}

}
