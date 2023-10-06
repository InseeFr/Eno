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
        List<Object> resizingList = new ArrayList<>();
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
        // Manage eventual resizing duplicates
        List<Object> resizingListWithoutDuplicates = noDuplicatesControl(resizingList);
        // Set the resizing list only if it is not empty
        if(!resizingListWithoutDuplicates.isEmpty()) {
            ResizingType resizingType = new ResizingType();
            resizingType.setAny(resizingListWithoutDuplicates);
            lunaticQuestionnaire.setResizing(resizingType);
        }
    }

    /** Throws an exception if there are duplicates in the resizing list.
     * If there is duplicate keys, resizing entries are merged is the resizing expression is the same,
     * an exception is thrown otherwise.
     * @param resizingList Resizing list with eventual duplicates.
     * @return The resizing list with duplicate entries merged. */
    private List<Object> noDuplicatesControl(List<Object> resizingList) {
        // Duplicates between regular entries
        Map<String, LunaticResizingEntry> resizingMap = new HashMap<>();
        List<LunaticResizingEntry> resizingEntries = resizingList.stream()
                .filter(LunaticResizingEntry.class::isInstance)
                .map(LunaticResizingEntry.class::cast)
                .toList();
        resizingEntries.forEach(resizingEntry -> {
            String variableName = resizingEntry.getName();
            if (! resizingMap.containsKey(variableName))
                resizingMap.put(variableName, resizingEntry);
            else
                resizingMap.put(variableName, mergeResizingEntries(resizingMap.get(variableName), resizingEntry));
        });
        List<Object> result = new ArrayList<>(resizingMap.values());
        // Duplicate between pairwise entry and regular entries
        Optional<LunaticResizingPairwiseEntry> pairwiseResizingEntry = resizingList
                .stream()
                .filter(LunaticResizingPairwiseEntry.class::isInstance)
                .map(LunaticResizingPairwiseEntry.class::cast)
                .findAny();
        if (pairwiseResizingEntry.isPresent()) {
            if (resizingMap.containsKey(pairwiseResizingEntry.get().getName()))
                throw new LunaticLogicException(String.format(
                        "Variable '%s' is used to define the size of the pairwise links question and other " +
                                "components (loops or dynamic tables), which is currently forbidden.",
                        pairwiseResizingEntry.get().getName()));
            result.add(pairwiseResizingEntry.get());
        }
        //
        return result;
    }

    /**
     * Merge resizing entries given into a single one.
     * @param entry1 Resizing entry.
     * @param entry2 Resizing entry, with same name.
     * @return A merged resizing entry with variables from both entries.
     */
    public LunaticResizingEntry mergeResizingEntries(LunaticResizingEntry entry1, LunaticResizingEntry entry2) {
        // (should not happen, but you never know)
        if (! entry1.getName().equals(entry2.getName()))
            throw new IllegalArgumentException("Resizing entries with different names cannot be merged.");
        //
        if (! entry1.getSize().equals(entry2.getSize()))
            throw new LunaticLogicException(String.format(
                    "Variable '%s' is used to define the size of different components in the questionnaire. " +
                            "Check loop 'max' iteration expressions, dynamic table max size expressions.",
                    entry1.getName()));
        //
        LunaticResizingEntry mergedEntry = new LunaticResizingEntry(
                entry1.getName(), entry1.getSize(), entry1.getVariables());
        mergedEntry.getVariables().addAll(entry2.getVariables());
        return mergedEntry;
    }

}
