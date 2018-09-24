package fr.insee.eno.generation;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class DDI2FRGenerator implements Generator {
	
	private InputStream propertiesFiles;
	
	private static final Logger logger = LoggerFactory.getLogger(DDI2FRGenerator.class);
	
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
		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(surveyName);
		outputBasicFormPath = Constants.tEMP_XFORMS_FOLDER(sUB_TEMP_FOLDER) + "/" + formNameFolder + "/" + Constants.BASIC_FORM_TMP_FILENAME;
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		
		
		InputStream isTRANSFORMATIONS_DDI2FR_DDI2FR_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL);
		InputStream isPROPERTIES_FILE = this.getPropertiesFiles();
		InputStream isPARAMETERS_FILE = Constants.getInputStreamFromPath(Constants.PARAMETERS_FILE);
		
		InputStream isFinalInput = FileUtils.openInputStream(finalInput);
		OutputStream osOutputBasicForm = FileUtils.openOutputStream(new File(outputBasicFormPath));
		
		saxonService.transformDDI2FR(
				isFinalInput,
				osOutputBasicForm,
				isTRANSFORMATIONS_DDI2FR_DDI2FR_XSL,
				isPROPERTIES_FILE,
				isPARAMETERS_FILE);
		
		isTRANSFORMATIONS_DDI2FR_DDI2FR_XSL.close();
		isPROPERTIES_FILE.close();
		isPARAMETERS_FILE.close();
		isFinalInput.close();
		osOutputBasicForm.close();
		
		String outputForm = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form/form.xhtml";
		
		InputStream isOutputBasicFormPath = FileUtils.openInputStream(new File(outputBasicFormPath));
		OutputStream osOutputForm = FileUtils.openOutputStream(new File(outputForm));
		InputStream isBROWSING_TEMPLATE_XSL = Constants.getInputStreamFromPath(Constants.BROWSING_FR_TEMPLATE_XSL);
		saxonService.transformBrowsingDDI2FR(
				isOutputBasicFormPath,
				osOutputForm,
				isBROWSING_TEMPLATE_XSL,
				Constants.LABEL_FOLDER);
		isOutputBasicFormPath.close();
		osOutputForm.close();
		isBROWSING_TEMPLATE_XSL.close();
		
		
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

	public void setPropertiesFile(InputStream propertiesFiles) {
		this.propertiesFiles = propertiesFiles;
	}
	private InputStream getPropertiesFiles() {
		InputStream isPROPERTIES_FILE = null;
		if (propertiesFiles == null) {
			isPROPERTIES_FILE = Constants.getInputStreamFromPath(Constants.CONFIG_DDI2FR);
		}else{
			isPROPERTIES_FILE = propertiesFiles;
		}
		return isPROPERTIES_FILE;
	}
	
	public String in2out(){
		return "ddi2fr";
	}
	
}
