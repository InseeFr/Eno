package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
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
public class NumericQuestion extends SingleResponseQuestion {

    /**
     * Minimum value allowed.
     */
    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
    @Lunatic(contextType = InputNumber.class, field = "setMin(#param)")
    Double minValue;

    /**
     * Maximum value allowed.
     */
    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
    @Lunatic(contextType = InputNumber.class, field = "setMax(#param)")
    Double maxValue;

    /**
     * Maximum number of decimals authorized during data collected.
     * In DDI, a null value is equivalent to 0.
     * Represented as a BigInteger since it is the type used in both DDI and Lunatic for this property.
     */
    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
    @Lunatic(contextType = InputNumber.class, field = "setDecimals(#param)")
    BigInteger numberOfDecimals;

    /** Unit associated to the collected value.
     * In DDI, this information is not available (even through references) in 'QuestionItem' object.
     * It is mapped in Variable class and moved here by a processing. */
    @Lunatic(contextType = InputNumber.class, field = "setUnit(#param)")
    String unit;

    /** Lunatic component type property.
     * This should be inserted by Lunatic-Model serializer later on. */
    @Lunatic(contextType = InputNumber.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "INPUT_NUMBER";

}
