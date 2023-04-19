package fr.insee.eno.core.model.question;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateQuestion extends SingleResponseQuestion {

    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0)?.getMinimumValue()?.getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setMin(#param)")
    private String minValue;

    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0)?.getMaximumValue()?.getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setMax(#param)")
    private String maxValue;

    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getDateFieldFormat().getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setDateFormat(#param)")
    private String format;

    @Lunatic(contextType = Datepicker.class,
            field = "setComponentType(T(fr.insee.lunatic.model.flat.ComponentTypeEnum).valueOf(#param))")
    String lunaticComponentType = "DATEPICKER";
}
