package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.ItemReference;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.EnoProcessingInterface;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnoAddNumberingInQuestions implements EnoProcessingInterface {

    public static final String QUESTION_NUMBERING_SEPARATOR = ".";

    private final EnoParameters.QuestionNumberingMode mode;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        if (mode != EnoParameters.QuestionNumberingMode.NONE) {
            int questionNumber = 1;
            for (String sequenceId : enoQuestionnaire.getSequences().stream().map(EnoIdentifiableObject::getId).toList()) {
                Sequence sequence = (Sequence) enoQuestionnaire.get(sequenceId);
                for (String componentId : sequence.getSequenceStructure().stream().map(ItemReference::getId).toList()) {
                    EnoComponent component = (EnoComponent) enoQuestionnaire.get(componentId);
                    if (component instanceof Subsequence subsequence) {
                        for (String questionId : subsequence.getSequenceStructure().stream().map(ItemReference::getId).toList()) {
                            Question question = (Question) enoQuestionnaire.get(questionId);
                            DynamicLabel questionLabel = question.getLabel();
                            questionLabel.setValue(questionNumber + QUESTION_NUMBERING_SEPARATOR + " " + questionLabel.getValue());
                            questionNumber ++;
                        }
                    } else {
                        Question question = (Question) enoQuestionnaire.get(componentId);
                        DynamicLabel questionLabel = question.getLabel();
                        questionLabel.setValue(questionNumber + QUESTION_NUMBERING_SEPARATOR + " " + questionLabel.getValue());
                        questionNumber ++;
                    }
                }
                if (mode == EnoParameters.QuestionNumberingMode.SEQUENCE) {
                    questionNumber = 1;
                }
            }
        }
    }

}
