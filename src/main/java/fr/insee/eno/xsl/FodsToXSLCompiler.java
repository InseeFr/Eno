package fr.insee.eno.xsl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;
import fr.insee.eno.utils.FolderCleaner;

/**
 * The core engine of Eno is based on XSL functions that are generated from a catalog
 * of drivers stored in a FODS spreadsheet. This class manage this generation
 * process.
 * */
public class FodsToXSLCompiler {
	
	private final static String FIVE_SPACES = "     ";
	
	final static Logger logger = LogManager.getLogger(FodsToXSLCompiler.class);

	private static XslTransformation saxonService = new XslTransformation();
	private static FolderCleaner cleanService = new FolderCleaner();

	/**
	 * A Main function used to be called from maven:exec plugin The main
	 * EnoPreprocessing method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			cleaning();

			logger.info("Fods to XSL: START");
			logger.debug(FIVE_SPACES + "Fods2Xsl target called for each .fods file");

			// Fods2Xsl for /transformations/ddi/.fods files
			generateDDI2FRDrivers();
			generateDDI2FRFunctions();
			generateDDI2FRTreeNavigation();

			// Fods2Xsl for /output/ddi/.fods files
			generateDDIFunctions();
			generateDDITemplates();
			
			logger.info("Fods2Xsl : xsl stylesheets created.");

			// Incorporation target : creating ddi2fr.xsl
			ddi2frIncorporationTarget();
			logger.debug("Fods to XSL: END");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.debug("Fods to XSL : END");
			System.exit(0);
		}
	}

	/**
	 * Be sure the temporary directories are cleaned.
	 * @throws IOException
	 */
	private static void cleaning() throws IOException {
		logger.debug("Before compilation : Cleaning /temp folder");
		cleanService.cleanOneFolder(Constants.TEMP_FOLDER);
		logger.debug("/temp folder cleaned");
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FRDrivers() throws Exception, IOException {
		logger.info("Generating DDI2FR drivers.");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL);
		fods2XslTarget(
				Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_FODS),
				FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL_TMP));
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FRFunctions() throws Exception, IOException {
		logger.info("Generating DDI2FR functions.");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL);
		fods2XslTarget(
				Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS),
				FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL_TMP));
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FRTreeNavigation() throws Exception, IOException {
		logger.info("Generating DDI2FR tree navigation");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS + 
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL);
		fods2XslTarget(
				Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS),
				FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL_TMP));
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDIFunctions() throws Exception, IOException {
		logger.info("Generating DDI functions");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.INPUTS_DDI_FUNCTIONS_FODS + 
				" -Output : " + Constants.INPUTS_DDI_FUNCTIONS_XSL);
		fods2XslTarget(
				Constants.getInputStreamFromPath(Constants.INPUTS_DDI_FUNCTIONS_FODS),
				FileUtils.openOutputStream(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP));
	}

	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDITemplates() throws Exception, IOException {
		logger.info("Generating DDI templates");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.INPUTS_DDI_TEMPLATES_FODS + 
				" -Output : " + Constants.INPUTS_DDI_TEMPLATES_XSL);
		fods2XslTarget(
				Constants.getInputStreamFromPath(Constants.INPUTS_DDI_TEMPLATES_FODS),
				FileUtils.openOutputStream(Constants.INPUTS_DDI_TEMPLATES_XSL_TMP));
	}

	/**
	 * This is the generic method called when generating XSLs from a FODS description file.
	 * 
	 * @param inputFods : the input fods file
	 * @param outputXsl : the output xsl file to be created
	 * @throws Exception : XSL related exceptions
	 */
	public static void fods2XslTarget(InputStream inputFods, OutputStream outputXsl) throws Exception {
		logger.info("Entering Fods2Xsl");
		// From inputfile.fods to preformate.tmp using preformatting.xsl
		logger.debug(
				FIVE_SPACES +
				"Preformatting : -Input : " + inputFods +
				" -Output : " + Constants.TEMP_PREFORMATE_TMP +
				 " -Stylesheet : " + Constants.UTIL_FODS_PREFORMATTING_XSL);
		
		saxonService.transform(
				inputFods,
				Constants.getInputStreamFromPath(Constants.UTIL_FODS_PREFORMATTING_XSL),
				FileUtils.openOutputStream(Constants.TEMP_PREFORMATE_TMP));

		// From preformate.tmp to xml.tmp using fods2xml.xsl
		logger.debug(
				"Fods2Xml : -Input : " + Constants.TEMP_PREFORMATE_TMP +
				" -Output : " + Constants.TEMP_XML_TMP +
				" -Stylesheet : " + Constants.FODS_2_XML_XSL);
		
		saxonService.transform(
				FileUtils.openInputStream(Constants.TEMP_PREFORMATE_TMP),
				Constants.getInputStreamFromPath(Constants.FODS_2_XML_XSL),
				FileUtils.openOutputStream(Constants.TEMP_XML_TMP));

		// From xml.tmp to inputfile.xsl
		logger.debug(
				"Xml2Xsl : -Input : " + Constants.TEMP_XML_TMP + 
				" -Output : " + outputXsl + 
				" -Stylesheet : " + Constants.XML_2_XSL_XSL);
		
		saxonService.transform(
				FileUtils.openInputStream(Constants.TEMP_XML_TMP),
				Constants.getInputStreamFromPath(Constants.XML_2_XSL_XSL),
				outputXsl);

		logger.info("Leaving Fods2Xsl");
	}

	/**
	 * Main method of the incorporation target
	 * 
	 * @throws Exception
	 */
	public static void ddi2frIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
		// Incorporating ddi2fr-fixed.xsl, drivers.xsl, functions.xsl and
		// tree-navigation.xsl into ddi2fr.xsl
		logger.debug(
				"Incorporating " + Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL +
				" and " + Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_TMP);
		
		saxonService.transformIncorporation(
				Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL),
				Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL),
				FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP),
				Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL_TMP);

		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP + 
				" and " + Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_BIS_TMP);
		
		saxonService.transformIncorporation(
				FileUtils.openInputStream(Constants.TEMP_TEMP_TMP),
				Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL),
				FileUtils.openOutputStream(Constants.TEMP_TEMP_BIS_TMP),
				Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL_TMP);

		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_BIS_TMP +
				" and " + Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL_TMP + 
				" in " + Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL_TMP);
		
		saxonService.transformIncorporation(
				FileUtils.openInputStream(Constants.TEMP_TEMP_BIS_TMP),
				Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL),
				FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL_TMP),
				Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL_TMP);

		// Incorporating source-fixed.xsl, functions.xsl and templates.xsl into
		// source.xsl
		logger.debug(
				"Incorporating " + Constants.INPUTS_DDI_SOURCE_FIXED_XSL +
				" and " + Constants.INPUTS_DDI_FUNCTIONS_XSL +
				" in " + Constants.TEMP_TEMP_TMP);
		
		saxonService.transformIncorporation(
				Constants.getInputStreamFromPath(Constants.INPUTS_DDI_SOURCE_FIXED_XSL), 
				Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL),
				FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP), 
				Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP);

		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP +
				" and " + Constants.INPUTS_DDI_TEMPLATES_XSL +
				" in " + Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		saxonService.transformIncorporation(
				FileUtils.openInputStream(Constants.TEMP_TEMP_TMP), 
				Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL),
				FileUtils.openOutputStream(Constants.INPUTS_DDI_SOURCE_XSL_TMP), 
				Constants.INPUTS_DDI_TEMPLATES_XSL_TMP);
		logger.debug("Leaving Incorporation");
	}
}
