package fr.insee.eno.params.validation;

import fr.insee.eno.parameters.ENOParameters;

public interface Validator {
	
	boolean validate(byte[] parameters);
	
	boolean validate(ENOParameters parametersType);

}
