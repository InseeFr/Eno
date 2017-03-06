package fr.insee.eno.preprocessing;

import fr.insee.eno.transform.xsl.XslTransformation;

/**
 * A DDI specific preprocessor.
 * */
public class DDIPreprocessor implements Preprocessor {
	
	// FIXME Inject !
	private static XslTransformation saxonService = new XslTransformation();

	@Override
	public String process(String inputFile, String parametersFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
