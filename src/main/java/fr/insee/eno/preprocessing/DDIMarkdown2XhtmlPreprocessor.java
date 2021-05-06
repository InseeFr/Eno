package fr.insee.eno.preprocessing;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * DDI preprocessor.
 */
public class DDIMarkdown2XhtmlPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIMarkdown2XhtmlPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_MD2XHTML_XSL;

	@Override
	public File process(File input, byte[] parameters, String survey, String in2out) throws Exception {

		logger.info("DDIMarkdown2XhtmlPreprocessor Target : START");
		String md2xhtmlOutput = FilenameUtils.removeExtension(input.getPath()) + Constants.MD_EXTENSION;
		// ----- mw2xhtml
		logger.debug("Markdown to XHTML : -Input : " + input + " -Output : " + md2xhtmlOutput + " -Stylesheet : "
				+ Constants.UTIL_DDI_MD2XHTML_XSL + " -Parameters : " + Constants.sUB_TEMP_FOLDER(survey));

		InputStream isDDI_MW2XHTML_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		InputStream isInputFile = FileUtils.openInputStream(input);

		OutputStream osTEMP_NULL_TMP = FileUtils.openOutputStream(new File(md2xhtmlOutput));

		try {
			saxonService.transformMw2XHTML(isInputFile, isDDI_MW2XHTML_XSL, osTEMP_NULL_TMP,
					Constants.SUB_TEMP_FOLDER_FILE(survey));
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the Markdown2Xhtml transformation. %s : %s",
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		isInputFile.close();
		isDDI_MW2XHTML_XSL.close();
		osTEMP_NULL_TMP.close();

		// ----- tweak-xhtml-for-ddi
		// tweak-xhtml-for-ddi-input = mw2xhtml-output

		String outputTweakXhtmlForDdi = FilenameUtils.removeExtension(input.getPath()) + Constants.MD2_EXTENSION;

		logger.debug("Tweak-xhtml-for-ddi : -Input : " + md2xhtmlOutput + " -Output : " + outputTweakXhtmlForDdi
				+ " -Stylesheet : " + Constants.UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL + " -Parameters : "
				+ (parameters == null ? "Default parameters" : "Provided parameters"));

		InputStream isTweakXhtmlForDdi = FileUtils.openInputStream(new File(md2xhtmlOutput));
		InputStream isUTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL = Constants
				.getInputStreamFromPath(Constants.UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL);
		OutputStream osTweakXhtmlForDdi = FileUtils.openOutputStream(new File(outputTweakXhtmlForDdi));
		try {
			saxonService.transformTweakXhtmlForDdi(isTweakXhtmlForDdi, isUTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL,
					osTweakXhtmlForDdi, Constants.SUB_TEMP_FOLDER_FILE(survey));
		}catch(Exception e) {
			String errorMessage = "An error was occured during the Markdown2Xhtml transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		isTweakXhtmlForDdi.close();
		isUTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL.close();
		osTweakXhtmlForDdi.close();

		logger.debug("DDIMarkdown2XhtmlPreprocessor : END");
		return new File(outputTweakXhtmlForDdi);

	}

}
