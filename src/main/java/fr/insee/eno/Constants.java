package fr.insee.eno;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all the different paths used in the application Based on
 * the Eno-Questionnaire-Generator architecture
 * 
 * @author gerose
 *
 */
public final class Constants {
	
	
	private static final Logger logger = LoggerFactory.getLogger(Constants.class);

	private Constants() {

	}
	
	// ---------- Core resources: references to XSL, XML, etc. resources used to generate a questionnaire
	
	// ----- Folders
	public static final String UTIL_FOLDER_PATH = "/xslt/util";
	public static final String TRANSFORMATIONS_FOLDER = "/xslt/transformations";
	public static final String CONFIG_FOLDER = "/config";
	public static final String INPUTS_FOLDER = "/xslt/inputs";
	public static final File LABEL_FOLDER = getFileFromUrl(Constants.class.getResource("/lang/fr"));
	
	// ----- Ref
	public static final String PARAMETERS_XML = "parameters.xml";
	
	// ----- XSL Parameters path
	public static final String CONFIG_DDI2FR = "/config/ddi2fr.xml";
	public static final String CONFIG_DDI2ODT = "/config/ddi2odt.xml";
	public static final String CONFIG_DDI2PDF = "/config/ddi2pdf.xml";
	public static final String CONFIG_POGUES_XML2DDI = "/config/pogues-xml2ddi.xml";
	public static final String PARAMETERS = "/parameters.xml";
	public static final String LABELS_FOLDER = "/lang/fr/";
	
	
	// ----- Files
	public static final String DDI_DEREFERENCING_XSL = UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl";
	public static final String PARAMETERS_FILE = "/" + PARAMETERS_XML;
	public static final String UTIL_DDI_TITLING_XSL = UTIL_FOLDER_PATH + "/ddi/titling.xsl";
	public static final String UTIL_POGUES_XML_SUPP_GOTO_XSL = UTIL_FOLDER_PATH + "/pogues-xml/2suppressionGoto.xsl";
	public static final String UTIL_POGUES_XML_MERGE_ITE_XSL = UTIL_FOLDER_PATH + "/pogues-xml/tweak-to-merge-equivalent-ite.xsl";
	public static final String UTIL_DDI_MW2XHTML_XSL = UTIL_FOLDER_PATH + "/ddi/mw2xhtml.xsl";
	public static final String UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL = UTIL_FOLDER_PATH + "/ddi/tweak-xhtml-for-ddi.xsl";
	public static final String UTIL_DDI_CLEANING_XSL = UTIL_FOLDER_PATH + "/ddi/cleaning.xsl";
	public static final String UTIL_FODS_PREFORMATTING_XSL = UTIL_FOLDER_PATH + "/fods/preformatting.xsl";
	public static final String UTIL_XSL_INCORPORATION_XSL = UTIL_FOLDER_PATH + "/xsl/incorporation.xsl";
	public static final String UTIL_DDI_DEREFERENCING_XSL = UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl";
	public static final String BROWSING_FR_TEMPLATE_XSL = UTIL_FOLDER_PATH + "/fr/browsing.xsl";
	public static final String PROPERTIES_FILE_FR = CONFIG_FOLDER + "/ddi2fr.xml";
	public static final String PROPERTIES_FILE_ODT = CONFIG_FOLDER + "/ddi2odt.xml";
	public static final String PROPERTIES_FILE_PDF = CONFIG_FOLDER + "/ddi2pdf.xml";
	public static final String PROPERTIES_FILE_DDI = CONFIG_FOLDER + "/pogues-xml2ddi.xml";
	
	// ---------- XSL generation
	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/ddi2odt.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/ddi2pdf.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/pogues-xml2ddi.xsl";
	
	public static final String TRANSFORMATIONS_DDI2FR_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2fr/drivers.fods";
	public static final String TRANSFORMATIONS_DDI2ODT_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2odt/drivers.fods";
	public static final String TRANSFORMATIONS_DDI2PDF_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2pdf/drivers.fods";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/drivers.fods";
	
	public static final String TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2fr/functions.fods";
	public static final String TRANSFORMATIONS_DDI2ODT_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2odt/functions.fods";
	public static final String TRANSFORMATIONS_DDI2PDF_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2pdf/functions.fods";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/pogues-xml2ddi/functions.fods";
	
	public static final String TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/functions.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/functions.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/functions.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/functions.xsl";
	
	public static final String TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2fr/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2ODT_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2odt/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2PDF_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2pdf/tree-navigation.fods";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/tree-navigation.xsl";
	
	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr-fixed.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_DDI2ODT_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/ddi2odt-fixed.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_DDI2PDF_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/ddi2pdf-fixed.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/pogues-xml2ddi-fixed.xsl";
	
	// ---------- 
	public static final String INPUTS_DDI_FUNCTIONS_FODS = INPUTS_FOLDER + "/ddi/functions.fods";
	public static final String INPUTS_DDI_FUNCTIONS_XSL = INPUTS_FOLDER + "/ddi/functions.xsl";
	public static final String INPUTS_DDI_TEMPLATES_FODS = INPUTS_FOLDER + "/ddi/templates.fods";
	public static final String INPUTS_DDI_TEMPLATES_XSL = INPUTS_FOLDER + "/ddi/templates.xsl";
	
	public static final String INPUTS_POGUES_XML_FUNCTIONS_FODS = INPUTS_FOLDER + "/pogues-xml/functions.fods";
	public static final String INPUTS_POGUES_XML_FUNCTIONS_XSL = INPUTS_FOLDER + "/pogues-xml/functions.xsl";
	public static final String INPUTS_POGUES_XML_TEMPLATES_FODS = INPUTS_FOLDER + "/pogues-xml/templates.fods";
	public static final String INPUTS_POGUES_XML_TEMPLATES_XSL = INPUTS_FOLDER + "/pogues-xml/templates.xsl";
	
	public static final String INPUTS_DDI_SOURCE_FIXED_XSL = INPUTS_FOLDER + "/ddi/source-fixed.xsl";
	public static final String INPUTS_POGUES_XML_SOURCE_FIXED_XSL = INPUTS_FOLDER + "/pogues-xml/source-fixed.xsl";
	public static final String FODS_2_XML_XSL = TRANSFORMATIONS_FOLDER + "/fods2xml.xsl";
	public static final String XML_2_XSL_XSL = TRANSFORMATIONS_FOLDER + "/xml2xsl.xsl";
	
	// ---------- Temporary file system
	
	// ----- Folders
	//public static final String TEMP_FOLDER_PATH = "/target/eno";
	public static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") + "/eno";
	
	public static final File TEMP_FOLDER = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH);
	public static final File SUB_TEMP_FOLDER = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp");
	
	// ----- Files
	public static final File TEMP_NULL_TMP = getFileOrDirectoryFromPath(SUB_TEMP_FOLDER + "/null.tmp");
	public static final File TEMP_PREFORMATE_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/preformate.tmp");
	public static final File TEMP_XML_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/xml.tmp");
	public static final File TEMP_TEMP_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/temp.tmp");
	public static final File TEMP_TEMP_BIS_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/temp-bis.tmp");
	// Those files holds the XSL generated from FODS ; they will be then copied to resource directory when packaging to JAR
	public static final File TRANSFORMATIONS_DDI2FR_DDI2FR_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/ddi2fr.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/ddi2odt.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/ddi2pdf.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/pogues-xml2ddi.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/drivers.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/functions.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/tree-navigation.xsl");
	
	public static final File INPUTS_DDI_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi/functions.xsl");
	public static final File INPUTS_DDI_TEMPLATES_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi/templates.xsl");
	public static final File INPUTS_DDI_SOURCE_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi/source.xsl");
	
	public static final File INPUTS_POGUES_XML_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml/functions.xsl");
	public static final File INPUTS_POGUES_XML_TEMPLATES_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml/templates.xsl");
	public static final File INPUTS_POGUES_XML_SOURCE_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml/source.xsl");
	
	// ---------- Utilies
	/** Generic file getter from classpath 
	 * @return the file or null when not found.
	 * */
	public static InputStream getInputStreamFromPath(String path) {
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
//	public static final String ROOT_FOLDER = "D:/arkn1q/Mes Documents/eclipse_workspace/Eno";
//
//	public static final String QUESTIONNAIRE_FOLDER = ROOT_FOLDER + "/questionnaires";
//	public static final String TEMP_TEST_FOLDER = TEMP_FOLDER_PATH + "/nonRegressionTest";

	/********************************************************/
	/******************* ENOPreprocessing *******************/
	/********************************************************/


	

	////// INCORPORATION TARGET
	//// Temporary files used in INCORPORATION


	//// Xsl stylesheets used in INCORPORATION
	

	/********************************************************/
	/******************* DDIPreprocessing *******************/
	/********************************************************/
	//// Plugin Conf
	public static final String PDF_PLUGIN_XML_CONF = "src/main/resources/config/plugins-conf.xml";
	public static final File PDF_PLUGIN_XML_CONF_FILE = getFileFromUrl(Constants.class.getResource("/config/plugins-conf.xml"));
	


	/********************************************************/
	/******************* DDIPreprocessing *******************/
	/********************************************************/

	//// Temporary files used in DDIPreprocessing
	public static final String OLD_TEMP_NULL_TMP2 = SUB_TEMP_FOLDER + "/null.tmp";

	//// Xsl stylesheets used in DDIPreprocessing

	
	public static final String CLEANED_EXTENSION = "-cleaned.tmp";
	public static final String MW_EXTENSION = "-mw.tmp";
	public static final String FINAL_EXTENSION = "-final.tmp";
	public static final String TEMP_EXTENSION = "-temp.xml";
	public static final String FINAL_DDI_EXTENSION = "-final.xml";
	public static final String FINAL_PDF_EXTENSION = "-out.fo";
	public static final String TEMP_XFORMS_FOLDER = SUB_TEMP_FOLDER + "/xforms";
	public static final String TEMP_ODT_FOLDER = SUB_TEMP_FOLDER + "/odt";
	public static final String TEMP_PDF_FOLDER = SUB_TEMP_FOLDER + "/pdf";
	public static final String TEMP_DDI_FOLDER = SUB_TEMP_FOLDER + "/ddi";
	public static final String TEMP_POGUES_XML_FOLDER = SUB_TEMP_FOLDER + "/pogues-xml";
	public static final String BASIC_FORM_TMP_FILENAME = "basic-form.tmp";

	/********************************************************/
	/***************** NON REGRESSION TEST ******************/
	/********************************************************/

	public static final String TEST_FILE_TO_COMPARE = TEMP_FOLDER_PATH + "/simpsons/v1/form/form.xhtml";
	public static final String TEST_REFERENCE_FILE = "simpsons-form.xhtml";
	public static final String TEST_INPUT_XML = "simpsons.xml";
	
	
}
