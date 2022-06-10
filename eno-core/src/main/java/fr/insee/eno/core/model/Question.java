package fr.insee.eno.core.model;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(of="name")
public class Question {

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    @Setter
    @Getter
    private String name;

    private QuestionType type;


}
