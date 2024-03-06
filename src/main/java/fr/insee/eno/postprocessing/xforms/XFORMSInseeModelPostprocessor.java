package fr.insee.eno.postprocessing.xforms;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class XFORMSInseeModelPostprocessor implements Postprocessor {

	private static final Logger logger = LoggerFactory.getLogger(XFORMSInseeModelPostprocessor.class);

	private XslTransformation saxonService = new XslTransformation();

	private static final String styleSheetPath = Constants.UTIL_XFORMS_INSEE_MODEL_XSL;


	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream input, byte[] parameters, String survey) throws Exception {
		return this.process(input, parameters, null, null, null, survey);
	}

	@Override
	public ByteArrayOutputStream process(ByteArrayInputStream input, byte[] parametersFile, byte[] metadata, byte[] specificTreatmentXsl, byte[] mapping, String survey) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		InputStream FO_XSL = Constants.getInputStreamFromPath(styleSheetPath);
		InputStream mappingStream=null;
		if(mapping!=null) {
			mappingStream = new ByteArrayInputStream(mapping);
		}

		try(input; FO_XSL) {
			saxonService.transformInseeModelXforms(input, byteArrayOutputStream, FO_XSL, mappingStream);
		}catch(Exception e) {
			String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
					toString(),
					e.getMessage(),
					Utils.getErrorLocation(styleSheetPath,e));
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		logger.info("End of InseeModel post-processing ");

		return byteArrayOutputStream;

	}

	@Override
	public String toString() {
		return PostProcessing.XFORMS_INSEE_MODEL.name();
	}

}
