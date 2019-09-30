package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
@Service
public class DDIDereferencingPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIDereferencingPreprocessor.class);

	@Autowired
	private XslTransformation saxonService;

	@Override
	public File process(File inputFile, byte[] parametersFile, String survey, String in2out) throws Exception {
		logger.info("DDIPreprocessing Target : START");

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);
		// ----- Dereferencing
		logger.debug("Dereferencing : -Input : " + inputFile + " -Output : " + Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER)
				+ " -Stylesheet : " + Constants.UTIL_DDI_DEREFERENCING_XSL + " -Parameters : " + sUB_TEMP_FOLDER);

		InputStream isDDI_DEREFERENCING_XSL = Constants.getInputStreamFromPath(Constants.DDI_DEREFERENCING_XSL);
		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		OutputStream osTEMP_NULL_TMP = FileUtils.openOutputStream(Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER));
		saxonService.transformDereferencing(isInputFile, isDDI_DEREFERENCING_XSL, osTEMP_NULL_TMP,
				Constants.sUB_TEMP_FOLDER_FILE(survey)); // FIXME 4th param
															// should be a
															// parameters file
															// (?!!?).
		isInputFile.close();
		isDDI_DEREFERENCING_XSL.close();
		osTEMP_NULL_TMP.close();
		// ----- Cleaning
		logger.debug("Cleaning target");
		File f = Constants.sUB_TEMP_FOLDER_FILE(survey);
		File[] matchCleaningInput = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith("null");
			}
		});
		
		String cleaningInput = null;
		
		logger.debug("Searching matching files in : " + sUB_TEMP_FOLDER);
		for (File file : matchCleaningInput) {
			cleaningInput = file.getAbsolutePath();
			logger.debug("Found : " + cleaningInput);
		}
		
		logger.debug("DDIPreprocessing Dereferencing : END");
		return new File(cleaningInput);
	}
	
	public String toString() {
		return PreProcessing.DDI_DEREFERENCING.name();
	}


}
