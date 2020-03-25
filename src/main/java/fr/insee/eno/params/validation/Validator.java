package fr.insee.eno.params.validation;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;

public interface Validator {
	
	/**
	 * It validates all ENOParameters and it is based on the following function
	 * @param pipeline
	 * @return a ValidationMessage with a message and the boolean if it is valid.
	 */
	ValidationMessage validate(ENOParameters parametersType);

	/**
	 * It validates the combination of InFormat/OutFormat.
	 * @param pipeline
	 * @return a ValidationMessage with a message and the boolean if it is valid.
	 */
	ValidationMessage validateIn2Out(InFormat inFormat, OutFormat outFormat);
	
	/**
	 * It validates PreProcessing according to the pipeline.
	 * @param pipeline
	 * @return a ValidationMessage with a message and the boolean if it is valid.
	 */
	ValidationMessage validatePreProcessings(Pipeline pipeline);

	/**
	 * It validates PostProcessing according to the pipeline.
	 * @param pipeline
	 * @return a ValidationMessage with a message and the boolean if it is valid.
	 */
	ValidationMessage validatePostProcessings(Pipeline pipeline);
	

}
