package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.variable.ExternalVariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = fr.insee.pogues.model.ExternalVariableType.class)
@Context(format = Format.DDI, type = fr.insee.ddi.lifecycle33.logicalproduct.VariableType.class)
@Context(format = Format.LUNATIC, type = ExternalVariableType.class)
public class ExternalVariable extends Variable {

    @Lunatic("setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.EXTERNAL;

    /** DDI reference of the variable.
     * For an external variable, it is the variable name itself. */
    @DDI("!getVariableNameList().isEmpty() ? getVariableNameArray(0).getStringArray(0)?.getStringValue() : null")
    private String reference;

    /** See Pogues / Lunatic model documentation. */
    @Pogues("isDeletedOnReset()")
    @Lunatic("setIsDeletedOnReset(#param)")
    private Boolean isDeletedOnReset;

}
