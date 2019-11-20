package fr.insee.eno.params.validation;

import java.io.InputStream;

public interface SchemaValidator {
	
	/**
	 * 
	 * @param paramsIS
	 * @return
	 */
	ValidationMessage validate(InputStream paramsIS);
	

}
