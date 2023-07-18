package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.IVariableType;
import logicalproduct33.VariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectedVariable extends Variable {

    @Lunatic(contextType = IVariableType.class,
            field = "setVariableType(T(fr.insee.eno.core.model.variable.Variable).lunaticCollectionType(#param))")
    private CollectionType collectionType = CollectionType.COLLECTED;

    /** DDI reference of the variable.
     * For a collected variable, it is the 'SourceParameterReference' id, directly in the variable object. */
    @DDI(contextType = VariableType.class, field = "getSourceParameterReference().getIDArray(0).getStringValue()")
    private String reference;

    /** Reference to the question in which the variable is collected.
     * This property has been removed in Lunatic variables. */
    @DDI(contextType = VariableType.class,
            field = "!#this.getQuestionReferenceList().isEmpty() ? " +
                    "getQuestionReferenceArray(0).getIDArray(0).getStringValue() : null")
    private String questionReference;

}
