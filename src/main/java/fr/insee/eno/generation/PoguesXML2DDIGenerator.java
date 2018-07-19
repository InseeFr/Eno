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

public class PoguesXML2DDIGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(PoguesXML2DDIGenerator.class);

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
		String sUB_TEMP_FOLDER = Constants.sUB_TEMP_FOLDER(surveyName);
		outputBasicFormPath = Constants.tEMP_DDI_FOLDER(sUB_TEMP_FOLDER) + "/" + formNameFolder + "/"
				+ Constants.BASIC_FORM_TMP_FILENAME;
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);

		InputStream isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL = Constants
				.getInputStreamFromPath(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL);
		InputStream isPROPERTIES_FILE = Constants.getInputStreamFromPath(Constants.CONFIG_POGUES_XML2DDI);
		InputStream isPARAMETERS_FILE = Constants.getInputStreamFromPath(Constants.PARAMETERS_FILE);

		InputStream isFinalInput = FileUtils.openInputStream(finalInput);
		OutputStream osOutputBasicForm = FileUtils.openOutputStream(new File(outputBasicFormPath));

		saxonService.transformPoguesXML2DDI(isFinalInput, osOutputBasicForm,
				isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL, isPROPERTIES_FILE, isPARAMETERS_FILE);

		isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL.close();
		isPROPERTIES_FILE.close();
		isPARAMETERS_FILE.close();
		isFinalInput.close();
		osOutputBasicForm.close();

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
