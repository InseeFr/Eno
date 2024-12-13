package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionItemType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.InputNumber;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Eno model class to represent numeric questions, that is to say questions where data collected can only be a
 * numeric value.
 * In DDI, it corresponds to a QuestionItem.
 * In Lunatic, it corresponds to the InputNumber component.
 */
@Getter
@Setter
@Context(format = Format.DDI, type = QuestionItemType.class)
@Context(format = Format.LUNATIC, type = InputNumber.class)
public class NumericQuestion extends SingleResponseQuestion {

    /**
     * Minimum value allowed.
     */
    @DDI("getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
            "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
    @Lunatic("setMin(#param)")
    Double minValue;

    /**
     * Maximum value allowed.
     */
    @DDI("getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
            "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
    @Lunatic("setMax(#param)")
    Double maxValue;

    /**
     * Maximum number of decimals authorized during data collected.
     * In DDI, a null value is equivalent to 0.
     * Represented as a BigInteger since it is the type used in both DDI and Lunatic for this property.
     */
    @DDI("getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
    @Lunatic("setDecimals(#param)")
    BigInteger numberOfDecimals;

    /** Unit associated to the collected value.
     * In DDI, this information is not available (even through references) in 'QuestionItem' object.
     * It is mapped in Variable class and moved here by a processing. */
    @Lunatic("setUnit(#param)")
    DynamicLabel unit;

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "INPUT_NUMBER";

}
