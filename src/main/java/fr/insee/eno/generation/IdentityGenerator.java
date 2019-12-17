package fr.insee.eno.generation;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslParameters;

public class IdentityGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(IdentityGenerator.class);

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		logger.info("Identity generation : START");
		logger.info("Identity generation : simply copying input file in another file");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);
		outputBasicFormPath = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form";
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		String outputForm = outputBasicFormPath + "/form.xml";
		FileUtils.copyFile(finalInput, new File(outputForm));
		logger.info("Identity generation : END");
		return new File(outputForm);
	}
	
	private String getFormNameFolder(File finalInput) {
		String formNameFolder;
		formNameFolder = FilenameUtils.getBaseName(finalInput.getAbsolutePath());
		formNameFolder = FilenameUtils.removeExtension(formNameFolder);
		formNameFolder = formNameFolder.replace(XslParameters.TITLED_EXTENSION, "");
		return formNameFolder;
	}

	public String in2out() {
		return "identity";
	}	
}
