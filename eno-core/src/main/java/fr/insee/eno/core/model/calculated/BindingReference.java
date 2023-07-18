package fr.insee.eno.core.model.calculated;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reusable33.InParameterType;

/** Class to associate a reference id with a variable name in some calculated expressions. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Context(format = Format.DDI, type = InParameterType.class)
public class BindingReference extends EnoObject {

    @DDI("getIDArray(0).getStringValue()")
    private String id;

    /** Name of the referenced variable. */
    @DDI("getParameterNameArray(0).getStringArray(0).getStringValue()")
    private String variableName;

}
