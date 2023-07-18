package fr.insee.eno.core.model.response;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ResponseType;
import lombok.Getter;
import lombok.Setter;
import reusable33.ParameterType;

@Getter
@Setter
@Context(format = Format.DDI, type = ParameterType.class)
@Context(format = Format.LUNATIC, type = ResponseType.class)
public class Response extends EnoObject {

    /** Variable name corresponding to the response. */
    @DDI(contextType = ParameterType.class, field = "getParameterNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = ResponseType.class, field = "setName(#param)")
    String variableName;

}
