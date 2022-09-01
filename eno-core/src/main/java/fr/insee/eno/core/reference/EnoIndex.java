package fr.insee.eno.core.reference;

import fr.insee.eno.core.model.*;
import fr.insee.eno.core.model.question.Question;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Class designed to be used in processing to easily access different kinds of Eno objects. */
@Slf4j
public class EnoIndex {

    // TODO: this class might be split into two parts

    Map<String, EnoIdentifiableObject> index = new HashMap<>();

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

    /** Recursively path through all eno identifiable objects and put them in the index. */
    private void recursiveIndexing(EnoIdentifiableObject enoIdentifiableObject) {
        // Finally done in mapper, TODO: remove this method
    }

    /** Holdall method to fill some useful maps / collections. */
    public void complementaryIndexing(EnoQuestionnaire enoQuestionnaire) {
        //
        enoQuestionnaire.getSequences().forEach(sequence -> sequenceMap.put(sequence.getId(), sequence));
        enoQuestionnaire.getSubsequences().forEach(subsequence -> subsequenceMap.put(subsequence.getId(), subsequence));
        enoQuestionnaire.getSingleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        enoQuestionnaire.getMultipleResponseQuestions().forEach(question -> questionMap.put(question.getId(), question));
        componentMap.putAll(sequenceMap);
        componentMap.putAll(subsequenceMap);
        componentMap.putAll(questionMap);
        //
        enoQuestionnaire.getVariables().forEach(variable -> variableMap.put(variable.getReference(), variable)); //TODO: careful here is Variable class is split
    }

    public void put(String enoObjectId, EnoIdentifiableObject enoIdentifiableObject) {
        index.put(enoObjectId, enoIdentifiableObject);
    }

    public EnoIdentifiableObject get(String enoObjectId) {
        if (! index.containsKey(enoObjectId)) {
            log.debug("No Eno object with id '"+enoObjectId+"' in the index.");
            log.debug("If it should be, make sure that the corresponding object inherits "
                    +EnoIdentifiableObject.class.getSimpleName()+".");
        }
        return index.get(enoObjectId);
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
