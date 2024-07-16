package fr.insee.eno.core.utils;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.lunatic.model.flat.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class that provide some methods for Lunatic-Model objects.
 */
@Slf4j
public class LunaticUtils {

    private LunaticUtils() {}

    /**
     * Return the list of components that contain a response (questions/loops) from the given component list.
     * @param components A list of Lunatic components.
     * @return A new list with only components that contain a response.
     */
    public static List<ComponentType> getResponseComponents(List<ComponentType> components) {
        return components.stream()
                .filter(component -> !component.getComponentType().equals(ComponentTypeEnum.SEQUENCE))
                .filter(component -> !component.getComponentType().equals(ComponentTypeEnum.SUBSEQUENCE))
                .toList();
    }

    /**
     * Get the list of response names that belong to the component.
     * @param component A Lunatic component which hold responses.
     * @return List of response names that belong to the given component.
     * @throws IllegalArgumentException if the given component is of a type that does not contain responses.
     */
    public static List<String> getResponseNames(ComponentType component) {
        List<String> names;
        switch (component.getComponentType()) {
            case CHECKBOX_GROUP -> names = ((CheckboxGroup) component).getResponses().stream()
                    .map(ResponsesCheckboxGroup::getResponse)
                    .map(ResponseType::getName)
                    .toList();
            case ROSTER_FOR_LOOP -> names = ((RosterForLoop) component).getComponents().stream()
                    .filter(subcomponent -> subcomponent.getResponse() != null)
                    .map(subcomponent -> subcomponent.getResponse().getName())
                    .toList();
            case LOOP -> names = getResponseComponents(((Loop) component).getComponents()).stream()
                    .map(LunaticUtils::getResponseNames)
                    .flatMap(Collection::stream)
                    .toList();
            case TABLE -> names = ((Table) component).getBodyLines().stream()
                    .map(BodyLine::getBodyCells)
                    .flatMap(Collection::stream)
                    .filter(subcomponent -> subcomponent.getResponse() != null)
                    .map(subcomponent -> subcomponent.getResponse().getName())
                    .toList();
            default -> {
                if (! (component instanceof ComponentSimpleResponseType simpleResponseComponent))
                    throw new IllegalArgumentException(String.format(
                            "Method to get response names cannot be called on a component of type %s.",
                            component.getComponentType()));
                if (simpleResponseComponent.getResponse() == null)
                    throw new MappingException("Lunatic component '" + component.getId() + "' has no response.");
                names = List.of(simpleResponseComponent.getResponse().getName());
            }
        }
        return names;
    }

    /**
     * From a Lunatic loop object given, returns the list of collected variable names that belong to this loop.
     * @param loop A Lunatic loop.
     * @return A list of collected variable names.
     */
    public static Set<String> getCollectedVariablesInLoop(Loop loop) {
        Set<String> result = new HashSet<>();
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

    /**
     * Return the response name of the component that belong to the given pairwise links. This method checks if
     * the inner component is valid.
     * @param pairwiseLinks A pairwise links component.
     * @return The response name of the pairwise inner component.
     * @throws LunaticPairwiseException if the component in the pairwise is invalid.
     */
    public static String getPairwiseResponseVariable(PairwiseLinks pairwiseLinks) {
        ComponentType pairwiseInnerComponent = getPairwiseInnerComponent(pairwiseLinks);
        return ((ComponentSimpleResponseType) pairwiseInnerComponent).getResponse().getName();
    }

    /**
     * Return the unique choice component that belong to the given pairwise links. This method checks if the inner
     * component is valid.
     * @param pairwiseLinks A pairwise links component.
     * @return The pairwise inner component.
     * @throws LunaticPairwiseException if the component in the pairwise is invalid.
     */
    public static ComponentType getPairwiseInnerComponent(PairwiseLinks pairwiseLinks) {
        // Get the pairwise inner component
        checkPairwiseComponentSize(pairwiseLinks);
        ComponentType pairwiseComponent = pairwiseLinks.getComponents().getFirst();
        // Check that this component has a complying type
        if (! (ComponentTypeEnum.DROPDOWN.equals(pairwiseComponent.getComponentType()) ||
                ComponentTypeEnum.RADIO.equals(pairwiseComponent.getComponentType()) ||
                ComponentTypeEnum.CHECKBOX_ONE.equals(pairwiseComponent.getComponentType())))
            throw new LunaticPairwiseException(String.format(
                    "Lunatic pairwise component should be a unique choice component. Pairwise object '%s' " +
                            "contains a component of type '%s'.",
                    pairwiseLinks.getId(), pairwiseComponent.getComponentType()));
        //
        return pairwiseComponent;
    }

    /**
     * Checks if the given pairwise links contains exactly one component.
     * @param pairwiseLinks A pairwise component.
     * @throws LunaticPairwiseException if there is not exactly one component in the pairwise.
     */
    private static void checkPairwiseComponentSize(PairwiseLinks pairwiseLinks) {
        int pairwiseComponentsSize = pairwiseLinks.getComponents().size();
        if (pairwiseComponentsSize != 1)
            throw new LunaticPairwiseException(String.format(
                    "Lunatic pairwise must contain exactly 1 component. Pairwise object '%s' contains %s.",
                    pairwiseLinks.getId(), pairwiseComponentsSize));
    }

}
