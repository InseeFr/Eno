package fr.insee.eno.core.processing.out.steps.lunatic.resizing;

import fr.insee.eno.core.exceptions.business.LunaticLoopResolutionException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoIdentifiableObject;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.lunatic.LunaticResizingEntry;
import fr.insee.eno.core.model.navigation.LinkedLoop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LunaticLoopResizingLogic {

    private final Questionnaire lunaticQuestionnaire;
    private final EnoQuestionnaire enoQuestionnaire;
    private final EnoIndex enoIndex;

    public LunaticLoopResizingLogic(Questionnaire lunaticQuestionnaire, EnoQuestionnaire enoQuestionnaire, EnoIndex enoIndex) {
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoIndex = enoIndex;
    }

    /**
     * Build resizing entries for a loop.
     * @param lunaticLoop Lunatic loop object.
     * @return list of resizing entries of the loop.
     */
    public List<LunaticResizingEntry> buildResizingEntries(Loop lunaticLoop) {

        // Corresponding Eno loop object
        fr.insee.eno.core.model.navigation.Loop enoLoop = (fr.insee.eno.core.model.navigation.Loop)
                enoIndex.get(lunaticLoop.getId());
        if (enoLoop == null)
            throw new MappingException(String.format(
                    "Eno loop object corresponding to Lunatic loop '%s' cannot be found.", lunaticLoop.getId()));

        // Linked loop are processed starting from main loops
        if (enoLoop instanceof LinkedLoop)
            return new ArrayList<>();

        // Variable names that are the keys of the resizing
        List<String> resizingVariableNames = findResizingVariablesForLoop((StandaloneLoop) enoLoop);

        if (resizingVariableNames.isEmpty())
            return new ArrayList<>();

        // Expression that resize the concerned variables
        String sizeExpression = lunaticLoop.getLines().getMax().getValue();
        // Concerned variables to be resized
        List<String> resizedVariableNames = findResizedVariablesForLoop(lunaticLoop, (StandaloneLoop) enoLoop);

        List<LunaticResizingEntry> resizingLoopEntries = new ArrayList<>();
        resizingVariableNames.forEach(variableName ->
                resizingLoopEntries.add(
                        new LunaticResizingEntry(variableName, sizeExpression, resizedVariableNames)));

        return resizingLoopEntries;
    }

    private List<String> findResizingVariablesForLoop(StandaloneLoop enoLoop) {
        List<String> maxIterationDependencies = enoLoop.getMaxIteration().getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .toList();
        return lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> maxIterationDependencies.contains(variable.getName()))
                .filter(variable -> VariableTypeEnum.COLLECTED.equals(variable.getVariableType()))
                .map(IVariableType::getName)
                .toList();
    }

    // Note: external variables are not concerned since their values are not designed to be changed dynamically
    private List<String> findResizedVariablesForLoop(Loop lunaticLoop, StandaloneLoop enoLoop) {
        List<String> resizedVariableNames = new ArrayList<>();
        List<Loop> lunaticLinkedLoops = findLunaticLinkedLoops(enoLoop);
        lunaticLinkedLoops.forEach(loop -> resizedVariableNames.addAll(getCollectedVariablesInLoop(lunaticLoop)));
        return resizedVariableNames;
    }

    private List<Loop> findLunaticLinkedLoops(StandaloneLoop enoLoop) {
        // This first filter could be removed if Lunatic linked loops had the reference of their "main" loop
        List<String> linkedLoopIds = enoQuestionnaire.getLoops().stream()
                .filter(LinkedLoop.class::isInstance)
                .map(LinkedLoop.class::cast)
                .filter(linkedLoop -> enoLoop.getId().equals(linkedLoop.getReference()))
                .map(EnoIdentifiableObject::getId)
                .toList();
        return lunaticQuestionnaire.getComponents().stream()
                .filter(component -> linkedLoopIds.contains(component.getId()))
                .map(Loop.class::cast)
                .toList();
    }

    private List<String> getCollectedVariablesInLoop(Loop loop) {
        List<String> result = new ArrayList<>();
        loop.getComponents().forEach(component -> {
            switch (component.getComponentType()) {
                case CHECKBOX_BOOLEAN, INPUT_NUMBER, INPUT, TEXTAREA, DATEPICKER, RADIO, CHECKBOX_ONE, DROPDOWN ->
                        result.add(((ComponentSimpleResponseType) component).getResponse().getName());
                case CHECKBOX_GROUP ->
                        ((CheckboxGroup) component).getResponses().forEach(responsesCheckboxGroup ->
                                result.add(responsesCheckboxGroup.getResponse().getName()));
                case TABLE ->
                        ((Table) component).getBodyLines().forEach(bodyLine ->
                                bodyLine.getBodyCells().stream()
                                        .filter(bodyCell -> bodyCell.getResponse() != null)
                                        .forEach(bodyCell -> result.add(bodyCell.getResponse().getName())));
                case ROSTER_FOR_LOOP ->
                        throw new LunaticLoopResolutionException(String.format(
                                "Dynamic tables are forbidden in loops: loop '%s' contains a dynamic table.",
                                loop.getId()));
                case LOOP ->
                        throw new LunaticLoopResolutionException(String.format(
                                "Nested loop are forbidden: loop '%s' contains an other loop.",
                                loop.getId()));
                case PAIRWISE_LINKS ->
                        throw new LunaticLoopResolutionException(String.format(
                                "Pairwise components are forbidden in loops: loop '%s' contains a pairwise component.",
                                loop.getId()));
                default ->
                        log.debug("(Resizing) Component of type {} has no response.", component.getComponentType());
            }
        });
        return result;
    }

}
