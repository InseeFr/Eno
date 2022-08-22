package fr.insee.eno.core.processing;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.TextQuestion;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.utils.RomanNumber;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.insee.eno.core.parameter.EnoParameters.QuestionNumberingMode;

@Slf4j
public class EnoProcessing {

    // TODO: find a way to split this class in smaller parts

    /** In DDI, in declarations / instructions, variable names are replaces by their reference,
     * surrounded by this character. */
    public static final String DECLARATION_REFERENCE_MARKER = "¤";

    public static final String COMMENT_VARIABLE_NAME = "COMMENT_QE";
    public static final String COMMENT_SEQUENCE_ID = "COMMENT-SEQ";
    public static final String COMMENT_SEQUENCE_LABEL = "Commentaire";
    public static final String COMMENT_QUESTION_ID = "COMMENT-QUESTION";
    public static final String COMMENT_QUESTION_LABEL =
            "Avez-vous des remarques concernant l'enquête ou des commentaires\u00a0?";
    public static final boolean COMMENT_QUESTION_MANDATORY = false;
    public static final int COMMENT_QUESTION_LENGTH = 2000;

    public static final String SEQUENCE_NUMBERING_SEPARATOR = " -";
    public static final String QUESTION_NUMBERING_SEPARATOR = ".";
    public static final String QUESTION_ARROW_CHAR = "➡";

    private final EnoParameters parameters;

    Map<String, Sequence> sequenceMap = new HashMap<>();
    Map<String, Subsequence> subsequenceMap = new HashMap<>();
    Map<String, Question> questionMap = new HashMap<>();
    Map<String, EnoComponent> enoComponentMap = new HashMap<>();

    /** In this local map, the key is the reference (not the id). */
    Map<String, Variable> enoVariableMap = new HashMap<>();
    // TODO: maybe replace all these maps by a EnoIndex class to be used in processing class(es)

    public EnoProcessing() {
        this.parameters = new EnoParameters();
    }

    public EnoProcessing(EnoParameters parameters) {
        this.parameters = parameters;
    }

    public void applyProcessing(EnoQuestionnaire enoQuestionnaire) {
        //
        if (parameters.isCommentSection()) addCommentSection(enoQuestionnaire);
        //
        enoQuestionnaire.getSequences().forEach(sequence -> sequenceMap.put(sequence.getId(), sequence));
        enoQuestionnaire.getSubsequences().forEach(subsequence -> subsequenceMap.put(subsequence.getId(), subsequence));
        enoQuestionnaire.getSingleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        enoQuestionnaire.getMultipleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        enoComponentMap.putAll(sequenceMap);
        enoComponentMap.putAll(subsequenceMap);
        enoComponentMap.putAll(questionMap);
        enoQuestionnaire.getVariables().forEach(variable -> enoVariableMap.put(variable.getReference(), variable));
        // (technical processing)
        insertUnitInQuestions(enoQuestionnaire);
        resolveCalculatedExpressions(enoQuestionnaire);
        resolveFilterExpressions(enoQuestionnaire);
        insertFilters(enoQuestionnaire);
        resolveDeclarationLabels(enoQuestionnaire);
        insertDeclarations(enoQuestionnaire);
        resolveControlExpressions(enoQuestionnaire);
        insertControls(enoQuestionnaire);
        /* TODO: warning: in all "insert" methods, parent object is not changed.
        *   2 solutions : set it each time (and then write tests!) or remove the parent attribute that is actually not used yet. */
        //
        modeSelection();
        //
        if (parameters.isSequenceNumbering()) addNumberingInSequences(enoQuestionnaire);
        addNumberingInQuestions(enoQuestionnaire, parameters.getQuestionNumberingMode());
        if (parameters.isArrowCharInQuestions()) addArrowCharInQuestion();
        //
        addMissingVariables(enoQuestionnaire);
    }

    /** In DDI, the 'unit' information is accessible in variables.
     * In Lunatic, this information is required in some numeric questions. */
    private void insertUnitInQuestions(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> variable.getUnit() != null)
                .forEach(variable -> {
                    Question question = questionMap.get(variable.getQuestionReference());
                    if (! (question instanceof NumericQuestion)) {
                        log.warn(String.format(
                                "Variable %s has a unit value '%s', and question reference '%s', " +
                                        "but question '%s' has not been identified as a numeric question. " +
                                        "This question will not have its unit set.",
                                variable, variable.getUnit(), variable.getQuestionReference(), question));
                    } else {
                        ((NumericQuestion) question).setUnit(variable.getUnit());
                    }
                });
    }

    /** Remove elements that does not correspond to the "selected modes" parameter.
     * For now, only declarations and instructions are concerned by mode selection. */
    private void modeSelection() {
        for (EnoComponent enoComponent : enoComponentMap.values()) {
            enoComponent.getDeclarations().removeIf(this::hasNoSelectedMode);
            enoComponent.getInstructions().removeIf(this::hasNoSelectedMode);
        }
    }

    /** Return true if the given instruction matches the selected modes from parameters. */
    private boolean hasNoSelectedMode(DeclarationInterface declaration) {
        return declaration.getModes().stream().noneMatch(parameters.getSelectedModes()::contains);
    }

    /** In DDI instructions / declarations, variables are replaced by a reference surrounded by a special character.
     * This method replaces references by variables name in each instruction / declaration.
     * This method also fills the object's list of variable names used in its label. */
    private void resolveDeclarationLabels(EnoQuestionnaire enoQuestionnaire) {
        // Get all declarations and instructions
        List<DeclarationInterface> declarations = new ArrayList<>(enoQuestionnaire.getDeclarations());
        enoComponentMap.values().forEach(enoComponent -> declarations.addAll(enoComponent.getInstructions()));
        //
        Pattern pattern = Pattern.compile(DECLARATION_REFERENCE_MARKER + "(.+?)"+ DECLARATION_REFERENCE_MARKER);
        for (DeclarationInterface declaration : declarations) {
            String declarationLabel = declaration.getLabel();
            // TODO: we could do what follows a bit neater maybe
            List<String> variableReferences = new ArrayList<>();
            for (Matcher matcher = pattern.matcher(declarationLabel); matcher.find();) {
                String match = matcher.group();
                String variableReference = match.substring(1, match.length()-1);
                variableReferences.add(variableReference);
            }
            for (String variableReference : variableReferences) {
                String variableName = enoVariableMap.get(variableReference).getName();
                declarationLabel = declarationLabel.replace(
                        DECLARATION_REFERENCE_MARKER + variableReference + DECLARATION_REFERENCE_MARKER,
                        variableName);
                declaration.getVariableNames().add(variableName);
            }
            declaration.setLabel(declarationLabel);
        }
    }

    /** Same principle as 'resolveFilterExpressions' method for calculated variables. */
    public void resolveCalculatedExpressions(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.getVariables().stream()
                .filter(variable -> variable.getCollected().equals("CALCULATED")) //TODO: no filter required here when separate list for calculated variables will be implemented
                .forEach(variable -> { //TODO: maybe a refactor is possible (Variable and Filter have similar attributes)
                    String expression = variable.getExpression();
                    for (BindingReference bindingReference : variable.getBindingReferences()) {
                        expression = expression.replace(bindingReference.getId(), bindingReference.getVariableName());
                    }
                    variable.setExpression(expression);
                });
    }

    /** In DDI, VTL expressions contain variables references instead of their name.
     * This method replaces the references with the names. */
    private void resolveFilterExpressions(EnoQuestionnaire enoQuestionnaire) {
        for (Filter filter : enoQuestionnaire.getFilters()) {
            String expression = filter.getExpression();
            for (BindingReference bindingReference : filter.getBindingReferences()) {
                expression = expression.replace(bindingReference.getId(), bindingReference.getVariableName());
            }
            filter.setExpression(expression);
        }
    }

    /** Same principle as 'resolveFilterExpressions' method for controls. */
    private void resolveControlExpressions(EnoQuestionnaire enoQuestionnaire) {
        for (Control control : enoQuestionnaire.getControls()) {
            String expression = control.getExpression();
            for (BindingReference bindingReference : control.getBindingReferences()) {
                expression = expression.replace(bindingReference.getId(), bindingReference.getVariableName());
            }
            control.setExpression(expression);
        }
    }

    /** This method iterates on filters of the given Eno questionnaire, and set the filter expression
     * in each concerned component. */
    private void insertFilters(EnoQuestionnaire enoQuestionnaire) {
        for (Filter filter : enoQuestionnaire.getFilters()) {
            for (String componentId : filter.getComponentReferences()) {
                EnoComponent enoComponent = enoComponentMap.get(componentId);
                enoComponent.setFilter(filter);
            }
        }
    }

    /** Controls are mapped directly in a flat list in the questionnaire object.
     * This processing is intended to insert them into the objects to which they belong.
     * (Controls are placed after the object they belong to in the sequence items lists.)
     * Concerned objects : sequences, subsequences and questions. */
    private void insertControls(EnoQuestionnaire enoQuestionnaire) { // TODO: code is a bit clumsy but works
        //
        Map<String, Control> controlMap = new HashMap<>();
        enoQuestionnaire.getControls().forEach(control -> controlMap.put(control.getId(), control));
        //
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<SequenceItem> sequenceItems = sequence.getSequenceItems();
            if (! sequenceItems.isEmpty()) {
                int bound = sequenceItems.size();
                // Sequence controls
                int i = 0;
                while (i<bound && sequenceItems.get(i).getType() == SequenceItem.SequenceItemType.CONTROL) {
                    sequence.getControls().add(controlMap.get(sequenceItems.get(i).getId()));
                    i ++;
                }
                // Elements (questions, subsequences) in sequence
                while (i < bound) {
                    SequenceItem sequenceItem = sequenceItems.get(i);
                    if (sequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
                        Question question = questionMap.get(sequenceItem.getId());
                        i ++;
                        while (i<bound && sequenceItems.get(i).getType() == SequenceItem.SequenceItemType.CONTROL) {
                            question.getControls().add(controlMap.get(sequenceItems.get(i).getId()));
                            i ++;
                        }
                    }
                    else if (sequenceItem.getType() == SequenceItem.SequenceItemType.SUBSEQUENCE) {
                        Subsequence subsequence = subsequenceMap.get(sequenceItem.getId());
                        List<SequenceItem> subsequenceItems = subsequence.getSequenceItems();
                        if (! subsequenceItems.isEmpty()) {
                            int bound2 = subsequenceItems.size();
                            // Subsequence controls
                            int j = 0;
                            while (j<bound2 && subsequenceItems.get(j).getType() == SequenceItem.SequenceItemType.CONTROL) {
                                subsequence.getControls().add(controlMap.get(subsequenceItems.get(j).getId()));
                                j ++;
                            }
                            // Elements (questions) in subsequence
                            while (j < bound2) {
                                SequenceItem subsequenceItem = subsequenceItems.get(j);
                                if (subsequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
                                    Question question = questionMap.get(subsequenceItem.getId());
                                    j ++;
                                    while (j<bound2 && subsequenceItems.get(j).getType() == SequenceItem.SequenceItemType.CONTROL) {
                                        question.getControls().add(controlMap.get(subsequenceItems.get(j).getId()));
                                        j ++;
                                    }
                                }
                                else { // skip other elements
                                    j ++;
                                }
                            }
                        }
                        i ++;
                    }
                    else { // skip other elements
                        i ++;
                    }
                }
            }
        }
    }

    /** Same idea as in the insertControls function, but for declarations.
     * (Declarations are placed before the object they belong to in the sequence items lists.)
     * Concerned objects : subsequences and questions. */
    private void insertDeclarations(EnoQuestionnaire enoQuestionnaire) { // TODO: code is a bit clumsy but works
        //
        Map<String, Declaration> declarationMap = new HashMap<>();
        enoQuestionnaire.getDeclarations().forEach(declaration -> declarationMap.put(declaration.getId(), declaration));
        //
        for (Sequence sequence : enoQuestionnaire.getSequences()) {
            List<String> declarationIdStack = new ArrayList<>();
            for (SequenceItem sequenceItem : sequence.getSequenceItems()) {
                if (sequenceItem.getType() == SequenceItem.SequenceItemType.DECLARATION) {
                    declarationIdStack.add(sequenceItem.getId());
                }
                if (sequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
                    Question question = questionMap.get(sequenceItem.getId());
                    declarationIdStack.forEach(declarationId ->
                            question.getDeclarations().add(declarationMap.get(declarationId)));
                    declarationIdStack = new ArrayList<>();
                }
                if (sequenceItem.getType() == SequenceItem.SequenceItemType.SUBSEQUENCE) {
                    Subsequence subsequence = subsequenceMap.get(sequenceItem.getId());
                    declarationIdStack.forEach(declarationId ->
                            subsequence.getDeclarations().add(declarationMap.get(declarationId)));
                    declarationIdStack = new ArrayList<>();
                    for (SequenceItem subsequenceItem : subsequence.getSequenceItems()) {
                        if (subsequenceItem.getType() == SequenceItem.SequenceItemType.DECLARATION) {
                            declarationIdStack.add(subsequenceItem.getId());
                        }
                        if (subsequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
                            Question question = questionMap.get(subsequenceItem.getId());
                            declarationIdStack.forEach(declarationId ->
                                    question.getDeclarations().add(declarationMap.get(declarationId)));
                            declarationIdStack = new ArrayList<>();
                        }
                    }
                }
            }
        }
    }

    private void addCommentSection(EnoQuestionnaire enoQuestionnaire) {
        //
        Variable commentVariable = new Variable();
        commentVariable.setName(COMMENT_VARIABLE_NAME);
        commentVariable.setQuestionReference(COMMENT_QUESTION_ID);
        commentVariable.setCollected("COLLECTED"); //TODO: maybe an enum here see Variable class
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
        commentQuestion.setMandatory(COMMENT_QUESTION_MANDATORY);
        commentQuestion.setMaxLength(BigInteger.valueOf(COMMENT_QUESTION_LENGTH));
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
        //
        enoQuestionnaire.setQuestionNumberingMode(mode);
        //
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

    private void addMissingVariables(EnoQuestionnaire enoQuestionnaire) {
        enoQuestionnaire.setMissingVariables(parameters.isMissingVariables());
        if (parameters.isMissingVariables()) {
            log.info("Adding 'missing' variables in questionnaire.");
            //TODO
        }
    }

}
