package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.QuestionNumberingMode;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.VtlSyntaxUtils;

import java.util.List;

public class EnoAddPrefixInQuestionLabels implements ProcessingStep<EnoQuestionnaire> {

    private static final String QUESTION_ARROW_CHAR = "➡";
    private static final String QUESTION_NUMBERING_SEPARATOR = ".";

    private final boolean arrowCharInQuestions;
    private final QuestionNumberingMode questionNumberingMode;
    private final EnoParameters.ModeParameter modeParameter;
    private final Format outFormat;

    private int questionNumber;

    public EnoAddPrefixInQuestionLabels(boolean arrowCharInQuestions, QuestionNumberingMode questionNumberingMode,
                                        EnoParameters.ModeParameter modeParameter, Format outFormat) {
        this.arrowCharInQuestions = arrowCharInQuestions;
        this.questionNumberingMode = questionNumberingMode;
        this.modeParameter = modeParameter;
        this.outFormat = outFormat;
    }

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        assert enoQuestionnaire.getIndex() != null;
        //
        if (prefixingDisabled())
            return;
        //
        questionNumber = 1;
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            for (String componentId : getSequenceComponentIds(sequence)) {
                EnoComponent component = (EnoComponent) enoQuestionnaire.get(componentId);
                if (component instanceof Subsequence subsequence) {
                    for (String questionId : getSequenceComponentIds(subsequence)) {
                        Question question = (Question) enoQuestionnaire.get(questionId);
                        addPrefixInQuestionLabel(question);
                    }
                } else {
                    Question question = (Question) component;
                    addPrefixInQuestionLabel(question);
                }
            }
            if (questionNumberingMode == QuestionNumberingMode.SEQUENCE) {
                questionNumber = 1;
            }
        }
    }

    private boolean prefixingDisabled() {
        return QuestionNumberingMode.NONE.equals(questionNumberingMode) && !arrowCharInQuestions;
    }

    /**
     * Returns the ordered list of component ids within the sequence or subsequence.
     * @param sequence Sequence or subsequence object.
     * @return The ordered list of component ids within the sequence or subsequence.
     */
    private List<String> getSequenceComponentIds(AbstractSequence sequence) {
        return sequence.getSequenceStructure().stream().map(StructureItemReference::getId).toList();
    }

    public void addPrefixInQuestionLabel(Question question) {
        if (prefixingDisabled())
            return;
        DynamicLabel questionLabel = question.getLabel();
        questionLabel.setValue(addPrefixInLabel(questionLabel.getValue(), questionNumber));
        questionNumber ++;
    }

    private String addPrefixInLabel(String questionLabelValue, int questionNumber) {
        return switch (modeParameter) {
            case CAPI, CATI, CAWI, PROCESS -> prefixingDynamicMode(questionLabelValue, questionNumber);
            case PAPI -> prefixingStaticMode(questionLabelValue, questionNumber);
        };
    }

    /** VTL prefixing using VTL string concatenation operator. */
    private String prefixingDynamicMode(String questionLabelValue, int questionNumber) {
        StringBuilder prefix = new StringBuilder();
        prefix.append("\"");
        if (arrowCharInQuestions)
            prefix.append(QUESTION_ARROW_CHAR).append(" ");
        if (questionNumberingMode != QuestionNumberingMode.NONE)
            prefix.append(questionNumber).append(questionNumberingSeparator(outFormat)).append(" ");
        prefix.append("\"");
        return VtlSyntaxUtils.concatenateStrings(prefix.toString(), questionLabelValue);
    }

    private static String questionNumberingSeparator(Format outFormat) {
        // In Lunatic the dot need to be escaped for markdown interpretation
        if (Format.LUNATIC.equals(outFormat))
            return "\\" + QUESTION_NUMBERING_SEPARATOR;
        return QUESTION_NUMBERING_SEPARATOR;
    }

    /** Raw string prefixing for the paper format where there is no VTL.
     * Also removes eventual quotes that might be in the label. */
    private String prefixingStaticMode(String questionLabelValue, int questionNumber) {
        StringBuilder prefix = new StringBuilder();
        if (arrowCharInQuestions)
            prefix.append(QUESTION_ARROW_CHAR).append(" ");
        if (questionNumberingMode != QuestionNumberingMode.NONE)
            prefix.append(questionNumber).append(QUESTION_NUMBERING_SEPARATOR).append(" ");
        return prefix + questionLabelValue.replace("\"", "");
    }

}
