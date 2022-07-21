package fr.insee.eno.core.model;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.IVariableType;
import logicalproduct33.VariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Variable extends EnoObject {

    @DDI(contextType = VariableType.class,
            field = "getVariableNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = IVariableType.class, field = "setName(#param)")
    private String name;

    @DDI(contextType = VariableType.class, field = "getQuestionReferenceArray(0).getIDArray(0).getStringValue()")
    @Lunatic(contextType = IVariableType.class, field = "setComponentRef(#param)")
    String questionReference;

    @DDI(contextType = VariableType.class,
            field = "#this instanceof T(reusable33.NumericRepresentationBaseType) ? " +
                    "getVariableRepresentation()?.getMeasurementUnit()?.getStringValue() : null")
    String unit;

}
