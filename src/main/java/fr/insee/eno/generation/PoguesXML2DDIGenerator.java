package fr.insee.eno.generation;

import java.io.*;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.transform.xsl.XslParameters;
import fr.insee.eno.transform.xsl.XslTransformation;

public class PoguesXML2DDIGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(PoguesXML2DDIGenerator.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_POGUES_XML2DDI_POGUES_XML2DDI_XSL;

	@Override
	public ByteArrayOutputStream generate(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String surveyName) throws Exception {
		logger.info(String.format("%s Target : START",in2out().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 byteArrayInputStream;){

			saxonService.transformPoguesXML2DDI(byteArrayInputStream, byteArrayOutputStream, xslIS, parameters);

		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					in2out(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}

		return byteArrayOutputStream;
	}

	/**
	 * @param finalInput
	 * @return
	 */
	private String getFormNameFolder(File finalInput) {
		String formNameFolder;
		formNameFolder = FilenameUtils.getBaseName(finalInput.getAbsolutePath());
		formNameFolder = FilenameUtils.removeExtension(formNameFolder);
		formNameFolder = formNameFolder.replace(XslParameters.TITLED_EXTENSION, "");
		return formNameFolder;
	}

	public String in2out() {
		return "xml-pogues2ddi";
	}
}
