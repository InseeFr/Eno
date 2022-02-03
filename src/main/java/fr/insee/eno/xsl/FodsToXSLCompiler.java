package fr.insee.eno.xsl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;
import fr.insee.eno.transform.xsl.XslTransformationIncorporation;
import fr.insee.eno.utils.FolderCleaner;

/**
 * The core engine of Eno is based on XSL functions that are generated from a catalog
 * of drivers stored in a FODS spreadsheet. This class manage this generation
 * process.
 * */




public class FodsToXSLCompiler {
	

	
	private final static String FIVE_SPACES = "     ";
	
	final static Logger logger = LoggerFactory.getLogger(FodsToXSLCompiler.class);

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
			copyGeneratedFiles();
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
	private static void generateIN2OUT(String fods, String drivers, String what) throws Exception, IOException {
		logger.info("Generating IN2OUT " + what);
		logger.debug(
				FIVE_SPACES + 
				"Fods2Xsl -Input : " + fods +
				" -Output : " + drivers );
		InputStream isTRANSFORMATIONS_DRIVERS_FODS = Constants.getInputStreamFromPath(fods);
		OutputStream osTRANSFORMATIONS_DRIVERS_XSL_TMP = FileUtils.openOutputStream(new File(drivers));
		
		fods2XslTarget(
				isTRANSFORMATIONS_DRIVERS_FODS,
				osTRANSFORMATIONS_DRIVERS_XSL_TMP);
		isTRANSFORMATIONS_DRIVERS_FODS.close();
		osTRANSFORMATIONS_DRIVERS_XSL_TMP.close();
	}
	
	// Drivers
	private static void generateDDI2XFORMSDrivers() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_FODS, Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP, "drivers");
    }

    private static void generateDDI2FODTDrivers() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_FODS, Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP, "drivers");
    }

    private static void generateDDI2FODrivers() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_FODS, Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP, "drivers");
    }

    private static void generateDDI2LUNATICXMLDrivers() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_FODS, Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP, "drivers");
    }

   private static void generatePOGUESXML2DDIDrivers() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_FODS, Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP, "drivers");
    }
   
   
   // Functions 
	private static void generateDDI2XFORMSFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_FODS, Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP, "functions");
    }

    private static void generateDDI2FODTFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_FODS, Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP, "functions");
    }

    private static void generateDDI2FOFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_FODS, Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP, "functions");
    }

    private static void generateDDI2LUNATICXMLFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_FODS, Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP, "functions");
    }

   private static void generatePOGUESXML2DDIFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_FODS, Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP, "functions");
    }
    
   // Tree navigation
	private static void generateDDI2XFORMSTreeNavigation() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_FODS, Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP, "tree navigation");
    }

    private static void generateDDI2FODTTreeNavigation() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_FODS, Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP, "tree navigation");
    }

    private static void generateDDI2FOTreeNavigation() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_FODS, Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP, "tree navigation");
    }

    private static void generateDDI2LUNATICXMLTreeNavigation() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_FODS, Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP, "tree navigation");
    }

   private static void generatePOGUESXML2DDITreeNavigation() throws Exception, IOException {
        generateIN2OUT(Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_FODS, Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP, "tree navigation");
    }
    
   // in functions
   private static void generateDDIFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.INPUTS_DDI_FUNCTIONS_FODS, Constants.INPUTS_DDI_FUNCTIONS_XSL, "DDI functions");
    }

   private static void generatePOGUESXMLFunctions() throws Exception, IOException {
        generateIN2OUT(Constants.INPUTS_POGUES_XML_FUNCTIONS_FODS, Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL, "POGUESXML functions");
    }

   // in templates
   private static void generateDDITemplates() throws Exception, IOException {
        generateIN2OUT(Constants.INPUTS_DDI_TEMPLATES_FODS, Constants.INPUTS_DDI_TEMPLATES_XSL, "DDI templates");
    }

   private static void generatePOGUESXMLTemplates() throws Exception, IOException {
        generateIN2OUT(Constants.INPUTS_POGUES_XML_TEMPLATES_FODS, Constants.INPUTS_POGUES_XML_TEMPLATES_XSL, "POGUESXML templates");
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
		final XslTransformation saxonService = new XslTransformation() {
		};
		
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
				osTEMP_PREFORMATE_TMP,
				isUTIL_FODS_PREFORMATTING_XSL);
		
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
				osTEMP_XML_TMP,
				isFODS_2_XML_XSL);
		
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
				outputXsl,
				isXML_2_XSL_XSL);
		
		isTEMP_XML_TMP.close();
		isXML_2_XSL_XSL.close();
		logger.info("Leaving Fods2Xsl");
	}
	
	
	
	/**
	 * When every file has been generated, we want to copy them in the /xslt directory to be
	 * used through the Java API.
	 * */
	private static void copyGeneratedFiles() throws Exception {
		String destinationBasePath = System.getProperty("dest");
		logger.info(String.format("Copying generated files to %s", destinationBasePath));       
		try {
			PathUtils.copyDirectory(Path.of(Constants.INPUTS_DDI_FUNCTIONS_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/inputs/ddi"));
			PathUtils.copyDirectory(Path.of(Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/inputs/pogues-xml"));			
			PathUtils.copyDirectory(Path.of(Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/transformations/ddi2xforms"));
			PathUtils.copyDirectory(Path.of(Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/transformations/ddi2fodt"));
			PathUtils.copyDirectory(Path.of(Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/transformations/ddi2fo"));
			PathUtils.copyDirectory(Path.of(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/transformations/ddi2lunatic-xml"));			
			PathUtils.copyDirectory(Path.of(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL_TMP).getParent(),Path.of(destinationBasePath + "/xslt/transformations/pogues-xml2ddi"));

		} catch(Exception e){
			throw e;
		}
	}    
	


	
	/**
	 * Main method of the incorporation target
	 * 
	 * @throws Exception
	 */
	public static void ddi2outIncorporationTarget(String fixed, String drivers, String functions, String treeNavigation, String xsl) throws Exception {
		logger.debug("Entering Incorporation");
	
		// Incorporating ddi2fr-fixed.xsl and drivers into TEMP_TEMP_TMP
        incorporation(fixed, drivers, Constants.TEMP_TEMP_TMP);
		
		// Fusion : functions.xsl and the file TEMP_TEMP_TMP into TEMP_TEMP_BIS_TMP
        incorporation(Constants.TEMP_TEMP_TMP,functions,Constants.TEMP_TEMP_BIS_TMP);
		
		// Fusion : tree-navigations.xsl and the file TEMP_TEMP_BIS_TMP into ddi2fr.xsl
        incorporation(Constants.TEMP_TEMP_BIS_TMP, treeNavigation, xsl);

        // DDI
        incorporationDDI();
        logger.debug("Leaving Incorporation");
	}


        private static void incorporationDDI() throws Exception {
        incorporation(Constants.INPUTS_DDI_SOURCE_FIXED_XSL,Constants.INPUTS_DDI_FUNCTIONS_XSL,Constants.TEMP_TEMP_TMP);
        incorporation(Constants.TEMP_TEMP_TMP, Constants.INPUTS_DDI_TEMPLATES_XSL, Constants.INPUTS_DDI_SOURCE_XSL_TMP);
        }
	

    	private static void incorporation(String incorporation1, String incorporation2, String into) throws Exception {
    		
   		
		// Incorporating ddi2fr-fixed.xsl and drivers into TEMP_TEMP_TMP
		logger.debug(
				"Incorporating " + incorporation1 +
				" and " + incorporation2 +
				" in " + into);
		InputStream isINCORPORATION1 = FileUtils.openInputStream(new File(incorporation1));
		InputStream isUTIL_XSL_INCORPORATION_XSL = Constants.getInputStreamFromPath(Constants.UTIL_XSL_INCORPORATION_XSL);
		OutputStream osINTO = Files.newOutputStream(Path.of(into));
		
		final XslTransformation saxonService = new XslTransformationIncorporation(Path.of(incorporation2).toFile()) {
		};
		
		saxonService.transform(
				isINCORPORATION1,
				osINTO,
				isUTIL_XSL_INCORPORATION_XSL);
		
		isINCORPORATION1.close();
		isUTIL_XSL_INCORPORATION_XSL.close();
		osINTO.close();
	}

   	public static void ddi2xformsIncorporationTarget() throws Exception {
        ddi2outIncorporationTarget(Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_FIXED_XSL, 
        Constants.TRANSFORMATIONS_DDI2XFORMS_DRIVERS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2XFORMS_FUNCTIONS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2XFORMS_TREE_NAVIGATION_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2XFORMS_DDI2XFORMS_XSL_TMP);
       }

    public static void ddi2fodtIncorporationTarget() throws Exception {
        ddi2outIncorporationTarget(Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_FIXED_XSL, 
        Constants.TRANSFORMATIONS_DDI2FODT_DRIVERS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2FODT_FUNCTIONS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2FODT_TREE_NAVIGATION_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL_TMP);
       }

    public static void ddi2foIncorporationTarget() throws Exception {
        ddi2outIncorporationTarget(Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_FIXED_XSL, 
        Constants.TRANSFORMATIONS_DDI2FO_DRIVERS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2FO_FUNCTIONS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2FO_TREE_NAVIGATION_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2FO_DDI2FO_XSL_TMP);
       }

    public static void ddi2lunaticxmlIncorporationTarget() throws Exception {
        ddi2outIncorporationTarget(Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_FIXED_XSL, 
        Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DRIVERS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_FUNCTIONS_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_TREE_NAVIGATION_XSL_TMP, 
        Constants.TRANSFORMATIONS_DDI2LUNATIC_XML_DDI2LUNATIC_XML_XSL_TMP);
       }

	public static void poguesxml2ddiIncorporationTarget() throws Exception {
		logger.debug("Entering Incorporation");
	
		// Incorporating ddi2fr-fixed.xsl and drivers into TEMP_TEMP_TMP
        incorporation(Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_FIXED_XSL, Constants.TRANSFORMATIONS_POGUES_XML2DDI_DRIVERS_XSL_TMP, Constants.TEMP_TEMP_TMP);
		
		// Fusion : functions.xsl and the file TEMP_TEMP_TMP into TEMP_TEMP_BIS_TMP
        incorporation(Constants.TEMP_TEMP_TMP,Constants.TRANSFORMATIONS_POGUES_XML2DDI_FUNCTIONS_XSL_TMP,Constants.TEMP_TEMP_BIS_TMP);
		
		// Fusion : tree-navigations.xsl and the file TEMP_TEMP_BIS_TMP into ddi2fr.xsl
        incorporation(Constants.TEMP_TEMP_BIS_TMP, Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP, Constants.TRANSFORMATIONS_POGUES_XML2DDI_TREE_NAVIGATION_XSL_TMP);

        // PoguesXML
        incorporation(Constants.INPUTS_POGUES_XML_SOURCE_FIXED_XSL, Constants.INPUTS_POGUES_XML_FUNCTIONS_XSL, Constants.TEMP_TEMP_TMP);
        incorporation(Constants.TEMP_TEMP_TMP, Constants.INPUTS_POGUES_XML_TEMPLATES_XSL, Constants.INPUTS_POGUES_XML_SOURCE_XSL_TMP);
        logger.debug("Leaving Incorporation");
	}

	
}
