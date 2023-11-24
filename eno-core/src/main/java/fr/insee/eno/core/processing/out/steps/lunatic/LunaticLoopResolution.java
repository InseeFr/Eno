package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.Constant;
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
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

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
        int position = insertSequenceInLoop(lunaticQuestionnaire, lunaticLoop, enoLoop.getLoopScope().get(0).getId());
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

    private void setOtherLoopProperties(Loop lunaticLoop, fr.insee.eno.core.model.navigation.Loop enoLoop) {
        lunaticLoop.setDepth(BigInteger.ONE);
        setLunaticLoopFilter(lunaticLoop);
        // TODO: hierarchy
        if (enoLoop instanceof LinkedLoop enoLinkedLoop) {
            setLinkedLoopIterations(lunaticLoop, enoLinkedLoop);
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
            lunaticLoop.getIterations().setValue("count("+ variableName +")");
            lunaticLoop.getIterations().setType(Constant.LUNATIC_LABEL_VTL);
            lunaticLoop.getLoopDependencies().add(variableName);
            return;
        }
        if (reference instanceof DynamicTableQuestion) {
            throw new UnsupportedOperationException(String.format(
                    "Linked loop '%s' is based on a dynamic table. This feature is not supported yet.",
                    enoLinkedLoop.getId()));
        }
        throw new LunaticLoopException(String.format(
                "Linked loop '%s' reference object's '%s' is neither a loop nor a dynamic table.",
                enoLinkedLoop.getId(), reference));
    }

    public static String findFirstVariableOfReference(LinkedLoop enoLinkedLoop, StandaloneLoop enoReferenceLoop,
                                                       EnoIndex enoIndex) {
        AbstractSequence startSequence = (AbstractSequence) enoIndex.get(enoReferenceLoop.getLoopScope().get(0).getId());
        if (startSequence.getSequenceStructure().isEmpty()) {
            throw new LunaticLoopException(String.format(
                    "Linked loop '%s' is based on loop '%s'. " +
                            "This loop is defined to start at sequence '%s', which is empty. " +
                            "Unable to find its first question to compute Lunatic \"iterations\" expression.",
                    enoLinkedLoop.getId(), enoReferenceLoop.getId(), startSequence.getId()));
        }
        String firstQuestionId = findFirstQuestionId(startSequence, enoIndex);
        EnoObject firstQuestion = enoIndex.get(firstQuestionId);
        if (! (firstQuestion instanceof SingleResponseQuestion)) {
            throw new LunaticLoopException(String.format(
                    "Linked loop '%s' is based on loop '%s' that starts at sequence '%s'. " +
                            "This first question of the sequence is not a \"simple\" question.",
                    enoLinkedLoop.getId(), enoReferenceLoop.getId(), startSequence.getId()));
        }
        return ((SingleResponseQuestion) firstQuestion).getResponse().getVariableName();
    }

    /**
     * Return the id of the first question in given sequence or subsequence object.
     *
     * @param sequence Eno sequence object.
     * @return The id of the first question within the sequence.
     */
    private static String findFirstQuestionId(AbstractSequence sequence, EnoIndex enoIndex) {
        StructureItemReference firstSequenceItem = sequence.getSequenceStructure().get(0);
        if (firstSequenceItem.getType() == StructureItemType.QUESTION)
            return firstSequenceItem.getId();
        AbstractSequence subsequence = (AbstractSequence) enoIndex.get(firstSequenceItem.getId());
        return findFirstQuestionId(subsequence, enoIndex);
    }

}
