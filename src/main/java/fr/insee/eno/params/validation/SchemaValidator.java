package fr.insee.eno.params.validation;

import java.io.InputStream;

public interface SchemaValidator {
	
	ValidationMessage validate(InputStream paramsIS);
	

}
