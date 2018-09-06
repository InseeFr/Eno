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

public class DDI2ODTGenerator implements Generator {
	
	private InputStream propertiesFiles;
	
	private static final Logger logger = LoggerFactory.getLogger(DDI2ODTGenerator.class);
	
	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, String surveyName) throws Exception {
		logger.info("DDI2ODT Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);

		outputBasicFormPath = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form";
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		
		String outputForm = outputBasicFormPath + "/form.odt";
		InputStream isTRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL);
		InputStream isPROPERTIES_FILE = this.getPropertiesFiles();
		InputStream isPARAMETERS_FILE = Constants.getInputStreamFromPath(Constants.PARAMETERS_FILE);
		
		InputStream isFinalInput = FileUtils.openInputStream(finalInput);
		OutputStream osOutputFile = FileUtils.openOutputStream(new File(outputForm));
		
		saxonService.transformDDI2ODT(
				isFinalInput,
				osOutputFile,
				isTRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL,
				isPROPERTIES_FILE,
				isPARAMETERS_FILE);
		
		isTRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL.close();
		isPROPERTIES_FILE.close();
		isPARAMETERS_FILE.close();
		isFinalInput.close();
		osOutputFile.close();
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
			isPROPERTIES_FILE = Constants.getInputStreamFromPath(Constants.CONFIG_DDI2ODT);
		}else{
			isPROPERTIES_FILE = propertiesFiles;
		}
		return isPROPERTIES_FILE;
	}
	public String in2out(){
		return "ddi2odt";
	}
}
