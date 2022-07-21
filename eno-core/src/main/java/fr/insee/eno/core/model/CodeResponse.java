package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.ResponsesCheckboxGroup;
import lombok.Getter;
import lombok.Setter;
import reusable33.ParameterType;

@Getter
@Setter
public class CodeResponse extends EnoObject {

    @DDI(contextType = ParameterType.class, field = "getIDArray(0).getStringValue()")
    @Lunatic(contextType = ResponsesCheckboxGroup.class, field = "setId(#param)")
    private String id;

    /*
    @DDI(contextType = ParameterType.class,
            field = "#index.get(#parent.getId())" +
                    ".getLabelArray(0).getContentArray(0).getStringValue()")
    @Lunatic(contextType = ResponsesCheckboxGroup.class, field = "setLabel(#param)")
    private String label;
    */ //TODO: AbstractEnoObject with a 'parent' field?

    @DDI(contextType = ParameterType.class, field = "#this")
    @Lunatic(contextType = ResponsesCheckboxGroup.class, field = "setResponse(#param)")
    Response response;

}
