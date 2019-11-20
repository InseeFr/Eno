package fr.insee.eno.params.validation;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;

public interface Validator {
	
	/**
	 * 
	 * @param parametersType
	 * @return
	 */
	ValidationMessage validate(ENOParameters parametersType);

	/**
	 * 
	 * @param inFormat
	 * @param outFormat
	 * @return
	 */
	ValidationMessage validateIn2Out(InFormat inFormat, OutFormat outFormat);
	
	/**
	 * 
	 * @param pipeline
	 * @return
	 */
	ValidationMessage validatePreProcessings(Pipeline pipeline);

	/**
	 * 
	 * @param pipeline
	 * @return
	 */
	ValidationMessage validatePostProcessings(Pipeline pipeline);
	

}
