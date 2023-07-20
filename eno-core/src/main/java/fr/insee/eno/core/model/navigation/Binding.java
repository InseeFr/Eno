package fr.insee.eno.core.model.navigation;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.model.EnoObject;
import lombok.Getter;
import lombok.Setter;
import reusable33.BindingType;

/**
 * This class can map source parameter id to target parameter id (used for table cells essentially)
 */
@Getter
@Setter
public class Binding extends EnoObject {

    @DDI(contextType = BindingType.class,
            field = "getSourceParameterReference().getIDArray(0).getStringValue()")
    String sourceParameterId;

    @DDI(contextType = BindingType.class,
            field = "getTargetParameterReference().getIDArray(0).getStringValue()")
    String targetParameterId;
}
