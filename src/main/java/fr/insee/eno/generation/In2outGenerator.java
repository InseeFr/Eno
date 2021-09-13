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
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class In2outGenerator{

	private static final Logger logger = LoggerFactory.getLogger(In2outGenerator.class);

	private XslTransformation saxonService = new XslTransformation();

	public File in2outGenerate(File finalInput, byte[] parameters, String surveyName, OutFormat outFormat) throws Exception {

		logger.info("int2out " + outFormat.toString() + " Target : START");
		logger.debug("Arguments : finalInput : " + finalInput + " surveyName " + surveyName);
		String formNameFolder = null;
		String outputBasicFormPath = null;

		String styleSheetPath = stylesheet(outFormat);
		String config = config(outFormat);
		
		formNameFolder = getFormNameFolder(finalInput);

		logger.debug("formNameFolder : " + formNameFolder);

		outputBasicFormPath = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/" + formNameFolder + "/form";
		logger.debug("Output folder for basic-form : " + outputBasicFormPath);
		
		String outputForm = outputBasicFormPath + outputName(outFormat);
	
		try (InputStream isTRANSFORMATIONS_in2out_XSL = Constants
				.getInputStreamFromPath(styleSheetPath);
			 InputStream isFinalInput = FileUtils.openInputStream(finalInput);
				OutputStream osOutputForm = FileUtils.openOutputStream(new File(outputForm));) {

    	saxonService.transformIn2Out(isFinalInput, osOutputForm, isTRANSFORMATIONS_in2out_XSL, parameters, config);
			
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					outFormat.toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
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
	

	public String config(OutFormat outFormat) {
		
		String configuration= null;
					switch (outFormat) {
			case DDI:
				configuration = Constants.CONFIG_POGUES_XML2DDI;
				break;
			case XFORMS:
				configuration = Constants.CONFIG_DDI2XFORMS;
				break;
			case LUNATIC_XML:
				configuration = Constants.CONFIG_DDI2LUNATIC_XML;
				break;
			case FODT:
				configuration = Constants.CONFIG_DDI2FODT;
				break;
			case FO:
				configuration = Constants.CONFIG_DDI2FO;
				break;
			}
	return configuration;
	}
					
	
	public String outputName(OutFormat outFormat) {
		
		String outputName= null;
					switch (outFormat) {
			case DDI:
				outputName = "/form.xml";
				break;
			case XFORMS:
				outputName = "/form.xml";
				break;
			case LUNATIC_XML:
				outputName = "/form.xml";
				break;
			case FODT:
				outputName = "/form.fodt";
				break;
			case FO:
				outputName = "/form.fo";
				break;
			}
	return outputName;
	}
			
public String stylesheet(OutFormat outFormat) {
	String stylesheetPath= null;
				switch (outFormat) {
				case DDI:
					stylesheetPath = Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL;
					break;
				case XFORMS:
					stylesheetPath = Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL;
					break;
				case LUNATIC_XML:
					stylesheetPath = Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL;
					break;
				case FODT:
					stylesheetPath = Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL;
					break;
				case FO:
					stylesheetPath = Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL;
					break;
				}
return stylesheetPath;
}

}
