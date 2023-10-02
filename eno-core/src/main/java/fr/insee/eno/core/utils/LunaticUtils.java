package fr.insee.eno.core.utils;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.lunatic.model.flat.CheckboxGroup;
import fr.insee.lunatic.model.flat.ComponentSimpleResponseType;
import fr.insee.lunatic.model.flat.Loop;
import fr.insee.lunatic.model.flat.Table;
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

}
