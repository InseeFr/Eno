package fr.insee.eno.postprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * DDI postprocessor.
 */
@Deprecated
public class DDIPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIPostprocessor.class);

	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public File process(File input, byte[] parameters, String survey) throws Exception {
		logger.info("DDIMarkdown2XhtmlPostprocessor Target : START");
		String mw2xhtmlOutput = FilenameUtils.removeExtension(input.getPath()) + Constants.MW_EXTENSION;
		// ----- mw2xhtml
		logger.debug("Markdown to XHTML : -Input : " + input + " -Output : " + mw2xhtmlOutput + " -Stylesheet : "
				+ Constants.UTIL_DDI_MW2XHTML_XSL + " -Parameters : " + Constants.sUB_TEMP_FOLDER(survey));

		InputStream isDDI_MW2XHTML_XSL = Constants.getInputStreamFromPath(Constants.UTIL_DDI_MW2XHTML_XSL);
		InputStream isInputFile = FileUtils.openInputStream(input);

		OutputStream osTEMP_NULL_TMP = FileUtils.openOutputStream(new File(mw2xhtmlOutput));
		saxonService.transformMw2XHTML(isInputFile, isDDI_MW2XHTML_XSL, osTEMP_NULL_TMP,
				Constants.SUB_TEMP_FOLDER_FILE(survey));
		isInputFile.close();
		isDDI_MW2XHTML_XSL.close();
		osTEMP_NULL_TMP.close();

		// ----- tweak-xhtml-for-ddi
		// tweak-xhtml-for-ddi-input = mw2xhtml-output

		String outputTweakXhtmlForDdi = FilenameUtils.removeExtension(input.getPath()) + Constants.FINAL_DDI_EXTENSION;

		logger.debug("Tweak-xhtml-for-ddi : -Input : " + mw2xhtmlOutput + " -Output : " + outputTweakXhtmlForDdi
				+ " -Stylesheet : " + Constants.UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL + " -Parameters : "
				+ (parameters == null ? "Default parameters" : "Provided parameters"));

		InputStream isTweakXhtmlForDdi = FileUtils.openInputStream(new File(mw2xhtmlOutput));
		InputStream isUTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL = Constants
				.getInputStreamFromPath(Constants.UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL);
		OutputStream osTweakXhtmlForDdi = FileUtils.openOutputStream(new File(outputTweakXhtmlForDdi));
		saxonService.transformTweakXhtmlForDdi(isTweakXhtmlForDdi, isUTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL,
				osTweakXhtmlForDdi, Constants.SUB_TEMP_FOLDER_FILE(survey));
		isTweakXhtmlForDdi.close();
		isUTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL.close();
		osTweakXhtmlForDdi.close();

		logger.debug("DDIMarkdown2XhtmlPostprocessor : END");
		return new File(outputTweakXhtmlForDdi);

	}

}
