package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Lunatic;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public abstract class MultipleResponseQuestion extends Question {

    @DDI(contextType = QuestionGridType.class,
            field = "getQuestionGridNameArray(0).getStringArray(0).getStringValue()")
    String name;

    /*
    @DDI(contextType = QuestionItemType.class, field = "getResponseCardinality()?.getMinimumResponses()?.intValue()")
    @Lunatic(contextType = {CheckboxGroup.class, Table.class},
            field = "setMandatory(#param > 0)")
    int minResponse;

    @DDI(contextType = QuestionItemType.class, field = "getResponseCardinality()?.getMaximumResponses()?.intValue()")
    int maxResponse;
    */

}
