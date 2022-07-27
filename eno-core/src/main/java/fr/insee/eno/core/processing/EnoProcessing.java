package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.utils.RomanNumber;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class EnoProcessing {

    public static final String COMMENT_VARIABLE_NAME = "COMMENT_QE";
    public static final String COMMENT_SEQUENCE_ID = "COMMENT-SEQ";
    public static final String COMMENT_SEQUENCE_LABEL = "Commentaire";
    public static final String COMMENT_QUESTION_ID = "COMMENT-QUESTION";
    public static final String COMMENT_QUESTION_LABEL =
            "Avez-vous des remarques concernant l'enquête ou des commentaires\u00a0?";

    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}

    public static final String SEQUENCE_NUMBERING_SEPARATOR = " -";
    public static final String QUESTION_NUMBERING_SEPARATOR = ".";
    public static final String QUESTION_ARROW_CHAR = "➡";

    private boolean commentSection = true; //TODO: parametrize
    private boolean sequenceNumbering = true; //TODO: parametrize
    private boolean arrowCharInQuestions = true; //TODO: parametrize

    Map<String, Sequence> sequenceMap = new HashMap<>();
    Map<String, Subsequence> subsequenceMap = new HashMap<>();
    Map<String, Question> questionMap = new HashMap<>();

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
        if (commentSection) addCommentSection(enoQuestionnaire);
        //
        enoQuestionnaire.getSequences().forEach(sequence -> sequenceMap.put(sequence.getId(), sequence));
        enoQuestionnaire.getSubsequences().forEach(subsequence -> subsequenceMap.put(subsequence.getId(), subsequence));
        enoQuestionnaire.getSingleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        enoQuestionnaire.getMultipleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        //
        if (sequenceNumbering) addNumberingInSequences(enoQuestionnaire);
        addNumberingInQuestions(enoQuestionnaire, QuestionNumberingMode.SEQUENCE); //TODO: parametrize
        if (arrowCharInQuestions) addArrowCharInQuestion();
    }

    private void addCommentSection(EnoQuestionnaire enoQuestionnaire) {
        //
        Variable commentVariable = new Variable();
        commentVariable.setName(COMMENT_VARIABLE_NAME);
        commentVariable.setQuestionReference(COMMENT_QUESTION_ID);
        enoQuestionnaire.getVariables().add(commentVariable);
        //
        Sequence sequence = new Sequence();
        sequence.setId(COMMENT_SEQUENCE_ID);
        sequence.setLabel(COMMENT_SEQUENCE_LABEL);
        sequence.getComponentReferences().add(COMMENT_QUESTION_ID);
        enoQuestionnaire.getSequences().add(sequence);
        //
        enoQuestionnaire.getSequenceReferences().add(COMMENT_SEQUENCE_ID);
        //
        TextQuestion commentQuestion = new TextQuestion();
        commentQuestion.setId(COMMENT_QUESTION_ID);
        commentQuestion.setName(COMMENT_VARIABLE_NAME);
        commentQuestion.setLabel(COMMENT_QUESTION_LABEL);
        commentQuestion.setMandatory(false);
        commentQuestion.setMaxLength(BigInteger.valueOf(2000));
        commentQuestion.setResponse(new Response());
        commentQuestion.getResponse().setVariableName(COMMENT_VARIABLE_NAME);
        enoQuestionnaire.getSingleResponseQuestions().add(commentQuestion);
    }

    private void addNumberingInSequences(EnoQuestionnaire enoQuestionnaire) {
        int sequenceNumber = 1;
        for (String sequenceId : enoQuestionnaire.getSequenceReferences()) {
            Sequence sequence = sequenceMap.get(sequenceId);
            String sequenceLabel = sequence.getLabel();
            sequence.setLabel(RomanNumber.toRoman(sequenceNumber) + SEQUENCE_NUMBERING_SEPARATOR + " " + sequenceLabel);
            sequenceNumber ++;
        }
    }

    private void addNumberingInQuestions(EnoQuestionnaire enoQuestionnaire, QuestionNumberingMode mode) {
        if (mode != QuestionNumberingMode.NONE) {
            int questionNumber = 1;
            for (String sequenceId : enoQuestionnaire.getSequenceReferences()) {
                Sequence sequence = sequenceMap.get(sequenceId);
                for (String componentId : sequence.getComponentReferences()) {
                    if (subsequenceMap.containsKey(componentId)) {
                        Subsequence subsequence = subsequenceMap.get(componentId);
                        for (String questionId : subsequence.getComponentReferences()) {
                            Question question = questionMap.get(questionId);
                            String questionLabel = question.getLabel();
                            question.setLabel(questionNumber + QUESTION_NUMBERING_SEPARATOR + " " + questionLabel);
                            questionNumber ++;
                        }
                    } else {
                        Question question = questionMap.get(componentId);
                        String questionLabel = question.getLabel();
                        question.setLabel(questionNumber + QUESTION_NUMBERING_SEPARATOR + " " + questionLabel);
                        questionNumber ++;
                    }
                }
                if (mode == QuestionNumberingMode.SEQUENCE) {
                    questionNumber = 1;
                }
            }
        }
    }

    private void addArrowCharInQuestion() {
        for (Question question : questionMap.values()) {
            String questionLabel = question.getLabel();
            question.setLabel(QUESTION_ARROW_CHAR + " " + questionLabel);
        }
    }

}
