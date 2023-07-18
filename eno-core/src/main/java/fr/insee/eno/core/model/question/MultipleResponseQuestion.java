package fr.insee.eno.core.model.question;

import datacollection33.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentMultipleResponseType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = ComponentMultipleResponseType.class)
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
