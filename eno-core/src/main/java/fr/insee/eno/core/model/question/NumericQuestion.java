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
    double minValue;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
    @Lunatic(contextType = InputNumber.class, field = "setMax(#param)")
    double maxValue;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
    @Lunatic(contextType = InputNumber.class, field = "setDecimals(#param)")
    BigInteger numberOfDecimals;

    // TODO: not available (even through references) in DDI QuestionItem, see Variable
    String unit;

}
