package fr.insee.eno.service;

import fr.insee.eno.parameters.ENOParameters;

import java.nio.file.Path;

public class NamingFileService {
	
	//Insee patterns integration TODO : parameterize file name
	private static final String XFORMS_ZIP_ENTRY_PATH_FORMAT = "orbeon/fr/%s/%s/form/form.xhtml";
	private static final String FO_ZIP_ENTRY_PATH_FORMAT = "courrier_type_%s%s%s.fo";
	private static final String DEFAULT_ZIP_ENTRY_PATH = "%s/%s/form.xml";

	public static String intoOutputZipFilename(String surveyName, String modelName, ENOParameters params) {

		String name=null;
		switch (params.getPipeline().getOutFormat()) {
			case XFORMS:
				name = String.format(
						XFORMS_ZIP_ENTRY_PATH_FORMAT,
						surveyName,
						modelName);
				break;
			case FO:
				name = String.format(
						FO_ZIP_ENTRY_PATH_FORMAT,
						surveyName.replace("-",""),
						modelName,
						params.getParameters().getFoParameters().getAccompanyingMail().value());
				break;
			default:
				name = String.format(
						DEFAULT_ZIP_ENTRY_PATH,
						surveyName, modelName);
				break;
		}
		return name;
	}

}
