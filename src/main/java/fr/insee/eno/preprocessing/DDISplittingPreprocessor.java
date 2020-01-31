package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDISplittingPreprocessor  {

	private static final Logger LOGGER = LoggerFactory.getLogger(DDISplittingPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	public List<File> splitDDI(File inputFile, String survey) throws Exception {
		LOGGER.info("DDI splitting preprocessing Target : START");

		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(survey);
		// ----- Dereferencing
		LOGGER.debug("Dereferencing : -Input : " + inputFile + " -Output : " + Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER)
		+ " -Stylesheet : " + Constants.UTIL_DDI_SPLITTING_XSL + " -Parameters : " + sUB_TEMP_FOLDER);

		InputStream isDDI_DEREFERENCING_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI_SPLITTING_XSL);
		InputStream isInputFile = FileUtils.openInputStream(inputFile);
		OutputStream osTEMP_NULL_TMP = FileUtils.openOutputStream(Constants.tEMP_NULL_TMP(sUB_TEMP_FOLDER));
		
		try {
			saxonService.transformDereferencing(isInputFile, isDDI_DEREFERENCING_XSL, osTEMP_NULL_TMP,
					Constants.sUB_TEMP_FOLDER_FILE(survey));
		}catch(Exception e) {
			throw new EnoGenerationException("An error was occured during the " + toString() + " transformation. "+e.getMessage());
		}

		isInputFile.close();
		isDDI_DEREFERENCING_XSL.close();
		osTEMP_NULL_TMP.close();
		// ----- Cleaning
		LOGGER.debug("Cleaning target");
		File f = Constants.sUB_TEMP_FOLDER_FILE(survey);
		File[] matchCleaningInput = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith("null");
			}
		});

		List<File> outputFiles = new ArrayList<File>();

		LOGGER.debug("Searching matching files in : " + sUB_TEMP_FOLDER);
		for (File file : matchCleaningInput) {
			if(!file.isDirectory()) {
				LOGGER.debug("Found : " + file.getAbsolutePath());
				outputFiles.add(file);
			}
		}
		if(outputFiles.size()==0) {
			throw new EnoGenerationException("DDI Splitting produced no file.");
		}

		LOGGER.debug("DDI splitting preprocessing : END");
		return outputFiles;
	}

	public String toString() {
		return "DDI splitting preprocessor";
	}


}
