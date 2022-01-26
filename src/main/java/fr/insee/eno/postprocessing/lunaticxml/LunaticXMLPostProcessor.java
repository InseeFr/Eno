package fr.insee.eno.postprocessing.lunaticxml;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.exception.Utils;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.transform.xsl.XslTransformSimplePost;
import fr.insee.eno.transform.xsl.XslTransformation;

	/**
	 * Customization of JS postprocessor.
	 */
	public abstract class LunaticXMLPostProcessor implements Postprocessor {
		
		private static final Logger logger = LoggerFactory.getLogger(LunaticXMLPostProcessor.class);

		public File process(File input, byte[] parameters, String survey, String styleSheetPath, String extension) throws Exception {

			File outputForJSFile = new File(input.getParent(),
					Constants.BASE_NAME_FORM_FILE +
					extension);
			
			logger.debug("Output folder for basic-form : " + outputForJSFile.getAbsolutePath());
			
			XslTransformation saxonService = new XslTransformSimplePost(parameters,this.outPreprocessing());

			InputStream JS_XSL = Constants.getInputStreamFromPath(styleSheetPath);
			InputStream inputStream = FileUtils.openInputStream(input);
			OutputStream outputStream = FileUtils.openOutputStream(outputForJSFile);

			try {saxonService.transform(inputStream,outputStream, JS_XSL);}
			catch(Exception e) {
				String errorMessage = String.format("An error was occured during the %s transformation. %s : %s",
						toString(),
						e.getMessage(),
						Utils.getErrorLocation(styleSheetPath,e));
				logger.error(errorMessage);
				throw new EnoGenerationException(errorMessage);
			}
			
			inputStream.close();
			outputStream.close();
			JS_XSL.close();
			logger.info("End JS externalize variables post-processing");

			return outputForJSFile;
		}
		
		public OutFormat outPreprocessing() {
			return OutFormat.LUNATIC_XML;
		}

	}
