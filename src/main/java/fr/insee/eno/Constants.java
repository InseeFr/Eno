package fr.insee.eno;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
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
	// TODO Instead of using static reference to a file system, adapt to use
	
	// ---------- Core resources
	
	// ----- Folders
	public static final String UTIL_FOLDER_PATH = "/xslt/util";
	public static final String TRANSFORMATIONS_FOLDER = "/xslt/transformations";
	public static final String CONFIG_FOLDER = "/config";
	public static final String INPUTS_FOLDER = "/xslt/inputs";
	public static final File LABEL_FOLDER = getFileFromUrl(Constants.class.getResource("/lang/fr"));
	
	// ----- Ref
	public static final String PARAMETERS_XML = "parameters.xml";
	
	// ----- Files
	public static final InputStream DDI_DEREFERENCING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl");
	public static final InputStream PARAMETERS_FILE = getResourceFileFromPath("/" + PARAMETERS_XML);
	public static final InputStream UTIL_DDI_TITLING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/titling.xsl");
	public static final InputStream UTIL_DDI_CLEANING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/cleaning.xsl");
	public static final InputStream UTIL_FODS_PREFORMATTING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/fods/preformatting.xsl");
	public static final InputStream UTIL_XSL_INCORPORATION_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/xsl/incorporation.xsl");
	public static final InputStream UTIL_DDI_DEREFERENCING_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl");
	public static final InputStream BROWSING_TEMPLATE_XSL = getResourceFileFromPath(UTIL_FOLDER_PATH + "/fr/browsing.xsl");	
	public static final InputStream PROPERTIES_FILE = getResourceFileFromPath(CONFIG_FOLDER + "/ddi2fr.xml");
	public static final InputStream TRANSFORMATIONS_DDI2FR_DDI2FR_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr.xsl");
	public static final InputStream TRANSFORMATIONS_DDI2FR_DRIVERS_FODS = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/drivers.fods");
	public static final InputStream TRANSFORMATIONS_DDI2FR_DRIVERS_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/drivers.xsl");
	public static final InputStream TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS = getResourceFileFromPath(TRANSFORMATIONS_FOLDER	+ "/ddi2fr/functions.fods");
	public static final InputStream TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/functions.xsl");
	public static final InputStream TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/tree-navigation.fods");
	public static final InputStream TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/tree-navigation.xsl");
	public static final InputStream TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr-fixed.xsl");
	public static final InputStream INPUTS_DDI_FUNCTIONS_FODS = getResourceFileFromPath(INPUTS_FOLDER + "/ddi/functions.fods");
	public static final InputStream INPUTS_DDI_FUNCTIONS_XSL = getResourceFileFromPath(INPUTS_FOLDER + "/ddi/functions.xsl");
	public static final InputStream INPUTS_DDI_TEMPLATES_FODS = getResourceFileFromPath(INPUTS_FOLDER + "/ddi/templates.fods");
	public static final InputStream INPUTS_DDI_TEMPLATES_XSL = getResourceFileFromPath(INPUTS_FOLDER + "/ddi/templates.xsl");
	public static final InputStream INPUTS_DDI_SOURCE_FIXED_XSL = getResourceFileFromPath(INPUTS_FOLDER + "/ddi/source-fixed.xsl");
	public static final InputStream INPUTS_DDI_SOURCE_XSL = getResourceFileFromPath(INPUTS_FOLDER + "/ddi/source.xsl");
	public static final InputStream FODS_2_XML_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/fods2xml.xsl");
	public static final InputStream XML_2_XSL_XSL = getResourceFileFromPath(TRANSFORMATIONS_FOLDER + "/xml2xsl.xsl");
	
	// ---------- Temporary file system
	
	// ----- Folders
	public static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") + "Eno";
	
	public static final File SUB_TEMP_FOLDER = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp");
	
	// ----- Files
	public static final File TEMP_NULL_TMP = getFileOrDirectoryFromPath(SUB_TEMP_FOLDER + "/null.tmp");
	public static final File TEMP_PREFORMATE_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/preformate.tmp");
	public static final File TEMP_XML_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/xml.tmp");
	public static final File TEMP_TEMP_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/temp.tmp");
	public static final File TEMP_TEMP_BIS_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/temp-bis.tmp");
	
	
	// ---------- Utilies
	/** Generic file getter from classpath 
	 * @return the file or null when not found.
	 * */
	private static InputStream getResourceFileFromPath(String path) {
		logger.debug("Loading " + path);
		try {
			return Constants.class.getResourceAsStream(path);
		} catch (Exception e) {
			logger.error("Error when loading file");
			return null;
		}
	}
	
	/** Generic getter for files or directories */
	private static File getFileOrDirectoryFromPath(String path) {
		return Paths.get(path).toFile();
	}
	
	private static File getFileFromUrl(URL url) {
		File file = null;
	    try {
	        file = new File(url.toURI());
	    } catch (URISyntaxException e) {
	        file = new File(url.getPath());
	    } finally {
	        return file;
	    }
	}
	
	// TODO Under this comment are the legacy references to files and directories that eventually will be deleted.
	// ----------------------------------------------- //

	// Root folder of the project : must be filled
	// FIXME use a dynamic path
	public static final String ROOT_FOLDER = "D:/arkn1q/Mes Documents/eclipse_workspace/Eno";

	public static final String QUESTIONNAIRE_FOLDER = ROOT_FOLDER + "/questionnaires";
	public static final String TEMP_TEST_FOLDER = TEMP_FOLDER_PATH + "/nonRegressionTest";

	/********************************************************/
	/******************* ENOPreprocessing *******************/
	/********************************************************/


	

	////// INCORPORATION TARGET
	//// Temporary files used in INCORPORATION


	//// Xsl stylesheets used in INCORPORATION
	

	

	/********************************************************/
	/******************* DDIPreprocessing *******************/
	/********************************************************/

	//// Temporary files used in DDIPreprocessing
	public static final String OLD_TEMP_NULL_TMP = SUB_TEMP_FOLDER + "/null.tmp";

	//// Xsl stylesheets used in DDIPreprocessing


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
