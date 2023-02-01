package fr.insee.eno.core.reference;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.model.variable.Variable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Class designed to be used in processing to easily access different kinds of Eno objects. */
public class EnoCatalog {

    /** Map of sequences stored bt id. */
    Map<String, Sequence> sequenceMap = new HashMap<>();
    /** Map of subsequences stored bt id. */
    Map<String, Subsequence> subsequenceMap = new HashMap<>();
    /** Map of questions stored bt id. */
    Map<String, Question> questionMap = new HashMap<>();
    /** Map of Eno components stored bt id. */
    Map<String, EnoComponent> componentMap = new HashMap<>();
    /** Map of collected variables stored by their reference (warning: keys = not ids). */
    Map<String, Variable> variableMap = new HashMap<>();

    public EnoCatalog(EnoQuestionnaire enoQuestionnaire) {
        complementaryIndexing(enoQuestionnaire);
    }

    /** Holdall method to fill some useful maps / collections. */
    private void complementaryIndexing(EnoQuestionnaire enoQuestionnaire) {
        //
        enoQuestionnaire.getSequences().forEach(sequence -> sequenceMap.put(sequence.getId(), sequence));
        enoQuestionnaire.getSubsequences().forEach(subsequence -> subsequenceMap.put(subsequence.getId(), subsequence));
        enoQuestionnaire.getSingleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        enoQuestionnaire.getMultipleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        componentMap.putAll(sequenceMap);
        componentMap.putAll(subsequenceMap);
        componentMap.putAll(questionMap);
        //
        enoQuestionnaire.getVariables().forEach(variable -> variableMap.put(variable.getReference(), variable)); //TODO: careful here if Variable class is split
    }

    public Sequence getSequence(String sequenceId) {
        return sequenceMap.get(sequenceId);
    }
    public Subsequence getSubsequence(String subsequenceId) {
        return subsequenceMap.get(subsequenceId);
    }
    public Question getQuestion(String questionId) {
        return questionMap.get(questionId);
    }
    public EnoComponent getComponent(String componentId) {
        return componentMap.get(componentId);
    }
    public Variable getVariable(String variableReference) {
        return variableMap.get(variableReference);
    }

    public Collection<Question> getQuestions() {
        return questionMap.values();
    }
    public Collection<EnoComponent> getComponents() {
        return componentMap.values();
    }

}
