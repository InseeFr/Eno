package fr.insee.eno.postprocessing.fr;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;

public class FRSpecificTreatmentPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(FRSpecificTreatmentPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {

		File outputForFOFile = new File(
				input.getPath().replace(Constants.INSERT_END_FR_EXTENSION, Constants.SPECIFIC_TREATMENT_FR_EXTENSION));
		System.out.println(input.getPath());
		String surveyName = survey;
		String formName = getFormName(input);
		
		//FIXME ajouter la verrue en parametre, si elle est nulle faire ça :
		InputStream VERRUE_XSL = Constants.getInputStreamFromPath(Constants.UTIL_FR_SPECIFIC_TREATMENT_XSL); 
		InputStream inputStream = FileUtils.openInputStream(input);
		OutputStream outputStream = FileUtils.openOutputStream(outputForFOFile);

		saxonService.transformSimple(inputStream, outputStream, VERRUE_XSL);
		
		inputStream.close();
		outputStream.close();
		VERRUE_XSL.close();
		logger.info("End of specific treatment post-processing " + input.getAbsolutePath());

		return outputForFOFile;
	}

	private String getFormName(File input) {
		return FilenameUtils.getBaseName(input.getParentFile().getParent());
	}

}
