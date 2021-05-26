package fr.insee.eno.service;

import java.nio.file.Path;

import fr.insee.eno.parameters.ENOParameters;

public class NamingFileService {
	
	//Insee patterns integration TODO : parameterize file name
	private static final String XFORMS_ZIP_ENTRY_PATH_FORMAT = "orbeon/fr/%s/%s/form/%s";
	private static final String FO_ZIP_ENTRY_PATH_FORMAT = "courrier_type_%s%s%s.fo";
	private static final String DEFAULT_ZIP_ENTRY_PATH_FORMAT = "%s/%s/%s";

	public static String intoOutputZipFilename(String surveyName, Path absolutePath,
			ENOParameters params) {
			
        String name=null;        
        switch (params.getPipeline().getOutFormat()) {
		case XFORMS:
			name = String.format(XFORMS_ZIP_ENTRY_PATH_FORMAT, surveyName, absolutePath.getParent().getFileName(),absolutePath.getFileName());
			break;
		case FO:
			name = String.format(FO_ZIP_ENTRY_PATH_FORMAT, surveyName.replace("-",""),absolutePath.getParent().getParent().getFileName(),params.getParameters().getFoParameters().getAccompanyingMail());
			break;
		default:
			name = String.format(DEFAULT_ZIP_ENTRY_PATH_FORMAT, surveyName, absolutePath.getParent().getParent().getFileName(),absolutePath.getFileName());
			break;
		}
		return name;
	}

}
