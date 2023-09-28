package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.business.LunaticLogicException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairwiseEntry;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LunaticAddResizing implements ProcessingStep<Questionnaire> {

    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;

    public LunaticAddResizing(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoQuestionnaire.getIndex();
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        ResizingType resizingType = new ResizingType();
        List<Object> resizingList = resizingType.getAny();
        //
        LunaticLoopResizingLogic loopResizingLogic = new LunaticLoopResizingLogic(
                lunaticQuestionnaire, enoQuestionnaire, enoIndex);
        LunaticPairwiseResizingLogic pairwiseResizingLogic = new LunaticPairwiseResizingLogic(
                lunaticQuestionnaire, enoIndex);
        LunaticDynamicTableResizingLogic dynamicTableResizingLogic = new LunaticDynamicTableResizingLogic(
                lunaticQuestionnaire);
        //
        lunaticQuestionnaire.getComponents().forEach(component -> {
            ComponentTypeEnum componentType = component.getComponentType();
            if (Objects.requireNonNull(componentType) == ComponentTypeEnum.LOOP) {
                resizingList.addAll(loopResizingLogic.buildResizingEntries((Loop) component));
            }
            if (componentType == ComponentTypeEnum.ROSTER_FOR_LOOP) {
                log.warn("Resizing is not implemented for dynamic tables.");
                resizingList.addAll(dynamicTableResizingLogic.buildResizingEntries((RosterForLoop) component));
            }
            if (componentType == ComponentTypeEnum.PAIRWISE_LINKS) {
                resizingList.addAll(pairwiseResizingLogic.buildPairwiseResizingEntries((PairwiseLinks) component));
            }
        });
        // Check that there is no duplicate keys for resizing
        // (Lunatic modeling for resizing has to be changed to be more precise, so that there would be no duplicate issue)
        noDuplicatesControl(resizingList);
        // Set the resizing list only if it is not empty
        if(!resizingList.isEmpty())
            lunaticQuestionnaire.setResizing(resizingType);
    }

    /** Throws an exception if there are duplicates in the resizing list.
     * We could make something more precise than this by making controls everywhere we add resizing entries.
     * Yet since the Lunatic "resizing" feature has to be reworked, we'll settle for that for now. */
    private void noDuplicatesControl(List<Object> resizingList) {
        Set<String> resizingKeys = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        resizingList.forEach(resizingEntry -> {
            // crappy code but all this will be removed when there will be a proper implementation in Lunatic-Model
            String variableName = null;
            if (resizingEntry instanceof LunaticResizingEntry lunaticResizingEntry)
                variableName = lunaticResizingEntry.getName();
            if (resizingEntry instanceof LunaticResizingPairwiseEntry lunaticResizingPairwiseEntry)
                variableName = lunaticResizingPairwiseEntry.getName();
            assert variableName != null;
            //
            if (resizingKeys.contains(variableName))
                duplicates.add(variableName);
            resizingKeys.add(variableName);
        });
        //
        if (! duplicates.isEmpty())
            throw new LunaticLogicException(String.format(
                    "Variables '%s' is are used to define the size of different components in the questionnaire. " +
                            "Check loop 'max' iteration expressions, dynamic table max size expressions, " +
                            "and variable used for the pairwise component.",
                    duplicates));
    }

}
