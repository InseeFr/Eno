package fr.insee.eno.xsl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	final static Logger logger = LoggerFactory.getLogger(FodsToXSLCompiler.class);

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
			// Fods2Xsl for /transformations/ddi/.fods files
			generateDDI2XFORMSDrivers();
			generateDDI2FODTDrivers();
			generateDDI2FODrivers();
			generateDDI2LUNATICXMLDrivers();
			generatePOGUESXML2DDIDrivers();
			generateDDI2XFORMSFunctions();
			generateDDI2FODTFunctions();
			generateDDI2FOFunctions();
			generateDDI2LUNATICXMLFunctions();
			generatePOGUESXML2DDIFunctions();
			generateDDI2XFORMSTreeNavigation();
			generatePOGUESXML2DDITreeNavigation();
			generateDDI2FODTTreeNavigation();
			generateDDI2FOTreeNavigation();
			generateDDI2LUNATICXMLTreeNavigation();
			// Fods2Xsl for /output/ddi/.fods files
			generateDDIFunctions();
			generatePOGUESXMLFunctions();
			generateDDITemplates();
			generatePOGUESXMLTemplates();
			logger.info("Fods2Xsl : xsl stylesheets created.");
			// Incorporation target : creating ddi2fr.xsl
			ddi2xformsIncorporationTarget();
			ddi2fodtIncorporationTarget();
			ddi2foIncorporationTarget();
			ddi2lunaticxmlIncorporationTarget();
			poguesxml2ddiIncorporationTarget();
			// TODO Copy generated files to JAR or classpath
			logger.debug("Fods to XSL: END");
			copyGeneratedFilesDDI2XFORMS();
			copyGeneratedFilesDDI2FODT();
			copyGeneratedFilesDDI2FO();
			copyGeneratedFilesDDI2LUNATICXML();
			copyGeneratedFilesPOGUESXML2DDI();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.exit(0);
		}
	}

	/**
	 * Make sure the temporary directories are cleaned.
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
	private static void generateDDI2XFORMSDrivers() throws Exception, IOException {
		logger.info("Generating DDI2XFORMS drivers.");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP);
		InputStream isTRANSFORMATIONS_DDI2XFORMS_DRIVERS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2XFORMS_DRIVERS_FODS,
				osTRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2XFORMS_DRIVERS_FODS.close();
		osTRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FODTDrivers() throws Exception, IOException {
		logger.info("Generating DDI2FODT drivers.");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP);
		InputStream isTRANSFORMATIONS_DDI2FODT_DRIVERS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2FODT_DRIVERS_FODS,
				osTRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2FODT_DRIVERS_FODS.close();
		osTRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FODrivers() throws Exception, IOException {
		logger.info("Generating DDI2FO drivers.");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP);
		InputStream isTRANSFORMATIONS_DDI2FO_DRIVERS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2FO_DRIVERS_FODS,
				osTRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2FO_DRIVERS_FODS.close();
		osTRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2LUNATICXMLDrivers() throws Exception, IOException {
		logger.info("Generating DDI2lUNATICXML drivers.");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP);
		InputStream isTRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_FODS,
				osTRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_FODS.close();
		osTRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP.close();
	}
	
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generatePOGUESXML2DDIDrivers() throws Exception, IOException {
		logger.info("Generating POGUESXML2DDI drivers.");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP);
		InputStream isTRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS);
		OutputStream osTRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS,
				osTRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS.close();
		osTRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP.close();
	}
		
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2XFORMSFunctions() throws Exception, IOException {
		logger.info("Generating DDI2XFORMS functions.");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL);
		InputStream isTRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_FODS,
				osTRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP);
		isTRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_FODS.close();
		osTRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FODTFunctions() throws Exception, IOException {
		logger.info("Generating DDI2FODT functions.");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL);
		InputStream isTRANSFORMATIONS_DDI2FODT_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2FODT_FUNCTIONS_FODS,
				osTRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP);
		isTRANSFORMATIONS_DDI2FODT_FUNCTIONS_FODS.close();
		osTRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FOFunctions() throws Exception, IOException {
		logger.info("Generating DDI2FO functions.");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL);
		InputStream isTRANSFORMATIONS_DDI2FO_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2FO_FUNCTIONS_FODS,
				osTRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP);
		isTRANSFORMATIONS_DDI2FO_FUNCTIONS_FODS.close();
		osTRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2LUNATICXMLFunctions() throws Exception, IOException {
		logger.info("Generating DDI2LUNATICXML functions.");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL);
		InputStream isTRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_FODS);
		OutputStream osTRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_FODS,
				osTRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP);
		isTRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_FODS.close();
		osTRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP.close();
	}
		
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generatePOGUESXML2DDIFunctions() throws Exception, IOException {
		logger.info("Generating POGUESXML2DDI functions.");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS +
				" -Output : " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL);
		InputStream isTRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS);
		OutputStream osTRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS,
				osTRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP);
		isTRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS.close();
		osTRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2XFORMSTreeNavigation() throws Exception, IOException {
		logger.info("Generating DDI2XFORMS tree navigation");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_FODS + 
				" -Output : " + Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL);
		InputStream isTRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_FODS);
		OutputStream osTRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_FODS,
				osTRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP);
		isTRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_FODS.close();
		osTRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FODTTreeNavigation() throws Exception, IOException {
		logger.info("Generating DDI2FODT tree navigation");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_FODS + 
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL);
		InputStream isTRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_FODS);
		OutputStream osTRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_FODS,
				osTRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP);
		isTRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_FODS.close();
		osTRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2FOTreeNavigation() throws Exception, IOException {
		logger.info("Generating DDI2FO tree navigation");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_FODS + 
				" -Output : " + Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL);
		InputStream isTRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_FODS);
		OutputStream osTRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_FODS,
				osTRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP);
		isTRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_FODS.close();
		osTRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generateDDI2LUNATICXMLTreeNavigation() throws Exception, IOException {
		logger.info("Generating DDI2LUNATICXML tree navigation");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_FODS + 
				" -Output : " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL);
		InputStream isTRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_FODS);
		OutputStream osTRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_FODS,
				osTRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP);
		isTRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_FODS.close();
		osTRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generatePOGUESXML2DDITreeNavigation() throws Exception, IOException {
		logger.info("Generating POGUESXML2DDI tree navigation");
		logger.debug(
				FIVE_SPACES +
				"Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS + 
				" -Output : " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL);
		InputStream isTRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS);
		
		OutputStream osTRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP);
		fods2XslTarget(
				isTRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS,
				osTRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP);
		isTRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS.close();
		osTRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP.close();
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
		InputStream isINPUTS_DDI_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.INPUTS_DDI_FUNCTIONS_FODS);
		OutputStream osINPUTS_DDI_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isINPUTS_DDI_FUNCTIONS_FODS,
				osINPUTS_DDI_FUNCTIONS_XSL_TMP);
		isINPUTS_DDI_FUNCTIONS_FODS.close();
		osINPUTS_DDI_FUNCTIONS_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generatePOGUESXMLFunctions() throws Exception, IOException {
		logger.info("Generating POGUESXML functions");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.INPUTS_POGUES_XML_FUNCTIONS_FODS + 
				" -Output : " + Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL);
		InputStream isINPUTS_POGUES_XML_FUNCTIONS_FODS = Constants.getInputStreamFromPath(Constants.INPUTS_POGUES_XML_FUNCTIONS_FODS);
		OutputStream osINPUTS_POGUES_XML_FUNCTIONS_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL_TMP);
		fods2XslTarget(
				isINPUTS_POGUES_XML_FUNCTIONS_FODS,
				osINPUTS_POGUES_XML_FUNCTIONS_XSL_TMP);
		isINPUTS_POGUES_XML_FUNCTIONS_FODS.close();
		osINPUTS_POGUES_XML_FUNCTIONS_XSL_TMP.close();
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
		InputStream isINPUTS_DDI_TEMPLATES_FODS = Constants.getInputStreamFromPath(Constants.INPUTS_DDI_TEMPLATES_FODS);
		OutputStream osINPUTS_DDI_TEMPLATES_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_DDI_TEMPLATES_XSL_TMP);
		fods2XslTarget(
				isINPUTS_DDI_TEMPLATES_FODS,
				osINPUTS_DDI_TEMPLATES_XSL_TMP);
		isINPUTS_DDI_TEMPLATES_FODS.close();
		osINPUTS_DDI_TEMPLATES_XSL_TMP.close();
	}
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	private static void generatePOGUESXMLTemplates() throws Exception, IOException {
		logger.info("Generating POGUESXML templates");
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + Constants.INPUTS_POGUES_XML_TEMPLATES_FODS + 
				" -Output : " + Constants.INPUTS_POGUES_XML_TEMPLATES_XSL);
		InputStream isINPUTS_POGUES_XML_TEMPLATES_FODS = Constants.getInputStreamFromPath(Constants.INPUTS_POGUES_XML_TEMPLATES_FODS);
		OutputStream osINPUTS_POGUES_XML_TEMPLATES_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_POGUES_XML_TEMPLATES_XSL_TMP);
		fods2XslTarget(
				isINPUTS_POGUES_XML_TEMPLATES_FODS,
				osINPUTS_POGUES_XML_TEMPLATES_XSL_TMP);
		isINPUTS_POGUES_XML_TEMPLATES_FODS.close();
		osINPUTS_POGUES_XML_TEMPLATES_XSL_TMP.close();
	}

	/**
	 * This is the generic method called when generating XSLs from a FODS description file.
	 * 
	 * There are three steps: 
	 * 
	 * <ol>
	 * 	<li>
	 * 		<p>first, a preformating step that ensure the file is ready to be processed.</p>
	 * 		<p><code>XSL: /xslt/util/fods/preformatting.xsl</code></p>
	 * 	</li>
	 *  <li>
	 *  	<p>then the fods is transformed in a proper XML file.</p>
	 *  	<p><code>XSL: /xslt/transformations/fods2xml.xsl</code></p>
	 *  </li>
	 *  <li>
	 *  	<p>finally, from this XML is generated the XSL.</p>
	 *  	<p><code>XSL: /xslt/transformations/xml2xsl.xsl</code></p>
	 *  </li>
	 * </ol>  
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
		
		InputStream isUTIL_FODS_PREFORMATTING_XSL = Constants.getInputStreamFromPath(Constants.UTIL_FODS_PREFORMATTING_XSL);
		OutputStream osTEMP_PREFORMATE_TMP = FileUtils.openOutputStream(Constants.TEMP_PREFORMATE_TMP);
		
		saxonService.transform(
				inputFods,
				isUTIL_FODS_PREFORMATTING_XSL,
				osTEMP_PREFORMATE_TMP);
		
		isUTIL_FODS_PREFORMATTING_XSL.close();
		osTEMP_PREFORMATE_TMP.close();
		
		// From preformate.tmp to xml.tmp using fods2xml.xsl
		logger.debug(
				"Fods2Xml : -Input : " + Constants.TEMP_PREFORMATE_TMP +
				" -Output : " + Constants.TEMP_XML_TMP +
				" -Stylesheet : " + Constants.FODS_2_XML_XSL);
		
		InputStream isTEMP_PREFORMATE_TMP = FileUtils.openInputStream(Constants.TEMP_PREFORMATE_TMP);
		InputStream isFODS_2_XML_XSL = Constants.getInputStreamFromPath(Constants.FODS_2_XML_XSL);
		OutputStream osTEMP_XML_TMP = FileUtils.openOutputStream(Constants.TEMP_XML_TMP);
		
		saxonService.transform(
				isTEMP_PREFORMATE_TMP,
				isFODS_2_XML_XSL,
				osTEMP_XML_TMP);
		
		isTEMP_PREFORMATE_TMP.close();
		isFODS_2_XML_XSL.close();
		osTEMP_XML_TMP.close();
		
		// From xml.tmp to inputfile.xsl
		logger.debug(
				"Xml2Xsl : -Input : " + Constants.TEMP_XML_TMP + 
				" -Output : " + outputXsl + 
				" -Stylesheet : " + Constants.XML_2_XSL_XSL);
		
		InputStream isTEMP_XML_TMP = FileUtils.openInputStream(Constants.TEMP_XML_TMP);
		InputStream isXML_2_XSL_XSL = Constants.getInputStreamFromPath(Constants.XML_2_XSL_XSL);
		
		saxonService.transform(
				isTEMP_XML_TMP,
				isXML_2_XSL_XSL,
				outputXsl);
		
		isTEMP_XML_TMP.close();
		isXML_2_XSL_XSL.close();
		logger.info("Leaving Fods2Xsl");
	}

	/**
	 * Main method of the incorporation target
	 * 
	 * @throws Exception
	 */
	public static void ddi2xformsIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
		// Incorporating ddi2fr-fixed.xsl, drivers.xsl, functions.xsl and
		// tree-navigation.xsl into ddi2fr.xsl
		
		// Incorporating ddi2fr-fixed.xsl and drivers into TEMP_TEMP_TMP
		logger.debug(
				"Incorporating " + Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_FIXED_XSL +
				" and " + Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isTRANSFORMATIONS_DDI2XFORMS_DDI2FR_FIXED_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isTRANSFORMATIONS_DDI2XFORMS_DDI2FR_FIXED_XSL,
				isUTIL_XSL_INCORPORATION_XSL,
				osTEMP_TEMP_TMP,
				Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2XFORMS_DDI2FR_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL.close();
		osTEMP_TEMP_TMP.close();
		
		// Fusion : functions.xsl and the file TEMP_TEMP_TMP into TEMP_TEMP_BIS_TMP
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP + 
				" and " + Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_BIS_TMP);
		InputStream isTEMP_TEMP_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL2 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_BIS_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_BIS_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP,
				isUTIL_XSL_INCORPORATION_XSL2,
				osTEMP_TEMP_BIS_TMP,
				Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP);
		isTEMP_TEMP_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL2.close();
		osTEMP_TEMP_BIS_TMP.close();
		
		// Fusion : tree-navigations.xsl and the file TEMP_TEMP_BIS_TMP into ddi2fr.xsl
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_BIS_TMP +
				" and " + Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP + 
				" in " + Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL_TMP);
		
		InputStream isTEMP_TEMP_BIS_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_BIS_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL3 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTRANSFORMATIONS_DDI2XFORMS_DDI2FR_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_BIS_TMP,
				isUTIL_XSL_INCORPORATION_XSL3,
				osTRANSFORMATIONS_DDI2XFORMS_DDI2FR_XSL_TMP,
				Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP);
		isTEMP_TEMP_BIS_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL3.close();
		osTRANSFORMATIONS_DDI2XFORMS_DDI2FR_XSL_TMP.close();

		
		// Incorporating source-fixed.xsl, functions.xsl and
		// template.xsl into source.xsl
		
		// Incorporating source-fixed.xsl and functions.xsl on TEMP_TEMP_TMP
		logger.debug(
				"Incorporating " + Constants.INPUTS_DDI_SOURCE_FIXED_XSL +
				" and " + Constants.INPUTS_DDI_FUNCTIONS_XSL +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isINPUTS_DDI_SOURCE_FIXED_XSL = Constants.getInputStreamFromPath(Constants.INPUTS_DDI_SOURCE_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL4 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP2 = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isINPUTS_DDI_SOURCE_FIXED_XSL, 
				isUTIL_XSL_INCORPORATION_XSL4,
				osTEMP_TEMP_TMP2, 
				Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP);
		isINPUTS_DDI_SOURCE_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL4.close();
		osTEMP_TEMP_TMP2.close();
		
		// Incorporating TEMP_TEMP_TMP and template.xsl on source.xsl
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP +
				" and " + Constants.INPUTS_DDI_TEMPLATES_XSL +
				" in " + Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		InputStream isTEMP_TEMP_TMP2 = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL5 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osINPUTS_DDI_SOURCE_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP2, 
				isUTIL_XSL_INCORPORATION_XSL5,
				osINPUTS_DDI_SOURCE_XSL_TMP, 
				Constants.INPUTS_DDI_TEMPLATES_XSL_TMP);
		isTEMP_TEMP_TMP2.close();
		isUTIL_XSL_INCORPORATION_XSL5.close();
		osINPUTS_DDI_SOURCE_XSL_TMP.close();
		logger.debug("Leaving Incorporation");
	}
	
	/**
	 * Main method of the incorporation target
	 * 
	 * @throws Exception
	 */
	public static void ddi2fodtIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
		// Incorporating ddi2odt-fixed.xsl, drivers.xsl, functions.xsl and
		// tree-navigation.xsl into ddi2odt.xsl
		logger.debug(
				"Incorporating " + Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_FIXED_XSL +
				" and " + Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isTRANSFORMATIONS_DDI2FODT_DDI2ODT_FIXED_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isTRANSFORMATIONS_DDI2FODT_DDI2ODT_FIXED_XSL,
				isUTIL_XSL_INCORPORATION_XSL,
				osTEMP_TEMP_TMP,
				Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2FODT_DDI2ODT_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL.close();
		osTEMP_TEMP_TMP.close();
		
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP + 
				" and " + Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_BIS_TMP);
		InputStream isTEMP_TEMP_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL2 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_BIS_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_BIS_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP,
				isUTIL_XSL_INCORPORATION_XSL2,
				osTEMP_TEMP_BIS_TMP,
				Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP);
		isTEMP_TEMP_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL2.close();
		osTEMP_TEMP_BIS_TMP.close();
		
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_BIS_TMP +
				" and " + Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP + 
				" in " + Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL_TMP);
		
		InputStream isTEMP_TEMP_BIS_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_BIS_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL3 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTRANSFORMATIONS_DDI2FODT_DDI2ODT_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_BIS_TMP,
				isUTIL_XSL_INCORPORATION_XSL3,
				osTRANSFORMATIONS_DDI2FODT_DDI2ODT_XSL_TMP,
				Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP);
		isTEMP_TEMP_BIS_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL3.close();
		osTRANSFORMATIONS_DDI2FODT_DDI2ODT_XSL_TMP.close();
		// Incorporating source-fixed.xsl, functions.xsl and templates.xsl into
		// source.xsl
		logger.debug(
				"Incorporating " + Constants.INPUTS_DDI_SOURCE_FIXED_XSL +
				" and " + Constants.INPUTS_DDI_FUNCTIONS_XSL +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isINPUTS_DDI_SOURCE_FIXED_XSL = Constants.getInputStreamFromPath(Constants.INPUTS_DDI_SOURCE_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL4 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP2 = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isINPUTS_DDI_SOURCE_FIXED_XSL, 
				isUTIL_XSL_INCORPORATION_XSL4,
				osTEMP_TEMP_TMP2, 
				Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP);
		isINPUTS_DDI_SOURCE_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL4.close();
		osTEMP_TEMP_TMP2.close();
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP +
				" and " + Constants.INPUTS_DDI_TEMPLATES_XSL +
				" in " + Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		InputStream isTEMP_TEMP_TMP2 = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL5 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osINPUTS_DDI_SOURCE_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP2, 
				isUTIL_XSL_INCORPORATION_XSL5,
				osINPUTS_DDI_SOURCE_XSL_TMP, 
				Constants.INPUTS_DDI_TEMPLATES_XSL_TMP);
		isTEMP_TEMP_TMP2.close();
		isUTIL_XSL_INCORPORATION_XSL5.close();
		osINPUTS_DDI_SOURCE_XSL_TMP.close();
		logger.debug("Leaving Incorporation");
	}
	
	
	/**
	 * Main method of the incorporation target
	 * 
	 * @throws Exception
	 */
	public static void ddi2foIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
		// Incorporating ddi2fo-fixed.xsl, drivers.xsl, functions.xsl and
		// tree-navigation.xsl into ddi2fo.xsl
		logger.debug(
				"Incorporating " + Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_FIXED_XSL +
				" and " + Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isTRANSFORMATIONS_DDI2FO_DDI2PDF_FIXED_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isTRANSFORMATIONS_DDI2FO_DDI2PDF_FIXED_XSL,
				isUTIL_XSL_INCORPORATION_XSL,
				osTEMP_TEMP_TMP,
				Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2FO_DDI2PDF_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL.close();
		osTEMP_TEMP_TMP.close();
		
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP + 
				" and " + Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_BIS_TMP);
		InputStream isTEMP_TEMP_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL2 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_BIS_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_BIS_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP,
				isUTIL_XSL_INCORPORATION_XSL2,
				osTEMP_TEMP_BIS_TMP,
				Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP);
		isTEMP_TEMP_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL2.close();
		osTEMP_TEMP_BIS_TMP.close();
		
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_BIS_TMP +
				" and " + Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP + 
				" in " + Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL_TMP);
		
		InputStream isTEMP_TEMP_BIS_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_BIS_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL3 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_BIS_TMP,
				isUTIL_XSL_INCORPORATION_XSL3,
				osTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL_TMP,
				Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP);
		isTEMP_TEMP_BIS_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL3.close();
		osTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL_TMP.close();
		// Incorporating source-fixed.xsl, functions.xsl and templates.xsl into
		// source.xsl
		logger.debug(
				"Incorporating " + Constants.INPUTS_DDI_SOURCE_FIXED_XSL +
				" and " + Constants.INPUTS_DDI_FUNCTIONS_XSL +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isINPUTS_DDI_SOURCE_FIXED_XSL = Constants.getInputStreamFromPath(Constants.INPUTS_DDI_SOURCE_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL4 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP2 = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isINPUTS_DDI_SOURCE_FIXED_XSL, 
				isUTIL_XSL_INCORPORATION_XSL4,
				osTEMP_TEMP_TMP2, 
				Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP);
		isINPUTS_DDI_SOURCE_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL4.close();
		osTEMP_TEMP_TMP2.close();
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP +
				" and " + Constants.INPUTS_DDI_TEMPLATES_XSL +
				" in " + Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		InputStream isTEMP_TEMP_TMP2 = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL5 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osINPUTS_DDI_SOURCE_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP2, 
				isUTIL_XSL_INCORPORATION_XSL5,
				osINPUTS_DDI_SOURCE_XSL_TMP, 
				Constants.INPUTS_DDI_TEMPLATES_XSL_TMP);
		isTEMP_TEMP_TMP2.close();
		isUTIL_XSL_INCORPORATION_XSL5.close();
		osINPUTS_DDI_SOURCE_XSL_TMP.close();
		logger.debug("Leaving Incorporation");
	}
	
	
	public static void ddi2lunaticxmlIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
		// Incorporating ddi2js-fixed.xsl, drivers.xsl, functions.xsl and
		// tree-navigation.xsl into ddi2lunaticxml.xsl
		logger.debug(
				"Incorporating " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_FIXED_XSL +
				" and " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isTRANSFORMATIONS_DDI2LUNATIC_XML_DDI2JS_FIXED_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isTRANSFORMATIONS_DDI2LUNATIC_XML_DDI2JS_FIXED_XSL,
				isUTIL_XSL_INCORPORATION_XSL,
				osTEMP_TEMP_TMP,
				Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DDI2LUNATIC_XML_DDI2JS_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL.close();
		osTEMP_TEMP_TMP.close();
		
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP + 
				" and " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_BIS_TMP);
		InputStream isTEMP_TEMP_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL2 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_BIS_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_BIS_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP,
				isUTIL_XSL_INCORPORATION_XSL2,
				osTEMP_TEMP_BIS_TMP,
				Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP);
		isTEMP_TEMP_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL2.close();
		osTEMP_TEMP_BIS_TMP.close();
		
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_BIS_TMP +
				" and " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP + 
				" in " + Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP);
		
		InputStream isTEMP_TEMP_BIS_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_BIS_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL3 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_BIS_TMP,
				isUTIL_XSL_INCORPORATION_XSL3,
				osTRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP,
				Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP);
		isTEMP_TEMP_BIS_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL3.close();
		osTRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP.close();
		// Incorporating source-fixed.xsl, functions.xsl and templates.xsl into
		// source.xsl
		logger.debug(
				"Incorporating " + Constants.INPUTS_DDI_SOURCE_FIXED_XSL +
				" and " + Constants.INPUTS_DDI_FUNCTIONS_XSL +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isINPUTS_DDI_SOURCE_FIXED_XSL = Constants.getInputStreamFromPath(Constants.INPUTS_DDI_SOURCE_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL4 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP2 = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isINPUTS_DDI_SOURCE_FIXED_XSL, 
				isUTIL_XSL_INCORPORATION_XSL4,
				osTEMP_TEMP_TMP2, 
				Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP);
		isINPUTS_DDI_SOURCE_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL4.close();
		osTEMP_TEMP_TMP2.close();
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP +
				" and " + Constants.INPUTS_DDI_TEMPLATES_XSL +
				" in " + Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		InputStream isTEMP_TEMP_TMP2 = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL5 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osINPUTS_DDI_SOURCE_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_DDI_SOURCE_XSL_TMP);
		
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP2, 
				isUTIL_XSL_INCORPORATION_XSL5,
				osINPUTS_DDI_SOURCE_XSL_TMP, 
				Constants.INPUTS_DDI_TEMPLATES_XSL_TMP);
		isTEMP_TEMP_TMP2.close();
		isUTIL_XSL_INCORPORATION_XSL5.close();
		osINPUTS_DDI_SOURCE_XSL_TMP.close();
		logger.debug("Leaving Incorporation");
	}
	
	
	/**
	 * Main method of the incorporation target
	 * 
	 * @throws Exception
	 */
	public static void poguesxml2ddiIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
		
		// Incorporating poguesxml2ddi-fixed.xsl, drivers.xsl, functions.xsl and
		// tree-navigation.xsl 
		// into poguesxml2ddi.xsl
		
		// Incorporating poguesxml2ddi-fixed.xsl and drivers into TEMP_TEMP_TMP
		logger.debug(
				"Incorporating " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL +
				" and " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL = Constants.getInputStreamFromPath(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL,
				isUTIL_XSL_INCORPORATION_XSL,
				osTEMP_TEMP_TMP,
				Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL.close();
		osTEMP_TEMP_TMP.close();
		
		// Fusion : functions.xsl and the file TEMP_TEMP_TMP into TEMP_TEMP_BIS_TMP
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP + 
				" and " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP +
				" in " + Constants.TEMP_TEMP_BIS_TMP);
		InputStream isTEMP_TEMP_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL2 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL_TMP = FileUtils.openOutputStream(Constants.TEMP_TEMP_BIS_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP,
				isUTIL_XSL_INCORPORATION_XSL2,
				osTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL_TMP,
				Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP);
		isTEMP_TEMP_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL2.close();
		osTRANSFORMATIONS_DDI2FO_DDI2PDF_XSL_TMP.close();
		
		// Fusion : tree-navigations.xsl and the file TEMP_TEMP_BIS_TMP into ddi2fr.xsl
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_BIS_TMP +
				" and " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP + 
				" in " + Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP);
		
		InputStream isTEMP_TEMP_BIS_TMP = FileUtils.openInputStream(Constants.TEMP_TEMP_BIS_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL3 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP = FileUtils.openOutputStream(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP);
		saxonService.transformIncorporation(
				isTEMP_TEMP_BIS_TMP,
				isUTIL_XSL_INCORPORATION_XSL3,
				osTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP,
				Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP);
		isTEMP_TEMP_BIS_TMP.close();
		isUTIL_XSL_INCORPORATION_XSL3.close();
		osTRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP.close();
		
		// Incorporating source-fixed.xsl, functions.xsl and templates.xsl into
		// source.xsl
		logger.debug(
				"Incorporating " + Constants.INPUTS_POGUES_XML_SOURCE_FIXED_XSL +
				" and " + Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL +
				" in " + Constants.TEMP_TEMP_TMP);
		
		InputStream isINPUTS_POGUES_XML_SOURCE_FIXED_XSL = Constants.getInputStreamFromPath(Constants.INPUTS_POGUES_XML_SOURCE_FIXED_XSL);
		InputStream isUTIL_XSL_INCORPORATION_XSL4 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osTEMP_TEMP_TMP2 = FileUtils.openOutputStream(Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(
				isINPUTS_POGUES_XML_SOURCE_FIXED_XSL, 
				isUTIL_XSL_INCORPORATION_XSL4,
				osTEMP_TEMP_TMP2, 
				Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL_TMP);
		isINPUTS_POGUES_XML_SOURCE_FIXED_XSL.close();
		isUTIL_XSL_INCORPORATION_XSL4.close();
		osTEMP_TEMP_TMP2.close();
		logger.debug(
				"Incorporating " + Constants.TEMP_TEMP_TMP +
				" and " + Constants.INPUTS_POGUES_XML_TEMPLATES_XSL +
				" in " + Constants.INPUTS_POGUES_XML_SOURCE_XSL_TMP);
		
		InputStream isTEMP_TEMP_TMP2 = FileUtils.openInputStream(Constants.TEMP_TEMP_TMP);
		InputStream isUTIL_XSL_INCORPORATION_XSL5 = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osINPUTS_POGUES_XML_SOURCE_XSL_TMP = FileUtils.openOutputStream(Constants.INPUTS_POGUES_XML_SOURCE_XSL_TMP);
		
		saxonService.transformIncorporation(
				isTEMP_TEMP_TMP2, 
				isUTIL_XSL_INCORPORATION_XSL5,
				osINPUTS_POGUES_XML_SOURCE_XSL_TMP, 
				Constants.INPUTS_POGUES_XML_TEMPLATES_XSL_TMP);
		isTEMP_TEMP_TMP2.close();
		isUTIL_XSL_INCORPORATION_XSL5.close();
		osINPUTS_POGUES_XML_SOURCE_XSL_TMP.close();
		logger.debug("Leaving Incorporation");
	}
	
	/**
	 * When every file has been generated, we want to copy them in the /xslt directory to be
	 * used through the Java API.
	 * */
	private static void copyGeneratedFilesDDI2XFORMS() throws Exception {
		String destinationBasePath = System.getProperty("dest");
		logger.info(String.format("Copying generated files to %s", destinationBasePath));
		File inputsDestination = new File(destinationBasePath + "/xslt/inputs/ddi");
		File transformsDestination = new File(destinationBasePath + "/xslt/transformations/ddi2xforms");
		try {
			FileUtils.copyDirectory(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP.getParentFile(), inputsDestination);
			FileUtils.copyDirectory(Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL_TMP.getParentFile(), transformsDestination);
		} catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * When every file has been generated, we want to copy them in the /xslt directory to be
	 * used through the Java API.
	 * */
	private static void copyGeneratedFilesDDI2FODT() throws Exception {
		String destinationBasePath = System.getProperty("dest");
		logger.info(String.format("Copying generated files to %s", destinationBasePath));
		File inputsDestination = new File(destinationBasePath + "/xslt/inputs/ddi");
		File transformsDestination = new File(destinationBasePath + "/xslt/transformations/ddi2fodt");
		try {
			FileUtils.copyDirectory(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP.getParentFile(), inputsDestination);
			FileUtils.copyDirectory(Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL_TMP.getParentFile(), transformsDestination);
		} catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * When every file has been generated, we want to copy them in the /xslt directory to be
	 * used through the Java API.
	 * */
	private static void copyGeneratedFilesDDI2FO() throws Exception {
		String destinationBasePath = System.getProperty("dest");
		logger.info(String.format("Copying generated files to %s", destinationBasePath));
		File inputsDestination = new File(destinationBasePath + "/xslt/inputs/ddi");
		File transformsDestination = new File(destinationBasePath + "/xslt/transformations/ddi2fo");
		try {
			FileUtils.copyDirectory(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP.getParentFile(), inputsDestination);
			FileUtils.copyDirectory(Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL_TMP.getParentFile(), transformsDestination);
		} catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * When every file has been generated, we want to copy them in the /xslt directory to be
	 * used through the Java API.
	 * */
	private static void copyGeneratedFilesDDI2LUNATICXML() throws Exception {
		String destinationBasePath = System.getProperty("dest");
		logger.info(String.format("Copying generated files to %s", destinationBasePath));
		File inputsDestination = new File(destinationBasePath + "/xslt/inputs/ddi");
		File transformsDestination = new File(destinationBasePath + "/xslt/transformations/ddi2lunatic-xml");
		try {
			FileUtils.copyDirectory(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP.getParentFile(), inputsDestination);
			FileUtils.copyDirectory(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP.getParentFile(), transformsDestination);
		} catch(Exception e){
			throw e;
		}
	}
	

	/**
	 * When every file has been generated, we want to copy them in the /xslt directory to be
	 * used through the Java API.
	 * */
	private static void copyGeneratedFilesPOGUESXML2DDI() throws Exception {
		String destinationBasePath = System.getProperty("dest");
		logger.info(String.format("Copying generated files to %s", destinationBasePath));
		File inputsDestination = new File(destinationBasePath + "/xslt/inputs/pogues-xml");
		File transformsDestination = new File(destinationBasePath + "/xslt/transformations/pogues-xml2ddi");
		try {
			FileUtils.copyDirectory(Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL_TMP.getParentFile(), inputsDestination);
			FileUtils.copyDirectory(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP.getParentFile(), transformsDestination);
		} catch(Exception e){
			throw e;
		}
	}

	
}
