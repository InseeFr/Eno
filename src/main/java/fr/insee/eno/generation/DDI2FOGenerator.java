package fr.insee.eno.generation;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class DDI2FOGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2FOGenerator.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		logger.info("DDI2FO Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);

		outputBasicFormPath = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form";
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);

		String outputForm = outputBasicFormPath + "/form.fo";


		try (InputStream isTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL = Constants
				.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL);
			 InputStream isFinalInput = FileUtils.openInputStream(finalInput);
				OutputStream osOutputForm = FileUtils.openOutputStream(new File(outputForm));) {
			
			saxonService.transformDDI2FO(isFinalInput, osOutputForm, isTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL, parameters);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the "+in2out()+" transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

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

	public String in2out() {
		return "ddi2fo";
	}

}
