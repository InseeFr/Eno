package fr.insee.eno.core.model.variable;

import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.VariableTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.DDI, type = logicalproduct33.VariableType.class)
@Context(format = Format.LUNATIC, type = fr.insee.lunatic.model.flat.VariableType.class)
public abstract class Variable extends EnoObject {

    public enum CollectionType {COLLECTED, CALCULATED, EXTERNAL}

    /** Variable collection type. */
    private CollectionType collectionType;

    /** Variables doesn't have an identifier in Lunatic. */
    @DDI("getIDArray(0).getStringValue()")
    private String id;

    /** In DDI, when a variable is used in a dynamic label, it is referred to through its reference (not by its id),
     * surrounded with a special character.
     * Calculated variables have it in the 'Binding' in their 'VariableRepresentation'. */
    private String reference;
    // TODO: see pairwise DDI with variable 'l0v32sjd': mistake or actual case?

    /** Variable name. */
    @DDI("!getVariableNameList().isEmpty() ? getVariableNameArray(0).getStringArray(0)?.getStringValue() : null")
    @Lunatic("setName(#param)")
    private String name;

    /** Measurement unit (for numeric variables). */
    @DDI("getVariableRepresentation()?.getValueRepresentation()?.getMeasurementUnit()?.getStringValue()")
    private String unit;

    /** Method to convert an Eno-model CollectionType object (from the enum class)
     * to the value expected in Lunatic-Model. */
    public static VariableTypeEnum lunaticCollectionType(CollectionType enoCollectionType) {
        return VariableTypeEnum.valueOf(enoCollectionType.name()); // (For now names coincide.)
    }

}
