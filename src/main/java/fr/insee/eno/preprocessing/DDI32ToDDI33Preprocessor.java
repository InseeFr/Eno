package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDI32ToDDI33Preprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDI32ToDDI33Preprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDI32ToDDI33Preprocessing Target : START");

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);
		String output = FilenameUtils.removeExtension(inputFile.getAbsolutePath()) + Constants.DDI32_DDI33_EXTENSION;;
		
		logger.debug("DDI32ToDDI33 : -Input : " + inputFile + " -Output : " +output
				+ " -Stylesheet : " + Constants.UTIL_DDI32_TO_DDI33_XSL + " -Parameters : " + sUB_TEMP_FOLDER);

		InputStream isDDI32_TO_DDI33_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI32_TO_DDI33_XSL);
		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		OutputStream osDDI32DDI33 = FileUtils.openOutputStream(new File(output));
		
		try {
			saxonService.transform(isInputFile, isDDI32_TO_DDI33_XSL, osDDI32DDI33);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		
		isInputFile.close();
		isDDI32_TO_DDI33_XSL.close();
		osDDI32DDI33.close();
		
		logger.debug("DDI32ToDDI33Preprocessing : END");
		return new File(output);
	}
	
	public String toString() {
		return PreProcessing.DDI_32_TO_DDI_33.name();
	}

}
