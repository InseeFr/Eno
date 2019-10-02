package fr.insee.eno.params.validation;

import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.parameters.InFormat;
import fr.insee.eno.parameters.OutFormat;
import fr.insee.eno.parameters.Pipeline;

public interface Validator {
	
	ValidationMessage validate(ENOParameters parametersType);

	ValidationMessage validateIn2Out(InFormat inFormat, OutFormat outFormat);
	
	ValidationMessage validatePreProcessings(Pipeline pipeline);

	ValidationMessage validatePostProcessings(Pipeline pipeline);
	

}
