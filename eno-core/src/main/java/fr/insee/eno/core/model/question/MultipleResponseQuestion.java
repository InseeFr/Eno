package fr.insee.eno.core.model.question;

import fr.insee.ddi.lifecycle33.datacollection.QuestionGridType;
import fr.insee.eno.core.annotations.Contexts.Context;
import fr.insee.eno.core.annotations.DDI;
import fr.insee.eno.core.annotations.Pogues;
import fr.insee.eno.core.parameter.Format;
import fr.insee.lunatic.model.flat.ComponentMultipleResponseType;
import fr.insee.pogues.model.QuestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Context(format = Format.POGUES, type = QuestionType.class)
@Context(format = Format.DDI, type = QuestionGridType.class)
@Context(format = Format.LUNATIC, type = ComponentMultipleResponseType.class)
public abstract class MultipleResponseQuestion extends Question {

    @Pogues("getName()")
    @DDI("getQuestionGridNameArray(0).getStringArray(0).getStringValue()")
    String name;

    /*
    @DDI("getResponseCardinality()?.getMinimumResponses()?.intValue()")
    @Lunatic("setMandatory(#param > 0)")
    int minResponse;

    @DDI("getResponseCardinality()?.getMaximumResponses()?.intValue()")
    int maxResponse;
    */

}
