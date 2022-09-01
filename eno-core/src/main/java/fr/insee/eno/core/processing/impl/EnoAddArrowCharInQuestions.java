package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.processing.EnoProcessingInterface;

public class EnoAddArrowCharInQuestions implements EnoProcessingInterface {

    public static final String QUESTION_ARROW_CHAR = "➡";

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        for (Question question : enoQuestionnaire.getIndex().getQuestions()) {
            String questionLabel = question.getLabel();
            question.setLabel(QUESTION_ARROW_CHAR + " " + questionLabel);
        }
    }

}
