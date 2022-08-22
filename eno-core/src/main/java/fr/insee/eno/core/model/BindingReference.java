package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;
import reusable33.InParameterType;

/** Class to associate a reference id with a variable name in some calculated expressions. */
@Getter
@Setter
public class BindingReference extends EnoObject {

    @DDI(contextType = InParameterType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    /** Name of the referenced variable. */
    @DDI(contextType = InParameterType.class, field = "getParameterNameArray(0).getStringArray(0).getStringValue()")
    private String variableName;

}
