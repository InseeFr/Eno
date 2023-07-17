package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.lunatic.model.flat.IVariableType;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import logicalproduct33.VariableType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Variable extends EnoObject {

    public enum CollectionType {COLLECTED, CALCULATED, EXTERNAL}

    /** Variable collection type. */
    private CollectionType collectionType;

    /** Variables doesn't have an identifier in Lunatic. */
    @DDI(contextType = VariableType.class, field = "getIDArray(0).getStringValue()")
    private String id;

    /** In DDI, when a variable is used in a calculated expression, it is referred to through a reference
     * (not by its id). This reference is the 'SourceParameterReference' id in the variable definition.
     * Collected variables directly have a source parameter reference.
     * Calculated variables have it in the 'Binding' in their 'VariableRepresentation'. */
    private String reference;

    /** Variable name. */
    @DDI(contextType = VariableType.class,
            field = "getVariableNameArray(0).getStringArray(0).getStringValue()")
    @Lunatic(contextType = IVariableType.class, field = "setName(#param)")
    private String name;

    /** Measurement unit (in case of some numeric variables). */
    @DDI(contextType = VariableType.class,
            field = "getVariableRepresentation().getValueRepresentation()?.getMeasurementUnit()?.getStringValue()")
    private String unit;

    /** Method to convert a Eno-model CollectionType object (from the enum class)
     * to the value expected in Lunatic-Model. */
    public static VariableTypeEnum lunaticCollectionType(CollectionType enoCollectionType) {
        return VariableTypeEnum.valueOf(enoCollectionType.name()); // (For now names coincide.)
    }

}
