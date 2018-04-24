package fr.insee.eno.generation;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class DDI2PDFGenerator implements Generator {
	
	private static final Logger logger = LoggerFactory.getLogger(DDI2PDFGenerator.class);
	
	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File generate(File finalInput, String surveyName) throws Exception {
		logger.info("DDI2PDF Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);

		outputBasicFormPath = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form";
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		
		String outputForm = outputBasicFormPath + "/form.fo";
		InputStream isTRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL);
		InputStream isPROPERTIES_FILE = Constants.getInputStreamFromPath(Constants.PROPERTIES_FILE_PDF);
		InputStream isPARAMETERS_FILE = Constants.getInputStreamFromPath(Constants.PARAMETERS_FILE);
		
		saxonService.transformDDI2PDF(
				FileUtils.openInputStream(finalInput),
				FileUtils.openOutputStream(new File(outputForm)),
				isTRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL,
				isPROPERTIES_FILE,
				isPARAMETERS_FILE);
		
		isTRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL.close();
		isPROPERTIES_FILE.close();
		isPARAMETERS_FILE.close();
		
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
