package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.SingleResponseQuestion;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.out.steps.lunatic.loop.LunaticLoopFilter;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static fr.insee.eno.core.utils.vtl.VtlSyntaxUtils.countVariable;

/** Lunatic technical processing for loops.
 * Requires: sorted components. */
@Slf4j
public class LunaticLoopResolution implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private EnoIndex enoIndex;

    public LunaticLoopResolution(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        enoIndex = enoQuestionnaire.getIndex();
        //
        enoQuestionnaire.getLoops().forEach(enoLoop -> {
            Loop lunaticLoop = findLunaticLoop(lunaticQuestionnaire, enoLoop);
            insertSequencesInLoop(lunaticQuestionnaire, lunaticLoop, enoLoop);
            setOtherLoopProperties(lunaticLoop, enoLoop);
        });
    }

    private static Loop findLunaticLoop(Questionnaire lunaticQuestionnaire, fr.insee.eno.core.model.navigation.Loop enoLoop) {
        for (Iterator<ComponentType> iterator = lunaticQuestionnaire.getComponents().iterator(); iterator.hasNext();) {
            ComponentType component = iterator.next();
            if (enoLoop.getId().equals(component.getId())){
                iterator.remove();
                return (Loop) component;
            }
        }
        throw new MappingException(String.format(
                "Loop '%s' not found among Lunatic components of questionnaire '%s'",
                enoLoop.getId(), lunaticQuestionnaire.getId()));
    }

    private void insertSequencesInLoop(Questionnaire lunaticQuestionnaire, Loop lunaticLoop, fr.insee.eno.core.model.navigation.Loop enoLoop) {
        if (enoLoop.getLoopScope().isEmpty())
            throw new LunaticLoopException("Loop '" + enoLoop.getId() + "' has an empty scope.");
        int position = insertSequenceInLoop(lunaticQuestionnaire, lunaticLoop, enoLoop.getLoopScope().getFirst().getId());
        enoLoop.getLoopScope().stream().skip(1).forEachOrdered(structureItemReference ->
                insertSequenceInLoop(lunaticQuestionnaire, lunaticLoop, structureItemReference.getId()));
        lunaticQuestionnaire.getComponents().add(position, lunaticLoop);
    }

    /** Replace components that are in the referenced sequence or subsequence
     * by a loop object containing these (including the sequence component itself). */
    private int insertSequenceInLoop(
            Questionnaire lunaticQuestionnaire, Loop lunaticLoop, String sequenceReference) {
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Iterator<ComponentType> iterator = components.iterator();
        // First iterate until we find the referenced sequence
        int position = 0;
        while (iterator.hasNext()) {
            ComponentType component = iterator.next();
            // Then transfer concerned components in the loop component
            if (sequenceReference.equals(component.getId())) {
                // Check that the reference is actually a sequence or subsequence
                assert component instanceof Sequence || component instanceof Subsequence;
                // Insert the sequence/subsequence (first element of the loop)
                lunaticLoop.getComponents().add(component);
                iterator.remove();
                insertComponentsInLoop(lunaticQuestionnaire, lunaticLoop, sequenceReference);
                break;
            }
            position++;
        }
        return position;
    }

    /** Insert components that belongs to the loop, in the right order, using Eno sequence object. */
    private void insertComponentsInLoop(
            Questionnaire lunaticQuestionnaire, Loop lunaticLoop, String sequenceReference) {
         AbstractSequence enoSequence = (AbstractSequence) enoIndex.get(sequenceReference);
         enoSequence.getSequenceStructure().forEach(structureItemReference -> {
             relocateComponent(lunaticQuestionnaire, lunaticLoop, structureItemReference.getId());
             if (StructureItemType.SUBSEQUENCE.equals(structureItemReference.getType()))
                 insertComponentsInLoop(lunaticQuestionnaire, lunaticLoop, structureItemReference.getId());
         });
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
            throw new MappingException(String.format(
                    "Unable to find component '%s' when trying to insert it in Lunatic loop '%s'.",
                    componentReference, lunaticLoop.getId()));
        }
        lunaticLoop.getComponents().add(searchedComponent);
    }

    private void setOtherLoopProperties(Loop lunaticLoop, @NonNull fr.insee.eno.core.model.navigation.Loop enoLoop) {
        lunaticLoop.setDepth(BigInteger.ONE);
        setLunaticLoopFilter(lunaticLoop, enoLoop);
        if (enoLoop instanceof LinkedLoop enoLinkedLoop) {
            setLinkedLoopIterations(lunaticLoop, enoLinkedLoop);
        }
    }

    /** Condition filter of the loop is the same as its first component where we remove occurenceFilter.
     * Dirty: we replace the occurrenceFilter by 'true' ....
     * TODO: change logic of computing LoopFilter
     * step1: loop over child components
     * step2: for each child compute it's filter (not resolved filter -> enoFilter (with filterScope)
     * step3: if this found filter is not the occurrenceFilter i.e "SAUF" condition, add it to the list (if not present)
     * step4: concatenate all found filters
     * @see LunaticLoopFilter
     * */
    private void setLunaticLoopFilter(Loop lunaticLoop, fr.insee.eno.core.model.navigation.Loop enoLoop) {
        if (lunaticLoop.getComponents().isEmpty()) {
            throw new MappingException(String.format(
                    "Loop '%s' is empty. This means something went wrong during the mapping or loop resolution.",
                    lunaticLoop.getId()));
        }
        lunaticLoop.setConditionFilter(LunaticLoopFilter.computeConditionFilter(enoLoop, enoQuestionnaire));
    }

    /** Lunatic linked loops have an "iterations" property.
     * The "iterations" property is a calculated expression, it is a VTL count on variable of the first question
     * of the loop it is based on. (This has been discussed with Lunatic, it is what it is for now...)
     * The "iterations" property is linked to the "loopDependencies" property,
     * this method also sets the "loopDependencies" property of the linked loop.
     * */
    private void setLinkedLoopIterations(Loop lunaticLoop, LinkedLoop enoLinkedLoop) {
        // We "just" want to find the first variable in the scope of the reference loop
        EnoIdentifiableObject reference = enoIndex.get(enoLinkedLoop.getReference());
        if (reference instanceof StandaloneLoop enoReferenceLoop) {
            String variableName = findFirstVariableOfReference(enoLinkedLoop, enoReferenceLoop, enoIndex);
            lunaticLoop.setIterations(new LabelType());
            lunaticLoop.getIterations().setValue(countVariable(variableName));
            lunaticLoop.getIterations().setType(LabelTypeEnum.VTL);
            lunaticLoop.getLoopDependencies().add(variableName);
            return;
        }
        if (reference instanceof DynamicTableQuestion enoDynamicTable) {
            String variableName = enoDynamicTable.getVariableNames().getFirst();
            lunaticLoop.setIterations(new LabelType());
            lunaticLoop.getIterations().setValue(countVariable(variableName));
            lunaticLoop.getIterations().setType(LabelTypeEnum.VTL);
            // For a dynamic table: insert all variables of the table in loop dependencies
            // Note: done this way since Eno xml does it like this,
            // but the loop dependency property doesn't really matter
            lunaticLoop.getLoopDependencies().addAll(enoDynamicTable.getVariableNames());
            return;
        }
        throw new LunaticLoopException(String.format(
                "Linked loop '%s' reference object's '%s' is neither a loop nor a dynamic table.",
                enoLinkedLoop.getId(), reference));
    }

    /**
     * Returns the first response name that belong to the reference loop.
     * @param enoLinkedLoop Eno linked loop passed for logging purposes.
     * @param enoReferenceLoop Eno reference ("main") loop.
     * @param enoIndex Eno index.
     * @return The first response name (string) that belong to the reference loop.
     */
    public static String findFirstVariableOfReference(LinkedLoop enoLinkedLoop, StandaloneLoop enoReferenceLoop,
                                                      EnoIndex enoIndex) {
        String contextErrorMessage = String.format(
                "Unable to find its first question to compute Lunatic \"iterations\" expression for linked loop '%s'.",
                enoLinkedLoop.getId());
        return findFirstResponseNameOfLoop(enoReferenceLoop, enoIndex, contextErrorMessage);
    }

    /**
     * Returns the first response name that belong to the loop.
     * @param enoLoop A eno loop object.
     * @param enoIndex Eno index.
     * @param contextErrorMessage Context that will be added in the exception message if retrieving the response name fails.
     * @return The first response name (string) that belong to the loop.
     */
    public static String findFirstResponseNameOfLoop(fr.insee.eno.core.model.navigation.Loop enoLoop,
                                                     EnoIndex enoIndex,
                                                     String contextErrorMessage) {
        AbstractSequence firstSequenceOfLoop = (AbstractSequence) enoIndex.get(enoLoop.getLoopScope().getFirst().getId());
        if (firstSequenceOfLoop.getSequenceStructure().isEmpty())
            throw new LunaticLoopException(String.format(
                    "Loop '%s' is defined to start at sequence '%s', which is empty. %s",
                    enoLoop.getId(), firstSequenceOfLoop.getId(), contextErrorMessage));
        String firstQuestionId = findFirstQuestionId(firstSequenceOfLoop, enoIndex);
        Optional<String> variableName = getVariableNameFromQuestionId(firstQuestionId, enoIndex);
        if (variableName.isEmpty())
            throw new LunaticLoopException(String.format(
                    "Loop '%s' that starts at sequence '%s'. " +
                            "This first question of the sequence is not a \"simple\" question. %s",
                    enoLoop.getId(), firstSequenceOfLoop.getId(), contextErrorMessage));
        return variableName.get();
    }

    private static Optional<String> getVariableNameFromQuestionId(String questionId, EnoIndex enoIndex) {
        EnoObject firstQuestion = enoIndex.get(questionId);
        if (! (firstQuestion instanceof SingleResponseQuestion))
            return Optional.empty();
        return Optional.of(((SingleResponseQuestion) firstQuestion).getResponse().getVariableName());
    }

    /**
     * Return the id of the first question in given sequence or subsequence object.
     *
     * @param sequence Eno sequence object.
     * @return The id of the first question within the sequence.
     */
    private static String findFirstQuestionId(AbstractSequence sequence, EnoIndex enoIndex) {
        StructureItemReference firstSequenceItem = sequence.getSequenceStructure().getFirst();
        if (firstSequenceItem.getType() == StructureItemType.QUESTION)
            return firstSequenceItem.getId();
        AbstractSequence subsequence = (AbstractSequence) enoIndex.get(firstSequenceItem.getId());
        return findFirstQuestionId(subsequence, enoIndex);
    }

}
