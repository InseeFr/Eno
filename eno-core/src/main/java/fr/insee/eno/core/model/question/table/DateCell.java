package fr.insee.eno.core.model.question.table;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.eno.core.model.question.TableCell;
import fr.insee.lunatic.model.flat.Datepicker;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateCell extends TableCell {

    // Note: with current Lunatic-Model implementation, it is impossible to set min & max in this case.
    // TODO: issue sent to Lunatic-Model, come back here when the issue has been solved.

    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0).getMinimumValue().getStringValue()")
    private String minValue;

    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getRangeArray(0).getMaximumValue().getStringValue()")
    private String maxValue;

    @DDI(contextType = QuestionItemType.class, field = "getResponseDomain().getDateFieldFormat().getStringValue()")
    @Lunatic(contextType = Datepicker.class, field = "setDateFormat(#param)")
    private String format;
}