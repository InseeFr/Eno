package fr.insee.eno.xsl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

import fr.insee.eno.utils.FolderCleaner;

public class FodsToXSLCompiler {
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
		System.out.println("Current log level is:" + logger.getLevel());

		try {

			logger.debug("Before EnoPreprocessing : Cleaning /temp folder");
			cleanService.cleanOneFolder(Constants.TARGET_TEMP_FOLDER);
			logger.debug("/temp folder cleaned");

			logger.debug("EnoPreprocessing : START");
			logger.debug("Fods2Xsl target called for each .fods file");

			// Fods2Xsl for /transformations/ddi/.fods files
			logger.debug("Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_FODS + " -Output : "
					+ Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL);
			fods2XslTarget(Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_FODS, Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL);

			logger.debug("Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS + " -Output : "
					+ Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL);
			fods2XslTarget(Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_FODS,
					Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL);

			logger.debug("Fods2Xsl -Input : " + Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS + " -Output : "
					+ Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL);
			fods2XslTarget(Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_FODS,
					Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL);

			// Fods2Xsl for /output/ddi/.fods files
			logger.debug("Fods2Xsl -Input : " + Constants.INPUTS_DDI_FUNCTIONS_FODS + " -Output : "
					+ Constants.INPUTS_DDI_FUNCTIONS_XSL);
			fods2XslTarget(Constants.INPUTS_DDI_FUNCTIONS_FODS, Constants.INPUTS_DDI_FUNCTIONS_XSL);

			logger.debug("Fods2Xsl -Input : " + Constants.INPUTS_DDI_TEMPLATES_FODS + " -Output : "
					+ Constants.INPUTS_DDI_TEMPLATES_XSL);
			fods2XslTarget(Constants.INPUTS_DDI_TEMPLATES_FODS, Constants.INPUTS_DDI_TEMPLATES_XSL);
			logger.debug("Fods2Xsl : xsl stylesheets created.");

			// Incorporation target : creating ddi2fr.xsl
			logger.debug("Incorporation target : START");
			ddi2frIncorporationTarget();
			logger.debug("Incorporation target : END");
			logger.debug("EnoPreprocessing : END");
		} catch (Exception e) {
			logger.error(e, e);
			logger.debug("Incorporation target : END");
			logger.debug("EnoPreprocessing : END");
			System.exit(0);
		}
	}

	/**
	 * Main method of the fods2xsl target
	 * 
	 * @param inputFodsPath
	 *            : the input fods file
	 * @param outputXslPath
	 *            : the output xsl file to be created
	 * @throws Exception
	 *             : XSL related exceptions
	 */
	public static void fods2XslTarget(String inputFodsPath, String outputXslPath) throws Exception {
		logger.debug("Entering Fods2Xsl");
		// From inputfile.fods to preformate.tmp using preformatting.xsl
		logger.debug("Preformatting : -Input : " + inputFodsPath + " -Output : " + Constants.TEMP_PREFORMATE_TMP
				+ " -Stylesheet : " + Constants.UTIL_FODS_PREFORMATTING_XSL);
		saxonService.transform(inputFodsPath, Constants.UTIL_FODS_PREFORMATTING_XSL, Constants.TEMP_PREFORMATE_TMP);

		// From preformate.tmp to xml.tmp using fods2xml.xsl
		logger.debug("Fods2Xml : -Input : " + Constants.TEMP_PREFORMATE_TMP + " -Output : " + Constants.TEMP_XML_TMP
				+ " -Stylesheet : " + Constants.FODS_2_XML_XSL);
		saxonService.transform(Constants.TEMP_PREFORMATE_TMP, Constants.FODS_2_XML_XSL, Constants.TEMP_XML_TMP);

		// From xml.tmp to inputfile.xsl
		logger.debug("Xml2Xsl : -Input : " + Constants.TEMP_XML_TMP + " -Output : " + outputXslPath + " -Stylesheet : "
				+ Constants.XML_2_XSL_XSL);
		saxonService.transform(Constants.TEMP_XML_TMP, Constants.XML_2_XSL_XSL, outputXslPath);

		logger.debug("Leaving Fods2Xsl");
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
		logger.debug("Incorporating " + Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL + " and "
				+ Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL + " in " + Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_FIXED_XSL,
				Constants.UTIL_XSL_INCORPORATION_XSL, Constants.TEMP_TEMP_TMP,
				Constants.TRANSFORMATIONS_DDI2FR_DRIVERS_XSL);

		logger.debug("Incorporating " + Constants.TEMP_TEMP_TMP + " and "
				+ Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL + " in " + Constants.TEMP_TEMP_BIS_TMP);
		saxonService.transformIncorporation(Constants.TEMP_TEMP_TMP, Constants.UTIL_XSL_INCORPORATION_XSL,
				Constants.TEMP_TEMP_BIS_TMP, Constants.TRANSFORMATIONS_DDI2FR_FUNCTIONS_XSL);

		logger.debug("Incorporating " + Constants.TEMP_TEMP_BIS_TMP + " and "
				+ Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL + " in "
				+ Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL);
		saxonService.transformIncorporation(Constants.TEMP_TEMP_BIS_TMP, Constants.UTIL_XSL_INCORPORATION_XSL,
				Constants.TRANSFORMATIONS_DDI2FR_DDI2FR_XSL, Constants.TRANSFORMATIONS_DDI2FR_TREE_NAVIGATION_XSL);

		// Incorporating source-fixed.xsl, functions.xsl and templates.xsl into
		// source.xsl
		logger.debug("Incorporating " + Constants.INPUTS_DDI_SOURCE_FIXED_XSL + " and "
				+ Constants.INPUTS_DDI_FUNCTIONS_XSL + " in " + Constants.TEMP_TEMP_TMP);
		saxonService.transformIncorporation(Constants.INPUTS_DDI_SOURCE_FIXED_XSL, Constants.UTIL_XSL_INCORPORATION_XSL,
				Constants.TEMP_TEMP_TMP, Constants.INPUTS_DDI_FUNCTIONS_XSL);

		logger.debug("Incorporating " + Constants.TEMP_TEMP_TMP + " and " + Constants.INPUTS_DDI_TEMPLATES_XSL + " in "
				+ Constants.INPUTS_DDI_SOURCE_XSL);
		saxonService.transformIncorporation(Constants.TEMP_TEMP_TMP, Constants.UTIL_XSL_INCORPORATION_XSL,
				Constants.INPUTS_DDI_SOURCE_XSL, Constants.INPUTS_DDI_TEMPLATES_XSL);
		logger.debug("Leaving Incorporation");
	}
}
