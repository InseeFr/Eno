package fr.insee.eno.generation;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;

public class IdentityGenerator implements Generator {

	private static final Logger logger = LoggerFactory.getLogger(IdentityGenerator.class);

	@Override
	public File generate(File finalInput, byte[] parameters, String surveyName) throws Exception {
		logger.info("Identity generation : START");
		logger.info("Identity generation : simply copying input file in another file");
		String outputForm = Constants.TEMP_FOLDER_PATH + "/" + surveyName + "/ddi33/out.xml";
		FileUtils.copyFile(finalInput, new File(outputForm));
		logger.info("Identity generation : END");
		return new File(outputForm);
	}

	public String in2out() {
		return "identity";
	}	
}
