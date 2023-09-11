package fr.insee.eno.core.model.question.table;

import datacollection33.GridResponseDomainInMixedType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.BodyCell;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.DDI, type = GridResponseDomainInMixedType.class)
@Context(format = Format.LUNATIC, type = BodyCell.class)
public class DateCell extends TableCell {

    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "DATEPICKER";

    // Note: with current Lunatic-Model implementation, it is impossible to set min & max in this case.
    // TODO: issue sent to Lunatic-Model, come back here when the issue has been solved.

    @DDI("getResponseDomain().getRangeArray(0).getMinimumValue().getStringValue()")
    private String minValue;

    @DDI("getResponseDomain().getRangeArray(0).getMaximumValue().getStringValue()")
    private String maxValue;

    @DDI("getResponseDomain().getDateFieldFormat().getStringValue()")
    @Lunatic("setDateFormat(#param)")
    private String format;

}