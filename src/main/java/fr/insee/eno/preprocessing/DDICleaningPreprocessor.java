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
 * A DDI specific preprocessor.
 */
public class DDICleaningPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDICleaningPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_CLEANING_XSL;

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String survey, String in2out) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));


		byte[] bytesOfInput = byteArrayInputStream.readAllBytes();
		logger.info("Length");
		logger.info(String.valueOf(bytesOfInput.length));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		File file = File.createTempFile("AAA-eno-",".xml");
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(bytesOfInput);
		}

		ByteArrayInputStream inputStream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 byteArrayInputStream;){
			saxonService.transformCleaning(inputStream, xslIS, byteArrayOutputStream, in2out);

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
		return PreProcessing.DDI_CLEANING.name();
	}


}
