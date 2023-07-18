package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.IVariableType;
import logicalproduct33.VariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalVariable extends Variable {

    @Lunatic(contextType = IVariableType.class,
            field = "setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.EXTERNAL;

    /** DDI reference of the variable.
     * For an external variable, it is the variable name itself. */
    @DDI(contextType = VariableType.class, field = "getVariableNameArray(0).getStringArray(0).getStringValue()")
    private String reference;

}
