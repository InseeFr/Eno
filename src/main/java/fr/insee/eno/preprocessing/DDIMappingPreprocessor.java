package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDIMappingPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIMappingPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");
		System.out.println(saxonService);

		String sUB_TEMP_FOLDER = Constants.tEMP_DDI_FOLDER(Constants.sUB_TEMP_FOLDER(survey));

		File mappingFile =Constants.tEMP_MAPPING_TMP(sUB_TEMP_FOLDER);
		// ----- Dereferencing
		logger.debug("Mapping : -Input : " + inputFile + " -Output : " + mappingFile
				+ " -Stylesheet : " + Constants.UTIL_DDI_DEREFERENCING_XSL + " -Parameters : " + sUB_TEMP_FOLDER);

		InputStream isDDI_MAPPING_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI_MAPPING_XSL);
		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		OutputStream osTEMP_MAPPING_TMP = FileUtils.openOutputStream(mappingFile);
		
		try {
			saxonService.transformMapping(isInputFile, isDDI_MAPPING_XSL, osTEMP_MAPPING_TMP,parametersFile);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		isInputFile.close();
		isDDI_MAPPING_XSL.close();
		osTEMP_MAPPING_TMP.close();

		return inputFile;

	}

	public String toString() {
		return PreProcessing.DDI_MAPPING.name();
	}


}
