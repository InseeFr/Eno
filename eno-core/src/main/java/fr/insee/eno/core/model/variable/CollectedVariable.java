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
public class CollectedVariable extends Variable {

    @Lunatic("setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.COLLECTED;

    /** DDI reference of the variable.
     * For a collected variable, it is the 'SourceParameterReference' id, directly in the variable object. */
    @DDI("getSourceParameterReference().getIDArray(0).getStringValue()")
    private String reference;

    /** Reference to the question in which the variable is collected.
     * This property has been removed in Lunatic variables. */
    @DDI("!#this.getQuestionReferenceList().isEmpty() ? " +
            "getQuestionReferenceArray(0).getIDArray(0).getStringValue() : null")
    private String questionReference;

}
