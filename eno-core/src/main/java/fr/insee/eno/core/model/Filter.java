package fr.insee.eno.core.model;

import datacollection33.IfThenElseTextType;
import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter extends EnoObject {

    @DDI(contextType = IfThenElseTextType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    private String componentReference;

    @DDI(contextType = IfThenElseTextType.class, field = "")
    private String expression;
}
