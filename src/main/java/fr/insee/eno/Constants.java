package fr.insee.eno;

/**
 * This class contains all the different paths used in the application Based on
 * the Eno-Questionnaire-Generator architecture
 * 
 * @author gerose
 *
 */
public final class Constants {

	private Constants() {

	}

	// Root folder of the project : must be filled
	// public static final String ROOT_FOLDER =
	// "D:/Users/gerose/Documents/Insee/UO3
	// CSPA/TestsREST/New_Eno_OneInput_Test/Eno-master";
	public static final String ROOT_FOLDER = "D:/arkn1q/Mes Documents/eclipse_workspace/Eno";

	// Useful folders
	public static final String INPUTS_FOLDER = ROOT_FOLDER + "/src/main/xslt/inputs";
	public static final String TRANSFORMATIONS_FOLDER = ROOT_FOLDER + "/src/main/xslt/transformations";
	public static final String UTIL_FOLDER = ROOT_FOLDER + "/src/main/xslt/util";
	public static final String TARGET_FOLDER = ROOT_FOLDER + "/target";
	public static final String QUESTIONNAIRE_FOLDER = ROOT_FOLDER + "/questionnaires";
	public static final String TEMP_TEST_FOLDER = TARGET_FOLDER + "/nonRegressionTest";

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
	public static final String UTIL_FODS_PREFORMATTING_XSL = UTIL_FOLDER + "/fods/preformatting.xsl";
	public static final String FODS_2_XML_XSL = TRANSFORMATIONS_FOLDER + "/fods2xml.xsl";
	public static final String XML_2_XSL_XSL = TRANSFORMATIONS_FOLDER + "/xml2xsl.xsl";

	// Temporary files used in FODS2XSL
	public static final String TEMP_PREFORMATE_TMP = TARGET_FOLDER + "/temp/preformate.tmp";
	public static final String TEMP_XML_TMP = TARGET_FOLDER + "/temp/xml.tmp";

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
	public static final String TEMP_TEMP_TMP = TARGET_FOLDER + "/temp/temp.tmp";
	public static final String TEMP_TEMP_BIS_TMP = TARGET_FOLDER + "/temp/temp-bis.tmp";

	//// Xsl stylesheets used in INCORPORATION
	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL = TRANSFORMATIONS_FOLDER
			+ "/ddi2fr/ddi2fr-fixed.xsl";
	public static final String UTIL_XSL_INCORPORATION_XSL = UTIL_FOLDER + "/xsl/incorporation.xsl";
	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr.xsl";
	public static final String INPUTS_DDI_SOURCE_FIXED_XSL = INPUTS_FOLDER + "/ddi/source-fixed.xsl";
	public static final String INPUTS_DDI_SOURCE_XSL = INPUTS_FOLDER + "/ddi/source.xsl";

	//// Used for Cleaning
	public static final String TARGET_TEMP_FOLDER = TARGET_FOLDER + "/temp";

	/********************************************************/
	/******************* DDIPreprocessing *******************/
	/********************************************************/

	//// Temporary files used in DDIPreprocessing
	public static final String TEMP_NULL_TMP = TARGET_TEMP_FOLDER + "/null.tmp";

	//// Xsl stylesheets used in DDIPreprocessing
	public static final String UTIL_DDI_DEREFERENCING_XSL = UTIL_FOLDER + "/ddi/dereferencing.xsl";
	public static final String UTIL_DDI_CLEANING_XSL = UTIL_FOLDER + "/ddi/cleaning.xsl";
	public static final String UTIL_DDI_TITLING_XSL = UTIL_FOLDER + "/ddi/titling.xsl";

	public static final String PARAMETERS_FILE = "parameters.xml";

	/********************************************************/
	/************************ DDI2FR ************************/
	/********************************************************/
	public static final String CONFIG_FOLDER = ROOT_FOLDER + "/config";
	public static final String PROPERTIES_FILE = CONFIG_FOLDER + "/ddi2fr.xml";
	public static final String BROWSING_TEMPLATE_XSL = UTIL_FOLDER + "/fr/browsing-and-template.xsl";
	public static final String CLEANED_EXTENSION = "-cleaned.tmp";
	public static final String FINAL_EXTENSION = "-final.tmp";
	public static final String TEMP_XFORMS_FOLDER = TARGET_TEMP_FOLDER + "/xforms";
	public static final String BASIC_FORM_TMP_FILENAME = "basic-form.tmp";

	/********************************************************/
	/***************** NON REGRESSION TEST ******************/
	/********************************************************/

	public static final String TEST_FILE_TO_COMPARE = TARGET_FOLDER + "/simpsons/v1/form/form.xhtml";
	public static final String TEST_REFERENCE_FILE = "simpsons-form.xhtml";
	public static final String TEST_INPUT_XML = "simpsons.xml";
}
