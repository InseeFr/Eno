package fr.insee.eno.core.processing.common.steps;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.label.Label;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.ExternalVariable;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.LabelTypeEnum;

import java.math.BigInteger;

public class EnoAddIdentificationSection implements ProcessingStep<EnoQuestionnaire> {

    public static final String IDENTIFICATION_LABEL_VARIABLE = "LABEL_UNITE_ENQUETEE";
    public static final String IDENTIFICATION_COMMENT_VARIABLE = "COMMENT_UE";
    public static final String IDENTIFICATION_SEQUENCE_ID = "BEGIN-QUESTION-SEQ";
    public static final String IDENTIFICATION_SEQUENCE_LABEL = "\"Identification\"";
    public static final String IDENTIFICATION_SUBSEQUENCE_ID = "BEGIN-QUESTION-SUBSEQ";
    public static final String IDENTIFICATION_SUBSEQUENCE_LABEL =
            "\"Identification de votre \" || cast(LABEL_UNITE_ENQUETEE, string)";
    public static final String IDENTIFICATION_QUESTION_ID = "COMMENT-UE-QUESTION";
    public static final String IDENTIFICATION_QUESTION_LABEL =
            "\"Remarque, commentaire sur un changement concernant votre \" || cast(LABEL_UNITE_ENQUETEE, string) || \"\u00a0:\"";
    public static final int IDENTIFICATION_QUESTION_LENGTH = 2000;
    public static final boolean IDENTIFICATION_QUESTION_MANDATORY = false;

    @Override
    public void apply(EnoQuestionnaire enoQuestionnaire) {
        //
        ExternalVariable labelVariable = new ExternalVariable();
        labelVariable.setName(IDENTIFICATION_LABEL_VARIABLE);
        enoQuestionnaire.getVariables().add(labelVariable);
        //
        CollectedVariable commentVariable = new CollectedVariable();
        commentVariable.setName(IDENTIFICATION_COMMENT_VARIABLE);
        commentVariable.setQuestionReference(IDENTIFICATION_QUESTION_ID);
        enoQuestionnaire.getVariables().add(commentVariable);
        //
        Sequence sequence = new Sequence();
        sequence.setId(IDENTIFICATION_SEQUENCE_ID);
        sequence.setLabel(new Label());
        sequence.getLabel().setValue(IDENTIFICATION_SEQUENCE_LABEL);
        sequence.getLabel().setType(LabelTypeEnum.VTL_MD.value());
        sequence.getSequenceStructure().add(
                StructureItemReference.builder().id(IDENTIFICATION_SUBSEQUENCE_ID).type(StructureItemType.SUBSEQUENCE).build());
        enoQuestionnaire.getSequences().add(0, sequence);
        enoQuestionnaire.getIndex().put(IDENTIFICATION_SEQUENCE_ID, sequence);
        //
        Subsequence subsequence = new Subsequence();
        subsequence.setId(IDENTIFICATION_SUBSEQUENCE_ID);
        subsequence.setLabel(new Label());
        subsequence.getLabel().setValue(IDENTIFICATION_SUBSEQUENCE_LABEL);
        subsequence.getLabel().setType(LabelTypeEnum.VTL_MD.value());
        subsequence.getSequenceStructure().add(
                StructureItemReference.builder().id(IDENTIFICATION_QUESTION_ID).type(StructureItemType.QUESTION).build());
        enoQuestionnaire.getSubsequences().add(0, subsequence);
        enoQuestionnaire.getIndex().put(IDENTIFICATION_SUBSEQUENCE_ID, subsequence);
        //
        TextQuestion question = new TextQuestion();
        question.setId(IDENTIFICATION_QUESTION_ID);
        question.setLabel(new DynamicLabel());
        question.getLabel().setValue(IDENTIFICATION_QUESTION_LABEL);
        question.getLabel().setType(LabelTypeEnum.VTL_MD.value());
        question.setLengthType(TextQuestion.qualifyLength(IDENTIFICATION_QUESTION_LENGTH));
        question.setMaxLength(BigInteger.valueOf(IDENTIFICATION_QUESTION_LENGTH));
        question.setMandatory(IDENTIFICATION_QUESTION_MANDATORY);
        enoQuestionnaire.getSingleResponseQuestions().add(0, question);
        enoQuestionnaire.getIndex().put(IDENTIFICATION_QUESTION_ID, question);
    }

}
