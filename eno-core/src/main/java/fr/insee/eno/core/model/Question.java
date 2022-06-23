package fr.insee.eno.core.model;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Question {

    @DDI(contextType = QuestionItemType.class, field = "getIDArray(0).getStringValue()")
    String id;

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionTextArray(0).getTextContentArray(0).getText().getStringValue()") //TODO: unsafe superclass method call
    String label;

    QuestionType type;

}
