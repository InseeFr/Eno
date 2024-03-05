package fr.insee.eno.preprocessing;

import java.io.*;

import fr.insee.eno.exception.Utils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PreProcessing;
import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 */
public class DDIMappingPreprocessor implements Preprocessor {

	private static final Logger logger = LoggerFactory.getLogger(DDIMappingPreprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_DDI_MAPPING_XSL;

	/**
	 * Warning the output is not a kind of ddi but juste a mappging file (used later in post proccessing)
	 * @param byteArrayInputStream
	 *            The file to preprocess
	 * @param parameters
	 *            An optional parameters file
	 * @param survey
	 *            An optional parameters file
	 * @param in2out
	 * @return
	 * @throws Exception
	 */
	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream byteArrayInputStream, byte[] parameters, String survey, String in2out) throws Exception {
		logger.info(String.format("%s Target : START",toString().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 byteArrayInputStream;){

			saxonService.transformLunaticXMLToLunaticXMLPost(byteArrayInputStream, byteArrayOutputStream, xslIS);

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
		return PreProcessing.DDI_MAPPING.name();
	}


}
