package fr.insee.eno;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class contains all the different paths used in the application Based on
 * the Eno-Questionnaire-Generator architecture
 * 
 * @author gerose
 *
 */
public final class Constants {
	
	private static final Logger logger = LogManager.getLogger(Constants.class);

	private Constants() {

	}
	
	// ----------------------------------------------- //
	// TODO Instead of using static reference to a file system, adapt:
	// - to use files that are in the classpath for the core engine (XSLTs, etc.)$
	// - to use properties and defaults for the rest
	
	// ---------- Core resources
	
	// ----- Folders
	public static final String UTIL_FOLDER_PATH = "/xslt/util";
	public static final String TRANSFORMATIONS_FOLDER = "/xslt/transformations";
	public static final String CONFIG_FOLDER = "/config";
	
	// ----- Files
	public static final File DDI_DEREFERENCING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl");
	public static final File PARAMETERS_FILE = getResourceFileFromPath("/parameters.xml");
	public static final File UTIL_DDI_TITLING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/titling.xsl");
	public static final File BROWSING_TEMPLATE_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/fr/browsing.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_DDI2FR_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr.xsl");
	public static final File PROPERTIES_FILE = getResourceFileFromPath(CONFIG_FOLDER + "/ddi2fr.xml");
	
	// ---------- Temporary file system
	
	// ----- Folders
	public static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") + "/Eno";
	//Used for Cleaning
	public static final File SUB_TEMP_FOLDER = getFileOrDirectory(TEMP_FOLDER_PATH + "/temp");
	
	// ----- Files
	public static final File TEMP_NULL_TMP = getFileOrDirectory(SUB_TEMP_FOLDER + "/null.tmp");
	public static final File UTIL_DDI_CLEANING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/cleaning.xsl");
	
	/** Generic file getter from classpath */
	private static File getResourceFileFromPath(String path) {
		logger.debug("Loading " + path);
		try {
			return Paths.get(Constants.class.getResource(path).toURI()).toFile();
		} catch (URISyntaxException e) {
			logger.error("Error when loading to a file");
			return new File("null");
		}
	}
	
	/** Generic getter for files or directories */
	private static File getFileOrDirectory(String path) {
		return Paths.get(path).toFile();
	}
	
	// TODO Under this comment are the legacy references to files and directories that eventually will be deleted.
	// ----------------------------------------------- //

	// Root folder of the project : must be filled
	// FIXME use a dynamic path
	public static final String ROOT_FOLDER = "D:/arkn1q/Mes Documents/eclipse_workspace/Eno";

	// Useful folders
	public static final String INPUTS_FOLDER = ROOT_FOLDER + "/src/main/resources/xslt/inputs";
	
	
	
	
	
	public static final String QUESTIONNAIRE_FOLDER = ROOT_FOLDER + "/questionnaires";
	public static final String TEMP_TEST_FOLDER = TEMP_FOLDER_PATH + "/nonRegressionTest";

	/********************************************************/
	/******************* ENOPreprocessing *******************/
	/********************************************************/

	////// FODS2XSL TARGET
	//// Paths for .fods files
	// output/ddi
	public static final String INPUTS_DDI_FUNCTIONS_FODS = INPUTS_FOLDER + "/ddi/functions.fods";
	public static final String INPUTS_DDI_TEMPLATES_FODS = INPUTS_FOLDER + "/ddi/templates.fods";
	// transformations/ddi2fr
	public static final String TRANSFORMATIONS_DDI2FR_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2fr/drivers.fods";
	public static final String TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER
			+ "/ddi2fr/functions.fods";
	public static final String TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER
			+ "/ddi2fr/tree-navigation.fods";

	// Xsl stylesheets used in FODS2XSL
	public static final String UTIL_FODS_PREFORMATTING_XSL = UTIL_FOLDER_PATH + "/fods/preformatting.xsl";
	public static final String FODS_2_XML_XSL = TRANSFORMATIONS_FOLDER + "/fods2xml.xsl";
	public static final String XML_2_XSL_XSL = TRANSFORMATIONS_FOLDER + "/xml2xsl.xsl";

	// Temporary files used in FODS2XSL
	public static final String TEMP_PREFORMATE_TMP = TEMP_FOLDER_PATH + "/temp/preformate.tmp";
	public static final String TEMP_XML_TMP = TEMP_FOLDER_PATH + "/temp/xml.tmp";

	//// Output xsl files created by ENOPreprocessing
	// output/ddi
	public static final String INPUTS_DDI_FUNCTIONS_XSL = INPUTS_FOLDER + "/ddi/functions.xsl";
	public static final String INPUTS_DDI_TEMPLATES_XSL = INPUTS_FOLDER + "/ddi/templates.xsl";

	// transformations/ddi2fr
	public static final String TRANSFORMATIONS_DDI2FR_DRIVERS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/drivers.xsl";
	public static final String TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/functions.xsl";
	public static final String TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER
			+ "/ddi2fr/tree-navigation.xsl";

	////// INCORPORATION TARGET
	//// Temporary files used in INCORPORATION
	public static final String TEMP_TEMP_TMP = TEMP_FOLDER_PATH + "/temp/temp.tmp";
	public static final String TEMP_TEMP_BIS_TMP = TEMP_FOLDER_PATH + "/temp/temp-bis.tmp";

	//// Xsl stylesheets used in INCORPORATION
	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL = TRANSFORMATIONS_FOLDER
			+ "/ddi2fr/ddi2fr-fixed.xsl";
	public static final String UTIL_XSL_INCORPORATION_XSL = UTIL_FOLDER_PATH + "/xsl/incorporation.xsl";
	
	public static final String INPUTS_DDI_SOURCE_FIXED_XSL = INPUTS_FOLDER + "/ddi/source-fixed.xsl";
	public static final String INPUTS_DDI_SOURCE_XSL = INPUTS_FOLDER + "/ddi/source.xsl";

	

	/********************************************************/
	/******************* DDIPreprocessing *******************/
	/********************************************************/

	//// Temporary files used in DDIPreprocessing
	public static final String OLD_TEMP_NULL_TMP = SUB_TEMP_FOLDER + "/null.tmp";

	//// Xsl stylesheets used in DDIPreprocessing
	public static final String UTIL_DDI_DEREFERENCING_XSL = UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl";
	
	

	

	/********************************************************/
	/************************ DDI2FR ************************/
	/********************************************************/
	
	
	
	public static final String CLEANED_EXTENSION = "-cleaned.tmp";
	public static final String FINAL_EXTENSION = "-final.tmp";
	public static final String TEMP_XFORMS_FOLDER = SUB_TEMP_FOLDER + "/xforms";
	public static final String BASIC_FORM_TMP_FILENAME = "basic-form.tmp";

	/********************************************************/
	/***************** NON REGRESSION TEST ******************/
	/********************************************************/

	public static final String TEST_FILE_TO_COMPARE = TEMP_FOLDER_PATH + "/simpsons/v1/form/form.xhtml";
	public static final String TEST_REFERENCE_FILE = "simpsons-form.xhtml";
	public static final String TEST_INPUT_XML = "simpsons.xml";
}
