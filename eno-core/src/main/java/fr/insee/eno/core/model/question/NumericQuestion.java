package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.InputNumber;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class NumericQuestion extends SingleResponseQuestion {

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
    @Lunatic(contextType = InputNumber.class, field = "setMin(#param)")
    Double minValue;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
    @Lunatic(contextType = InputNumber.class, field = "setMax(#param)")
    Double maxValue;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
    @Lunatic(contextType = InputNumber.class, field = "setDecimals(#param)")
    BigInteger numberOfDecimals;

    /** Unit associated to the collected value.
     * In DDI, this information is not available (even through references) in 'QuestionItem' object.
     * It is mapped in Variable class and moved here by a processing. */
    @Lunatic(contextType = InputNumber.class, field = "setUnit(#param)")
    String unit;

}
