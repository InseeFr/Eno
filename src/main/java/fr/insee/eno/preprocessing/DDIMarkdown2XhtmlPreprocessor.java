package fr.insee.eno.preprocessing;

import java.io.*;

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

	private static final String styleSheetPath_1 = Constants.UTIL_DDI_MD2XHTML_XSL;
	private static final String styleSheetPath_2 = Constants.UTIL_DDI_TWEAK_XHTML_FOR_DDI_XSL;


	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String survey, String in2out) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStreamTemp = subProcess(
				byteArrayInputStream,
				styleSheetPath_1);
		ByteArrayOutputStream byteArrayOutputStream = subProcess(
				new ByteArrayInputStream(byteArrayOutputStreamTemp.toByteArray()),
				styleSheetPath_2
		);
		byteArrayOutputStreamTemp.close();

		return byteArrayOutputStream;
	}

	public ByteArrayOutputStream subProcess(ByteArrayInputStream byteArrayInputStream, String styleSheetPath) throws Exception {
		logger.info(String.format("%s Target : step1 START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath_1);
			 byteArrayInputStream){

			saxonService.transformLunaticXMLToLunaticXMLPost(byteArrayInputStream, byteArrayOutputStream, xslIS);

		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath_1,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		return byteArrayOutputStream;
	}

}
