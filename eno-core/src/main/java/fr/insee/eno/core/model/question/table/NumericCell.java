package fr.insee.eno.core.model.question.table;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.BodyCell;
import fr.insee.lunatic.model.flat.InputNumber;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class NumericCell extends TableCell {
    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getLow()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getLow().getStringValue()) : null")
    @Lunatic(contextType = BodyCell.class, field = "setMin(#param)")
    Double minValue;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getNumberRangeList()?.get(0)?.getHigh()?.getStringValue() != null ? " +
                    "T(java.lang.Double).valueOf(getResponseDomain().getNumberRangeArray(0).getHigh().getStringValue()) : null")
    @Lunatic(contextType = BodyCell.class, field = "setMax(#param)")
    Double maxValue;

    @DDI(contextType = QuestionItemType.class,
            field = "getResponseDomain()?.getDecimalPositions() ?: T(java.math.BigInteger).valueOf('0')")
    @Lunatic(contextType = BodyCell.class, field = "setDecimals(#param)")
    BigInteger numberOfDecimals;

    // DDI processing for this attribute is done in a processing
    @Lunatic(contextType = InputNumber.class, field = "setUnit(#param)")
    String unit;
}
