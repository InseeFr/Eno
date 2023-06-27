package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.SequenceItem;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/** Lunatic technical processing for loops.
 * Requires: sorted components, hierarchy. */
@Slf4j
public class LunaticLoopResolution2 implements OutProcessingInterface<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private EnoIndex enoIndex;

    public LunaticLoopResolution2(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        enoIndex = enoQuestionnaire.getIndex();
        //
        enoQuestionnaire.getLoops().forEach(enoLoop -> {
            Loop lunaticLoop = new Loop();
            insertLoopComponent(lunaticQuestionnaire, lunaticLoop, enoLoop.getSequenceReference());
            insertEnoLoopInfo(lunaticLoop, enoLoop);
        });
    }

    /** Replace components that are in the referenced sequence or subsequence
     * by a loop object containing these (including the sequence component itself). */
    private void insertLoopComponent(
            Questionnaire lunaticQuestionnaire, Loop lunaticLoop, String sequenceReference) {
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Iterator<ComponentType> iterator = components.iterator();
        // First iterate until we find the referenced sequence
        while (iterator.hasNext()) {
            ComponentType component = iterator.next();
            // Then transfer concerned components in the loop component
            if (sequenceReference.equals(component.getId())) {
                // Check that the reference is actually a sequence or subsequence
                assert component instanceof SequenceType || component instanceof Subsequence;
                // Insert the sequence/subsequence (first element of the loop)
                lunaticLoop.getComponents().add(component);
                iterator.remove();
                insertComponentsInLoop(lunaticQuestionnaire, lunaticLoop, sequenceReference);
                break;
            }
        }
        components.add(lunaticLoop);
    }

    /** Insert components that belongs to the loop, in the right order, using Eno sequence object.
     * FIXME: this will not work if some components in the loop are filtered (see getComponentReferences method comments)
     * */
    private void insertComponentsInLoop(
            Questionnaire lunaticQuestionnaire, Loop lunaticLoop, String sequenceReference) {
         AbstractSequence enoSequence = (AbstractSequence) enoIndex.get(sequenceReference);
         enoSequence.getComponentReferences().forEach(componentReference ->
                 relocateComponent(lunaticQuestionnaire, lunaticLoop, componentReference));
    }

    /** Relocate the component with given reference (id) from the questionnaire's components
     * to the loop's components. */
    private void relocateComponent(Questionnaire lunaticQuestionnaire, Loop lunaticLoop, String componentReference) {
        // Find the component and remove it from questionnaire's components
        Iterator<ComponentType> iterator = lunaticQuestionnaire.getComponents().iterator();
        ComponentType searchedComponent = null;
        while (iterator.hasNext()) {
            searchedComponent = iterator.next();
            if (componentReference.equals(searchedComponent.getId())) {
                iterator.remove();
                break;
            }
        }
        // Insert the component in the loop
        if (searchedComponent == null) {
            throw new MappingException("Unable to find component "+componentReference); // TODO: more precise message
        }
        lunaticLoop.getComponents().add(searchedComponent);
    }

    private void insertEnoLoopInfo(Loop lunaticLoop, fr.insee.eno.core.model.navigation.Loop enoLoop) {
        //
        lunaticLoop.setId(enoLoop.getId());
        lunaticLoop.setDepth(BigInteger.ONE); // Note: Nested loops is not supported yet
        // Condition filter of the loop will is the same as its first component
        if (lunaticLoop.getComponents().isEmpty()) {
            log.warn("Loop '{}' is empty (weird).", lunaticLoop.getId());
        }
        lunaticLoop.setConditionFilter(lunaticLoop.getComponents().get(0).getConditionFilter());
        // TODO: is hierarchy useful in Loop components? (not sure)
        //
        if (enoLoop instanceof StandaloneLoop standaloneLoop) {
            standaloneLoopMapping(lunaticLoop, standaloneLoop);
        }
        if (enoLoop instanceof LinkedLoop linkedLoop) {
            linkedLoopMapping(lunaticLoop, linkedLoop);
        }
    }

    /** In case of a standalone loop: "min" and "max" lines. */
    private static void standaloneLoopMapping(Loop lunaticLoop, StandaloneLoop enoStandaloneLoop) {
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticLoop.setLines(new LinesLoop());
        LabelType minExpression = new LabelType();
        LabelType maxExpression = new LabelType();
        lunaticMapper.mapEnoObject(enoStandaloneLoop.getMinIteration(), minExpression);
        lunaticMapper.mapEnoObject(enoStandaloneLoop.getMaxIteration(), maxExpression);
        lunaticLoop.getLines().setMin(minExpression);
        lunaticLoop.getLines().setMax(maxExpression);
    }

    /** In case of linked loop: "iterations".
     * TODO: Issue on current Lunatic conception around this. To be addressed later on.
     * */
    private void linkedLoopMapping(Loop lunaticLoop, LinkedLoop enoLinkedLoop) {
        // We "just" want to find the first variable in the scope of the reference loop
        EnoIdentifiableObject reference = enoIndex.get(enoLinkedLoop.getReference());
        //
        if (reference instanceof StandaloneLoop) {
            AbstractSequence sequence = (AbstractSequence) enoIndex.get(enoLinkedLoop.getSequenceReference());
            String firstQuestionId = findFirstQuestionId(sequence, enoLinkedLoop);
            Question firstQuestion = (Question) enoIndex.get(firstQuestionId);
            if (firstQuestion instanceof SingleResponseQuestion singleResponseQuestion) {
                lunaticLoop.setIterations(new LabelType());
                lunaticLoop.getIterations().setValue(
                        "count("+singleResponseQuestion.getResponse().getVariableName()+")");
            } else {
                lunaticLoop.setIterations(new LabelType());
                log.warn("Linked loop '{}' is based on loop '{}' that starts at sequence '{}'. " +
                        "This first question of the sequence is not a \"simple\" question. " +
                        "The linked loop will not work as expected.",
                        enoLinkedLoop.getId(), enoLinkedLoop.getId(), enoLinkedLoop.getSequenceReference());
                lunaticLoop.getIterations().setValue("1");
            }
        }
        //
        else if (reference instanceof DynamicTableQuestion) {
            log.warn("Linked loop '{}' is based on a dynamic table. This feature is not supported yet.",
                    enoLinkedLoop.getId());
            lunaticLoop.setIterations(new LabelType());
            lunaticLoop.getIterations().setValue("1");
        }
        //
        else {
            log.warn("Linked loop '{}' reference object's '{}' is neither a loop nor a dynamic table.",
                    enoLinkedLoop.getId(), reference);
        }
    }

    /**
     * Return the id of the first question in given sequence or subsequence object.
     * @param sequence Eno sequence object.
     * @param enoLinkedLoop Passed only for logging purposes.
     * @return The id of the first question within the sequence.
     */
    private String findFirstQuestionId(AbstractSequence sequence, LinkedLoop enoLinkedLoop) {
        // First questionnaire component can be a subsequence or a question
        SequenceItem firstSubsequenceOrQuestionItem = sequence.getSequenceItems()
                .stream()
                .filter(sequenceItem -> sequenceItem.getType() == SequenceItem.SequenceItemType.SUBSEQUENCE
                        || sequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION)
                .findFirst().orElse(null);
        // Loop on empty sequence
        if (firstSubsequenceOrQuestionItem == null) {
            log.warn("Linked loop '{}' is based on loop '{}'. This loop references sequence '{}'. " +
                    "This sequence is empty! (Weird, but this should have no impact.)",
                    enoLinkedLoop.getId(), enoLinkedLoop.getReference(), sequence.getId());
            return null;
        }
        if (firstSubsequenceOrQuestionItem.getType() == SequenceItem.SequenceItemType.QUESTION) {
            return firstSubsequenceOrQuestionItem.getId();
        } else {
            sequence = (AbstractSequence) enoIndex.get(firstSubsequenceOrQuestionItem.getId());
            Optional<String> firstQuestionId = sequence.getSequenceItems()
                    .stream()
                    .filter(sequenceItem -> sequenceItem.getType() == SequenceItem.SequenceItemType.QUESTION)
                    .map(SequenceItem::getId)
                    .findFirst();
            if (firstQuestionId.isEmpty()) {
                throw new MappingException(String.format(
                        "Linked loop '%s' is based on loop '%s'. This loop references sequence '%s'. " +
                                "Unable to find first question to compute Lunatic \"iterations\" expression.",
                        enoLinkedLoop.getId(), enoLinkedLoop.getReference(), sequence.getId()));
            }
            return firstQuestionId.get();
        }
    }

}
