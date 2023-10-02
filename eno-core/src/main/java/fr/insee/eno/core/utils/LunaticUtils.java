package fr.insee.eno.core.utils;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LunaticUtils {

    private LunaticUtils() {}

    /**
     * From a Lunatic loop object given, returns the list of collected variable names that belong to this loop.
     * @param loop A Lunatic loop.
     * @return A list of collected variable names.
     */
    public static List<String> getCollectedVariablesInLoop(Loop loop) {
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
                        throw new LunaticLoopException(String.format(
                                "Dynamic tables are forbidden in loops: loop '%s' contains a dynamic table.",
                                loop.getId()));
                case LOOP ->
                        throw new LunaticLoopException(String.format(
                                "Nested loop are forbidden: loop '%s' contains an other loop.",
                                loop.getId()));
                case PAIRWISE_LINKS ->
                        throw new LunaticLoopException(String.format(
                                "Pairwise components are forbidden in loops: loop '%s' contains a pairwise component.",
                                loop.getId()));
                default ->
                        log.debug("(Variables in Lunatic loop) Component of type {} has no response.",
                                component.getComponentType());
            }
        });
        return result;
    }

    public static String getPairwiseResponseVariable(PairwiseLinks pairwiseLinks) {
        // Some controls...
        int pairwiseComponentsSize = pairwiseLinks.getComponents().size();
        if (pairwiseComponentsSize != 1)
            throw new LunaticPairwiseException(String.format(
                    "Lunatic pairwise must contain exactly 1 component. Pairwise object '%s' contains %s.",
                    pairwiseLinks.getId(), pairwiseComponentsSize));
        ComponentType pairwiseComponent = pairwiseLinks.getComponents().get(0);
        if (! (ComponentTypeEnum.DROPDOWN.equals(pairwiseComponent.getComponentType()) ||
                ComponentTypeEnum.RADIO.equals(pairwiseComponent.getComponentType()) ||
                ComponentTypeEnum.CHECKBOX_ONE.equals(pairwiseComponent.getComponentType())))
            throw new LunaticPairwiseException(String.format(
                    "Lunatic pairwise component should be a unique choice component. Pairwise object '%s' " +
                            "contains a component of type '%s'.",
                    pairwiseLinks.getId(), pairwiseComponent.getComponentType()));
        //
        return ((ComponentSimpleResponseType) pairwiseComponent).getResponse().getName();
    }

}
