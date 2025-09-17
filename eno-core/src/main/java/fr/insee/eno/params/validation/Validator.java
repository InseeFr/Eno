package fr.insee.eno.params.validation;

import fr.insee.eno.parameters.*;

public interface Validator {
	
	/**
	 * It validates all ENOParameters and it is based on the following function
	 * @param parametersType
	 * @return a ValidationMessage with a message and the boolean if it is valid.
	 */
	ValidationMessage validate(ENOParameters parametersType);

	/**
	 * It validates the combination of InFormat/OutFormat.
	 * @param inFormat
	 * @param outFormat
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

	ValidationMessage validateMode(OutFormat outFormat, Mode mode);

	

}
