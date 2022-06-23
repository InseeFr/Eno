package fr.insee.eno.core.model;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Question {

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    QuestionType type;

}
