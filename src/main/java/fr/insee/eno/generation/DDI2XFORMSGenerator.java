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

public class DDI2XFORMSGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2XFORMSGenerator.class);

	private XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		logger.info("DDI2XFORMS Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);
		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(surveyName);
		outputBasicFormPath = Constants.tEMP_XFORMS_FOLDER(sUB_TEMP_FOLDER) + "/" + formNameFolder + "/"
				+ Constants.BASIC_FORM_TMP_FILENAME;
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);

		
		try (
			InputStream isTRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL = Constants
				.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL);
			InputStream isFinalInput = FileUtils.openInputStream(finalInput);
			OutputStream osOutputBasicForm = FileUtils.openOutputStream(new File(outputBasicFormPath));){
			
			saxonService.transformDDI2XFORMS(isFinalInput, osOutputBasicForm, isTRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL,
					parameters);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the "+in2out()+" transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

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

	public String in2out() {
		return "ddi2xforms";
	}

}
