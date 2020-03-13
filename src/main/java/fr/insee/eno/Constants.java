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
	public static final String PARAMS_DEFAULT_FOLDER_PATH = "/params/default";
	public static final String PARAMS_SCHEMAS_FOLDER_PATH = "/params/schemas";
	public static final String TRANSFORMATIONS_FOLDER = "/xslt/transformations";
	public static final String CONFIG_FOLDER = "/config";
	public static final String INPUTS_FOLDER = "/xslt/inputs";
	public static final File LABEL_FOLDER = getFileFromUrl(Constants.class.getResource("/lang/fr"));
	public static final String PARAMETERS_DEFAULT_XML = PARAMS_DEFAULT_FOLDER_PATH+"/parameters.xml";
	


	// Params : schema
	public static final URL ENO_PARAMETERS_XSD = Constants.class.getResource(PARAMS_SCHEMAS_FOLDER_PATH+"/ENOParameters.xsd");

	// ----- XSL Parameters path
	public static final String CONFIG_DDI2FR = CONFIG_FOLDER + "/ddi2fr.xml";
	public static final String CONFIG_DDI2ODT = CONFIG_FOLDER + "/ddi2odt.xml";
	public static final String CONFIG_DDI2PDF = CONFIG_FOLDER + "/ddi2pdf.xml";
	public static final String CONFIG_POGUES_XML2DDI = CONFIG_FOLDER + "/pogues-xml2ddi.xml";
	public static final String CONFIG_DDI2JS = CONFIG_FOLDER + "/ddi2js.xml";
	public static final String PARAMETERS_DEFAULT = PARAMS_DEFAULT_FOLDER_PATH + "/parameters.xml";
	public static final String METADATA_DEFAULT = PARAMS_DEFAULT_FOLDER_PATH + "/metadata.xml";
	public static final String MAPPING_DEFAULT = PARAMS_DEFAULT_FOLDER_PATH + "/mapping.xml";
	public static final String LABELS_FOLDER = "/lang/fr/";


	/********************************************************/
	/********************** Pre-processing ******************/
	/********************************************************/
	public static final String PRE_PROCESSING_FOLDER = "/xslt/pre-processing";
	public static final String UTIL_DDI_SPLITTING_XSL = UTIL_FOLDER_PATH + "/ddi/splitting.xsl";
	/******************* DDI - Pre-processing ****************/
	public static final String DDI_DEREFERENCING_XSL = PRE_PROCESSING_FOLDER + "/ddi/dereferencing.xsl";
	public static final String UTIL_DDI_DEREFERENCING_XSL = UTIL_FOLDER_PATH + "/ddi/dereferencing.xsl";
	public static final String UTIL_DDI_TITLING_XSL = PRE_PROCESSING_FOLDER + "/ddi/titling.xsl";
	public static final String UTIL_DDI_MAPPING_XSL = PRE_PROCESSING_FOLDER + "/ddi/mapping.xsl";
	public static final String UTIL_DDI_CLEANING_XSL = PRE_PROCESSING_FOLDER + "/ddi/cleaning.xsl";
	public static final String UTIL_DDI32_TO_DDI33_XSL = UTIL_FOLDER_PATH + "/ddi/ddi32toddi33.xsl";
	
	/*************** PoguesXML - Pre-processing **************/
	public static final String UTIL_POGUES_XML_SUPP_GOTO_XSL = PRE_PROCESSING_FOLDER + "/pogues-xml/2suppressionGoto.xsl";
	public static final String UTIL_POGUES_XML_MERGE_ITE_XSL = PRE_PROCESSING_FOLDER + "/pogues-xml/tweak-to-merge-equivalent-ite.xsl";
	public static final String UTIL_POGUES_XML_GOTO_ITE_XSL = PRE_PROCESSING_FOLDER + "/pogues-xml/goto-2-if-then-else.xsl";
	
	
	
	/********************************************************/
	/********************* Post-processing ******************/
	/********************************************************/
	public static final String POST_PROCESSING_FOLDER = "/xslt/post-processing";
	
	/********************* DDI - Post-processing ******************/
	public static final String UTIL_DDI_MW2XHTML_XSL = POST_PROCESSING_FOLDER + "/ddi/mw2xhtml.xsl";
	public static final String UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL = POST_PROCESSING_FOLDER + "/ddi/tweak-xhtml-for-ddi.xsl";
	
	public static final String UTIL_FODS_PREFORMATTING_XSL = UTIL_FOLDER_PATH + "/fods/preformatting.xsl";
	public static final String UTIL_XSL_INCORPORATION_XSL = UTIL_FOLDER_PATH + "/xsl/incorporation.xsl";

	
	public static final String BROWSING_FR_TEMPLATE_XSL = UTIL_FOLDER_PATH + "/fr/browsing.xsl";

	/********************* XSL generation ******************/
	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/ddi2odt.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/ddi2pdf.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/pogues-xml2ddi.xsl";
	public static final String TRANSFORMATIONS_DDI2POGUES_XML_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pogues-xml/ddi2pogues-xml.xsl";
	public static final String TRANSFORMATIONS_DDI2JS_DDI2JS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2js/ddi2js.xsl";

	public static final String TRANSFORMATIONS_DDI2FR_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2fr/drivers.fods";
	public static final String TRANSFORMATIONS_DDI2ODT_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2odt/drivers.fods";
	public static final String TRANSFORMATIONS_DDI2PDF_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2pdf/drivers.fods";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/drivers.fods";
	public static final String TRANSFORMATIONS_DDI2JS_DRIVERS_FODS = TRANSFORMATIONS_FOLDER + "/ddi2js/drivers.fods";

	public static final String TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2fr/functions.fods";
	public static final String TRANSFORMATIONS_DDI2ODT_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2odt/functions.fods";
	public static final String TRANSFORMATIONS_DDI2PDF_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2pdf/functions.fods";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/pogues-xml2ddi/functions.fods";
	public static final String TRANSFORMATIONS_DDI2JS_FUNCTIONS_FODS = TRANSFORMATIONS_FOLDER	+ "/ddi2js/functions.fods";

	public static final String TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/functions.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/functions.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/functions.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/functions.xsl";
	public static final String TRANSFORMATIONS_DDI2JS_FUNCTIONS_XSL = TRANSFORMATIONS_FOLDER + "/ddi2js/functions.xsl";

	public static final String TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2fr/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2ODT_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2odt/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2PDF_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2pdf/tree-navigation.fods";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2JS_TREE_NAVIGATION_FODS = TRANSFORMATIONS_FOLDER + "/ddi2js/tree-navigation.fods";
	public static final String TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/pogues-xml2ddi/tree-navigation.xsl";
	public static final String TRANSFORMATIONS_DDI2JS_TREE_NAVIGATION_XSL = TRANSFORMATIONS_FOLDER + "/ddi2js/tree-navigation.xsl";

	public static final String TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2fr/ddi2fr-fixed.xsl";
	public static final String TRANSFORMATIONS_DDI2ODT_DDI2ODT_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2odt/ddi2odt-fixed.xsl";
	public static final String TRANSFORMATIONS_DDI2PDF_DDI2PDF_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2pdf/ddi2pdf-fixed.xsl";
	public static final String TRANSFORMATIONS_DDI2JS_DDI2JS_FIXED_XSL = TRANSFORMATIONS_FOLDER + "/ddi2js/ddi2js-fixed.xsl";
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
	
	/********************* Merging paramters ******************/
	public static final String MERGE_PARAMETERS_XSL = UTIL_FOLDER_PATH + "/params/merge-parameters.xsl";
	
	

	/********************************************************/
	/********************* Post-processing ******************/
	/********************************************************/
	public static final String OUPUTS_FOLDER = "/xslt/outputs/pdf";
	public static final String POST_PROCESSING_FOLDER_PDF = "/xslt/post-processing/pdf";
	public static final String POST_PROCESSING_FOLDER_JS = "/xslt/post-processing/js";
	public static final String POST_PROCESSING_FOLDER_FR = "/xslt/post-processing/fr";
	public static final String TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF =  OUPUTS_FOLDER + "/publipostage.xsl";

	/********************* PDF/FO - Post-processing ******************/
	public static final String TRANSFORMATIONS_CUSTOMIZATION_FO_4PDF_2 =  POST_PROCESSING_FOLDER_PDF + "/mailing-vtl.xsl";
	public static final String TRANSFORMATIONS_SPECIF_TREATMENT_FO_4PDF =  POST_PROCESSING_FOLDER_PDF +"/pdf-specific-treatment.xsl";
	public static final String TRANSFORMATIONS_ACCOMPANYING_MAILS_FO_4PDF =  POST_PROCESSING_FOLDER_PDF + "/accompanying-mails.xsl";
	public static final String TRANSFORMATIONS_COVER_PAGE_FO_4PDF =  POST_PROCESSING_FOLDER_PDF + "/insert-cover-page.xsl";
	public static final String TRANSFORMATIONS_END_QUESTION_FO_4PDF =  POST_PROCESSING_FOLDER_PDF + "/insert-end-questions.xsl";
	public static final String TRANSFORMATIONS_EDIT_STRUCTURE_PAGES_FO_4PDF =  POST_PROCESSING_FOLDER_PDF + "/edit-structure-page.xsl";
	
	/********************* JS/XML-Lunatic - Post-processing ******************/
	public static final String TRANSFORMATIONS_SORT_COMPONENTS_JS = POST_PROCESSING_FOLDER_JS + "/sort-components.xsl";
	public static final String TRANSFORMATIONS_EXTERNALIZE_VARIABLES_JS = POST_PROCESSING_FOLDER_JS + "/externalize-variables.xsl";
	public static final String TRANSFORMATIONS_INSERT_GENERIC_QUESTIONS_JS = POST_PROCESSING_FOLDER_JS + "/insert-generic-questions.xsl";
	
	/********************* FR/Xform Post-processing ******************/
	public static final String UTIL_FR_BROWSING_XSL = POST_PROCESSING_FOLDER_FR + "/browsing.xsl";
	public static final String UTIL_FR_FIX_ADHERENCE_XSL = POST_PROCESSING_FOLDER_FR + "/fix-adherence.xsl";
	public static final String UTIL_FR_EDIT_PATRON_XSL = POST_PROCESSING_FOLDER_FR + "/edit-patron.xsl";
	public static final String UTIL_FR_IDENTIFICATION_XSL = POST_PROCESSING_FOLDER_FR + "/identification.xsl";
	public static final String UTIL_FR_INSERT_END_XSL = POST_PROCESSING_FOLDER_FR + "/insert-end.xsl";
	public static final String UTIL_FR_INSERT_GENERIC_QUESTIONS_XSL = POST_PROCESSING_FOLDER_FR + "/insert-generic-questions.xsl";
	public static final String UTIL_FR_INSERT_WELCOME_XSL = POST_PROCESSING_FOLDER_FR + "/insert-welcome.xsl";
	public static final String UTIL_FR_MODELE_COLTRANE_XSL = POST_PROCESSING_FOLDER_FR + "/modele-coltrane.xsl";
	public static final String UTIL_FR_SPECIFIC_TREATMENT_XSL = POST_PROCESSING_FOLDER_FR + "/fr-specific-treatment.xsl";

	
	/********************* Temporary file system ******************/
	// ----- Folders
	public static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") + "/eno";

	public static final File TEMP_FOLDER = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH);

	public static final File TEMP_FILE_PARAMS(String file) {
		return getFileOrDirectoryFromPath(TEMP_FOLDER_PATH +"/"+file);
	}
	public static File sUB_TEMP_FOLDER_FILE (String survey){
		return getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/"+survey);
	}

	public static String sUB_TEMP_FOLDER (String survey){
		return TEMP_FOLDER_PATH + "/"+survey;
	}

	// ----- Files
	public static File tEMP_NULL_TMP (String sUB_TEMP_FOLDER){
		return getFileOrDirectoryFromPath(sUB_TEMP_FOLDER + "/null.tmp");
	}
	public static File tEMP_MAPPING_TMP (String sUB_TEMP_FOLDER){
		return getFileOrDirectoryFromPath(sUB_TEMP_FOLDER + "/mapping.xml");
	}
	public static final File TEMP_PREFORMATE_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/preformate.tmp");
	public static final File TEMP_XML_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/xml.tmp");
	public static final File TEMP_TEMP_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/temp.tmp");
	public static final File TEMP_TEMP_BIS_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/temp/temp-bis.tmp");

	// Those files holds the XSL generated from FODS ; they will be then copied to resource directory when packaging to JAR
	public static final File TRANSFORMATIONS_DDI2FR_DDI2FR_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/ddi2fr.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_DDI2ODT_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/ddi2odt.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_DDI2PDF_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/ddi2pdf.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/pogues-xml2ddi.xsl");
	public static final File TRANSFORMATIONS_DDI2JS_DDI2JS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2js/ddi2js.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/drivers.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2JS_DRIVERS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2js/drivers.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/functions.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2JS_FUNCTIONS_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2js/functions.xsl");
	public static final File TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2fr/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_DDI2ODT_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2odt/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_DDI2PDF_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2pdf/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/pogues-xml2ddi/tree-navigation.xsl");
	public static final File TRANSFORMATIONS_DDI2JS_TREE_NAVIGATION_XSL_TMP = getFileOrDirectoryFromPath(TEMP_FOLDER_PATH + "/ddi2js/tree-navigation.xsl");

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

	@SuppressWarnings("finally")
	public static File getFileFromUrl(URL url) {
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		} finally {
			return file;
		}
	}
	
	/********************************************************/
	/*********************** Temp foder  ********************/
	/********************************************************/
	public static String tEMP_XFORMS_FOLDER (String sUB_TEMP_FOLDER){
		return sUB_TEMP_FOLDER + "/xforms";
	}
	public static String tEMP_ODT_FOLDER(String sUB_TEMP_FOLDER){
		return sUB_TEMP_FOLDER + "/odt";
	}
	public static String tEMP_JS_FOLDER(String sUB_TEMP_FOLDER){
		return sUB_TEMP_FOLDER+ "/js";
	}
	public static String tEMP_PDF_FOLDER(String sUB_TEMP_FOLDER){
		return sUB_TEMP_FOLDER+ "/pdf";
	}
	public static String tEMP_DDI_FOLDER(String sUB_TEMP_FOLDER){
		return sUB_TEMP_FOLDER + "/ddi";
	}
	public static String tEMP_POGUES_XML_FOLDER(String sUB_TEMP_FOLDER){
		return sUB_TEMP_FOLDER + "/pogues-xml";
	}
	public static final String BASIC_FORM_TMP_FILENAME = "basic-form.tmp";
	
	
	
	
	/********************************************************/
	/********************* File Extension *******************/
	/************** (used during post-processing) ***********/
	
	public static final String BASE_NAME_FORM_FILE = "/form";

	/************ DDI and pogues-xml extension ***************/
	public static final String CLEANED_EXTENSION = "-cleaned.tmp";
	public static final String MW_EXTENSION = "-mw.tmp";
	public static final String FINAL_EXTENSION = "-final.tmp";
	public static final String TEMP_EXTENSION = "-temp.xml";
	public static final String FINAL_DDI_EXTENSION = "-final.xml";
	public static final String DDI32_DDI33_EXTENSION = "-ddi33.xml";

	/********************* pdf/fo extension *******************/
	public static final String ACCOMPANYING_MAILS_FO_EXTENSION = "-accompanying-mails.fo";
	public static final String COVER_PAGE_FO_EXTENSION = "-cover-page.fo";
	public static final String EDIT_STRUCTURE_FO_EXTENSION = "-edit-structure.fo";
	public static final String END_QUESTION_FO_EXTENSION = "-end-question.fo";
	public static final String SPECIFIC_TREAT_PDF_EXTENSION = "-specific-form.fo";
	public static final String TABLE_COL_SIZE_PDF_EXTENSION = "-temp.fo";
	public static final String FINAL_PDF_EXTENSION = "-final-out.fo";
	public static final String MAILING_FO_EXTENSION = "-mailing-vtl.fo";

	/****************** js/xml-lunatic extension **************/
	public static final String SORT_COMPONENTS_JS_EXTENSION = "-sorted.xml";
	public static final String INSERT_GENERIC_QUESTIONS_JS_EXTENSION = "-insert-questions.xml";
	public static final String EXTERNALIZE_VARIABLES_JS_EXTENSION = "-ext-variables.xml";
	public static final String VTL_PARSER_JS_EXTENSION = "-vtl-parsed.xml";
	public static final String FINAL_JS_EXTENSION = "-lunatic.xml";

	/******************** fr/xform extension ******************/
	public static final String BROWSING_FR_EXTENSION = "-browsing.xhtml";
	public static final String EDIT_PATRON_FR_EXTENSION = "-edit-patron.xhtml";
	public static final String FIX_ADHERENCE_FR_EXTENSION = "-fix-adherence.xhtml";
	public static final String IDENTIFICATION_FR_EXTENSION = "-identification.xhtml";
	public static final String INSERT_END_FR_EXTENSION = "-insert-end.xhtml";
	public static final String INSERT_WELCOME_FR_EXTENSION = "-insert-welcome.xhtml";
	public static final String INSERT_GENERIC_QUESTIONS_FR_EXTENSION = "-insert-questions.xhtml";
	public static final String MODELE_COLTRANE_FR_EXTENSION = "-modele-coltrane.xhtml";
	public static final String SPECIFIC_TREATMENT_FR_EXTENSION = "-specific-treatment.xhtml";
}
