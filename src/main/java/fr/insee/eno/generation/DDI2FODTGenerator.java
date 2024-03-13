package fr.insee.eno.generation;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class DDI2FODTGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(DDI2FODTGenerator.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.TRANSFORMATIONS_DDI2FODT_DDI2FODT_XSL;

	@Override
	public ByteArrayOutputStream generate(InputStream inputStream, byte[] parameters, String surveyName) throws Exception {
		logger.info(String.format("%s Target : START",in2out().toLowerCase()));

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream xslIS = Constants.getInputStreamFromPath(styleSheetPath);
			 inputStream;){
			
			saxonService.transformDDI2FODT(inputStream, byteArrayOutputStream, xslIS, parameters);
		
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
	public String in2out() {
		return "ddi2fodt";
	}
}
