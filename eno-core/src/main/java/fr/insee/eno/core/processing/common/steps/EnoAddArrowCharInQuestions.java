package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnoAddArrowCharInQuestions implements ProcessingStep<EnoQuestionnaire> {

    public static final String QUESTION_ARROW_CHAR = "➡";

    private EnoCatalog enoCatalog;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        for (Question question : enoCatalog.getQuestions()) {
            DynamicLabel questionLabel = question.getLabel();
            questionLabel.setValue(QUESTION_ARROW_CHAR + " " + questionLabel.getValue());
        }
    }

}
