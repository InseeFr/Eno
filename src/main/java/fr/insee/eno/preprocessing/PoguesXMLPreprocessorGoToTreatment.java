package fr.insee.eno.preprocessing;

import java.io.*;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A PoguesXML specific preprocessor : other goto2ite (rc version).
 */
public class PoguesXMLPreprocessorGoToTreatment implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(PoguesXMLPreprocessorGoToTreatment.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_POGUES_XML_GOTO_ITE_XSL;

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String survey, String in2out) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 byteArrayInputStream;){

			saxonService.transform(byteArrayInputStream, xslIS, byteArrayOutputStream);

		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		return byteArrayOutputStream;
	}

	public String toString() {
		return PreProcessing.POGUES_XML_GOTO_2_ITE.name();
	}


}
