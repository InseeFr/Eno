package fr.insee.eno.core.model;

import datacollection33.QuestionItemType;
import fr.insee.eno.core.annotations.DDI;

public class Question {

    @DDI(contextType = QuestionItemType.class,
            field = "getQuestionItemNameArray(0).getStringArray(0).getStringValue()")
    String name;

    QuestionType type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
