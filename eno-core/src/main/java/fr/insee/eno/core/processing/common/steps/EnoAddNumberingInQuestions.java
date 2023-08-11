package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.processing.ProcessingStep;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class EnoAddNumberingInQuestions implements ProcessingStep<EnoQuestionnaire> {

    public static final String QUESTION_NUMBERING_SEPARATOR = ".";

    private final EnoParameters.QuestionNumberingMode mode;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        if (mode == EnoParameters.QuestionNumberingMode.NONE) {
            return;
        }
        //
        int questionNumber = 1;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            for (String componentId : getSequenceComponentIds(sequence)) {
                EnoComponent component = (EnoComponent) enoQuestionnaire.get(componentId);
                if (component instanceof Subsequence subsequence) {
                    for (String questionId : getSequenceComponentIds(subsequence)) {
                        addNumberInQuestionLabel(enoQuestionnaire, questionNumber, questionId);
                        questionNumber ++;
                    }
                } else {
                    addNumberInQuestionLabel(enoQuestionnaire, questionNumber, componentId);
                    questionNumber ++;
                }
            }
            if (mode == EnoParameters.QuestionNumberingMode.SEQUENCE) {
                questionNumber = 1;
            }
        }
    }

    /**
     * Returns the ordered list of component ids within the sequence or subsequence.
     * @param sequence Sequence or subsequence object.
     * @return The ordered list of component ids within the sequence or subsequence.
     */
    private static List<String> getSequenceComponentIds(AbstractSequence sequence) {
        return sequence.getSequenceStructure().stream().map(StructureItemReference::getId).toList();
    }

    private static void addNumberInQuestionLabel(EnoQuestionnaire enoQuestionnaire, int questionNumber, String questionId) {
        Question question = (Question) enoQuestionnaire.get(questionId);
        DynamicLabel questionLabel = question.getLabel();
        questionLabel.setValue(addNumberInLabel(questionNumber, questionLabel));
    }

    private static String addNumberInLabel(int questionNumber, DynamicLabel questionLabel) {
        return questionNumber + QUESTION_NUMBERING_SEPARATOR + " " + questionLabel.getValue();
    }

}
