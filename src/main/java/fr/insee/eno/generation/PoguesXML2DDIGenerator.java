package fr.insee.eno.generation;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class PoguesXML2DDIGenerator implements Generator {
	
	private static final Logger logger = LogManager.getLogger(PoguesXML2DDIGenerator.class);
	
	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, String surveyName) throws Exception {
		logger.info("PoguesXML2DDI Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);

		outputBasicFormPath = Constants.TEMP_DDI_FOLDER + "/" + formNameFolder + "/" + Constants.BASIC_FORM_TMP_FILENAME;
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		
		
		InputStream isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL);
		InputStream isPROPERTIES_FILE = Constants.getInputStreamFromPath(Constants.PROPERTIES_FILE_DDI);
		InputStream isPARAMETERS_FILE = Constants.getInputStreamFromPath(Constants.PARAMETERS_FILE);
		
		saxonService.transformPoguesXML2DDI(
				FileUtils.openInputStream(finalInput),
				FileUtils.openOutputStream(new File(outputBasicFormPath)),
				isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL,
				isPROPERTIES_FILE,
				isPARAMETERS_FILE);
		
		isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL.close();
		isPROPERTIES_FILE.close();
		isPARAMETERS_FILE.close();
		
		//String outputForm = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form/form.xml";
		
		return new File(outputBasicFormPath);
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
