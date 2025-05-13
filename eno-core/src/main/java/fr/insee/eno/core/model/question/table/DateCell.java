package fr.insee.eno.core.model.question.table;

import fr.insee.ddi.lifecycle33.datacollection.GridResponseDomainInMixedType;
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
public class DateCell extends ResponseCell {

    @Lunatic("setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "DATEPICKER";

    @DDI("getResponseDomain().getRangeArray(0).getMinimumValue().getStringValue()")
    @Lunatic("setMin(#param)")
    private String minValue;

    @DDI("getResponseDomain().getRangeArray(0).getMaximumValue().getStringValue()")
    @Lunatic("setMax(#param)")
    private String maxValue;

    @DDI("getResponseDomain().getDateFieldFormat().getStringValue()")
    @Lunatic("setDateFormat(#param)")
    private String format;

}