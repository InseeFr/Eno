package fr.insee.eno.params;

import java.io.InputStream;

import fr.insee.eno.parameters.ENOParameters;

public interface ValorizatorParameters {
	
	String setParameters(ENOParameters enoParameters);
	
	InputStream setparameters(ENOParameters enoParameters);

}
