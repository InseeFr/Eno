package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.DDI, type = logicalproduct33.VariableType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.VariableType.class)
public class ExternalVariable extends Variable {

    @Lunatic("setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.EXTERNAL;

    /** DDI reference of the variable.
     * For an external variable, it is the variable name itself. */
    @DDI("!getVariableNameList().isEmpty() ? getVariableNameArray(0).getStringArray(0)?.getStringValue() : null")
    private String reference;

}
