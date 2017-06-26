package fr.insee.eno.generation;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;
import fr.insee.eno.Constants;

public class DDI2FRGenerator implements Generator {
	
	private static final Logger logger = LogManager.getLogger(DDI2FRGenerator.class);
	
	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, String surveyName) throws Exception {
		logger.info("DDI2FR Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);

		outputBasicFormPath = Constants.TEMP_XFORMS_FOLDER + "/" + formNameFolder + "/" + Constants.BASIC_FORM_TMP_FILENAME;
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		
		saxonService.transformDDI2FR(
				FileUtils.openInputStream(finalInput),
				FileUtils.openOutputStream(new File(outputBasicFormPath)),
				Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL,
				Constants.PROPERTIES_FILE,
				Constants.PARAMETERS_FILE);

		String outputForm = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form/form.xhtml";
		
		saxonService.transformBrowsing(
				FileUtils.openInputStream(new File(outputBasicFormPath)),
				FileUtils.openOutputStream(new File(outputForm)),
				Constants.BROWSING_TEMPLATE_XSL,
				Constants.LABEL_FOLDER);

		return new File(outputForm);
	}

	/**
	 * @param finalInput
	 * @return
	 */
	private String getFormNameFolder(File finalInput) {
		String formNameFolder;
		formNameFolder = FilenameUtils.getBaseName(finalInput.getAbsolutePath());
		formNameFolder = FilenameUtils.removeExtension(formNameFolder);
		formNameFolder = formNameFolder.replace(XslParameters.TITLED_EXTENSION, "");
		return formNameFolder;
	}

}
