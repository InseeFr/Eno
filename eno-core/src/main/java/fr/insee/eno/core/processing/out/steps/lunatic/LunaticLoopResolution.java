package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.business.LunaticLoopResolutionException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.mappers.LunaticMapper;
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
            lunaticLoop.setComponentType(ComponentTypeEnum.LOOP); // a bit ugly to do it here...
            insertLoopComponent(lunaticQuestionnaire, lunaticLoop, enoLoop.getLoopScope().get(0).getId()); // FIXME: extended loop
            insertEnoLoopInfo(lunaticLoop, enoLoop);
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
        //
        throw new MappingException("TODO");
    }

    /** Replace components that are in the referenced sequence or subsequence
     * by a loop object containing these (including the sequence component itself). */
    private void insertLoopComponent(
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
        components.add(position, lunaticLoop);
    }

    /** Insert components that belongs to the loop, in the right order, using Eno sequence object. */
    private void insertComponentsInLoop(
            Questionnaire lunaticQuestionnaire, Loop lunaticLoop, String sequenceReference) {
         AbstractSequence enoSequence = (AbstractSequence) enoIndex.get(sequenceReference);
         enoSequence.getSequenceStructure().forEach(structureItemReference ->
                 relocateComponent(lunaticQuestionnaire, lunaticLoop, structureItemReference.getId()));
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
            throw new MappingException(
                    "Unable to find component '"+componentReference+"' when trying to insert it in Lunatic loop.");
        }
        lunaticLoop.getComponents().add(searchedComponent);
    }

    /** Map eno loop's metadata into lunatic loop object. */
    private void insertEnoLoopInfo(Loop lunaticLoop, fr.insee.eno.core.model.navigation.Loop enoLoop) {
        //
        lunaticLoop.setId(enoLoop.getId());
        lunaticLoop.setDepth(BigInteger.ONE); // (Nested loops is not supported yet)
        setLunaticLoopFilter(lunaticLoop);
        // TODO: is hierarchy useful in Loop components? (not sure)
        //
        if (enoLoop instanceof StandaloneLoop standaloneLoop) {
            standaloneLoopMapping(lunaticLoop, standaloneLoop);
        }
        if (enoLoop instanceof LinkedLoop linkedLoop) {
            linkedLoopMapping(lunaticLoop, linkedLoop);
        }
    }

    /** Condition filter of the loop is the same as its first component. */
    private static void setLunaticLoopFilter(Loop lunaticLoop) {
        if (lunaticLoop.getComponents().isEmpty()) {
            throw new MappingException(String.format(
                    "Loop '%s' is empty. This means something went wrong during the mapping or loop resolution.",
                    lunaticLoop.getId()));
        }
        lunaticLoop.setConditionFilter(lunaticLoop.getComponents().get(0).getConditionFilter());
    }

    /** Lunatic standalone loops are not "paginated" and have a "lines" property (with "min" and "max"). */
    private void standaloneLoopMapping(Loop lunaticLoop, StandaloneLoop enoStandaloneLoop) {
        //
        lunaticLoop.setPaginatedLoop(false);
    }

    /** Lunatic linked loops are "paginated" and have the "iterations" property.
     * The "iterations" property is a calculated expression, it is a VTL count on variable of the first question
     * of the loop. This has been discussed with Lunatic, it is what it is for now. */
    private void linkedLoopMapping(Loop lunaticLoop, LinkedLoop enoLinkedLoop) {
        //
        lunaticLoop.setPaginatedLoop(true);
        // We "just" want to find the first variable in the scope of the reference loop
        EnoIdentifiableObject reference = enoIndex.get(enoLinkedLoop.getReference());
        //
        if (reference instanceof StandaloneLoop) {
            AbstractSequence sequence = (AbstractSequence) enoIndex.get(enoLinkedLoop.getLoopScope().get(0).getId()); // FIXME: see abive
            String firstQuestionId = findFirstQuestionId(sequence, enoLinkedLoop);
            EnoObject firstQuestion = enoIndex.get(firstQuestionId);
            if (! (firstQuestion instanceof SingleResponseQuestion)) {
                throw new LunaticLoopResolutionException(String.format(
                        "Linked loop '%s' is based on loop '%s' that starts at sequence '%s'. " +
                                "This first question of the sequence is not a \"simple\" question. " +
                                "The linked loop will not work as expected.",
                        enoLinkedLoop.getId(), enoLinkedLoop.getId(), enoLinkedLoop.getLoopScope().get(0).getId())); // FIXME: see above
            }
            String variableName = ((SingleResponseQuestion) firstQuestion).getResponse().getVariableName();
            lunaticLoop.setIterations(new LabelType());
            lunaticLoop.getIterations().setValue("count("+ variableName +")");
            return;
        }
        //
        if (reference instanceof DynamicTableQuestion) {
            log.warn("Linked loop '{}' is based on a dynamic table. This feature is not supported yet.",
                    enoLinkedLoop.getId());
            lunaticLoop.setIterations(new LabelType());
            lunaticLoop.getIterations().setValue("1");
            return;
        }
        //
        throw new LunaticLoopResolutionException(String.format(
                "Linked loop '%s' reference object's '%s' is neither a loop nor a dynamic table.",
                enoLinkedLoop.getId(), reference));
    }

    /**
     * Return the id of the first question in given sequence or subsequence object.
     * @param sequence Eno sequence object.
     * @param enoLinkedLoop Passed only for logging purposes.
     * @return The id of the first question within the sequence.
     */
    private String findFirstQuestionId(AbstractSequence sequence, LinkedLoop enoLinkedLoop) {
        //
        if (sequence.getSequenceStructure().isEmpty()) {
            throw new LunaticLoopResolutionException(String.format(
                    "Linked loop '%s' is based on loop '%s'. This loop references sequence '%s'. " +
                            "Unable to find its first question to compute Lunatic \"iterations\" expression.",
                    enoLinkedLoop.getId(), enoLinkedLoop.getReference(), enoLinkedLoop.getLoopScope().get(0).getId())); // FIXME: see above
        }
        StructureItemReference firstSequenceItem = sequence.getSequenceStructure().get(0);
        //
        if (firstSequenceItem.getType() == StructureItemType.QUESTION) {
            return firstSequenceItem.getId();
        }
        //
        AbstractSequence subsequence = (AbstractSequence) enoIndex.get(firstSequenceItem.getId());
        return findFirstQuestionId(subsequence, enoLinkedLoop);
    }

}
