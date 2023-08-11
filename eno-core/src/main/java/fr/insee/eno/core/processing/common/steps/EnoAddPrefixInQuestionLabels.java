package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.parameter.EnoParameters.QuestionNumberingMode;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.VtlSyntaxUtils;

import java.util.List;

public class EnoAddPrefixInQuestionLabels implements ProcessingStep<EnoQuestionnaire> {

    private static final String QUESTION_ARROW_CHAR = "➡";
    private static final String QUESTION_NUMBERING_SEPARATOR = ".";

    private final boolean arrowCharInQuestions;
    private final QuestionNumberingMode questionNumberingMode;

    public EnoAddPrefixInQuestionLabels(boolean arrowCharInQuestions, QuestionNumberingMode questionNumberingMode) {
        this.arrowCharInQuestions = arrowCharInQuestions;
        this.questionNumberingMode = questionNumberingMode;
    }

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        if (QuestionNumberingMode.NONE.equals(questionNumberingMode) && !arrowCharInQuestions) {
            return;
        }
        //
        int questionNumber = 1;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            for (String componentId : getSequenceComponentIds(sequence)) {
                EnoComponent component = (EnoComponent) enoQuestionnaire.get(componentId);
                if (component instanceof Subsequence subsequence) {
                    for (String questionId : getSequenceComponentIds(subsequence)) {
                        Question question = (Question) enoQuestionnaire.get(questionId);
                        addPrefixInQuestionLabel(question, questionNumber);
                        questionNumber ++;
                    }
                } else {
                    Question question = (Question) component;
                    addPrefixInQuestionLabel(question, questionNumber);
                    questionNumber ++;
                }
            }
            if (questionNumberingMode == QuestionNumberingMode.SEQUENCE) {
                questionNumber = 1;
            }
        }
    }

    /**
     * Returns the ordered list of component ids within the sequence or subsequence.
     * @param sequence Sequence or subsequence object.
     * @return The ordered list of component ids within the sequence or subsequence.
     */
    private List<String> getSequenceComponentIds(AbstractSequence sequence) {
        return sequence.getSequenceStructure().stream().map(StructureItemReference::getId).toList();
    }

    private void addPrefixInQuestionLabel(Question question, int questionNumber) {
        DynamicLabel questionLabel = question.getLabel();
        questionLabel.setValue(addPrefixInLabel(questionLabel, questionNumber));
    }

    private String addPrefixInLabel(DynamicLabel questionLabel, int questionNumber) {
        StringBuilder prefix = new StringBuilder();
        prefix.append("\"");
        if (arrowCharInQuestions)
            prefix.append(QUESTION_ARROW_CHAR).append(" ");
        if (questionNumberingMode != QuestionNumberingMode.NONE)
            prefix.append(questionNumber).append(QUESTION_NUMBERING_SEPARATOR).append(" ");
        prefix.append("\"");
        return VtlSyntaxUtils.concatenateStrings(prefix.toString(), questionLabel.getValue());
    }

}
