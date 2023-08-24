package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;
import reusable33.BindingType;

/**
 * This class can map source parameter id to target parameter id (used for table cells essentially)
 */
@Getter
@Setter
@Context(format = Format.DDI, type = BindingType.class)
public class Binding extends EnoObject {

    @DDI("getSourceParameterReference().getIDArray(0).getStringValue()")
    String sourceParameterId;

    @DDI("getTargetParameterReference().getIDArray(0).getStringValue()")
    String targetParameterId;

}
