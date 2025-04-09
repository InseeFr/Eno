package fr.insee.eno.core.utils;

import fr.insee.eno.core.exceptions.business.LunaticLoopException;
import fr.insee.eno.core.exceptions.technical.LunaticPairwiseException;
import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.lunatic.model.flat.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.insee.eno.core.model.navigation.ComponentFilter.DEFAULT_FILTER_VALUE;

/**
 * Utility class that provide some methods for Lunatic-Model objects.
 */
@Slf4j
public class LunaticUtils {

    private LunaticUtils() {}

    private static void nonNullTypeCheck(ComponentType component) {
        if (component.getComponentType() == null)
            throw new MappingException("Lunatic component " + component + " has a null component type.");
    }

    /**
     * Returns true if the component has responses, false otherwise.
     * @param component A Lunatic component.
     * @return True if the component has responses, false otherwise.
     */
    public static boolean isResponseComponent(@NonNull ComponentType component) {
        nonNullTypeCheck(component);
        return switch (component.getComponentType()) {
            case QUESTIONNAIRE, LOOP, ROUNDABOUT, SEQUENCE, SUBSEQUENCE, TEXT, ACCORDION -> false;
            case CHECKBOX_BOOLEAN, INPUT, TEXTAREA, SUGGESTER, INPUT_NUMBER, DATEPICKER, DURATION,
                    DROPDOWN, RADIO, CHECKBOX_ONE,
                    CHECKBOX_GROUP, TABLE, ROSTER_FOR_LOOP, PAIRWISE_LINKS,
                    QUESTION -> true;
        };
    }

    /**
     * Get the list of response names that belong to the component.
     * If the component is a questionnaire or a loop, this function iterates on the inner components recursively.
     * @param component A Lunatic component.
     * @return List of response names that belong to the given component.
     */
    public static List<String> getResponseNames(@NonNull ComponentType component) {
        nonNullTypeCheck(component);
        return switch (component.getComponentType()) {
            case QUESTIONNAIRE, LOOP, ROUNDABOUT -> ((ComponentNestingType) component).getComponents().stream()
                    .map(LunaticUtils::getDirectResponseNames)
                    .flatMap(Collection::stream)
                    .toList();
            default -> getDirectResponseNames(component);
        };
    }

    /**
     * Returns the list of response names of the component. The result will be an empty list if the given component has
     * no direct responses (e.g. questionnaire, loop, sequence, text etc.).
     * @param component Lunatic response component (such as Input, Table, PairwiseLinks).
     * @return The list of response names of the component.
     */
    public static List<String> getDirectResponseNames(@NonNull ComponentType component) {
        nonNullTypeCheck(component);

        if (! isResponseComponent(component))
            return new ArrayList<>();

        return switch (component.getComponentType()) {
            // Single response components
            case CHECKBOX_BOOLEAN, INPUT, TEXTAREA, SUGGESTER, INPUT_NUMBER, DATEPICKER, DURATION,
                    DROPDOWN, RADIO, CHECKBOX_ONE -> {
                ComponentSimpleResponseType simpleResponseComponent = (ComponentSimpleResponseType) component;
                if (simpleResponseComponent.getResponse() == null)
                    throw new MappingException("Lunatic component '" + component.getId() + "' has no response.");
                yield List.of(simpleResponseComponent.getResponse().getName());
            }
            // Multiple response components
            case CHECKBOX_GROUP -> ((CheckboxGroup) component).getResponses().stream()
                    .map(ResponseCheckboxGroup::getResponse)
                    .map(ResponseType::getName)
                    .toList();
            case ROSTER_FOR_LOOP -> ((RosterForLoop) component).getComponents().stream()
                    .filter(subcomponent -> subcomponent.getResponse() != null)
                    .map(subcomponent -> subcomponent.getResponse().getName())
                    .toList();
            case TABLE -> ((Table) component).getBodyLines().stream()
                    .map(BodyLine::getBodyCells)
                    .flatMap(Collection::stream)
                    .filter(subcomponent -> subcomponent.getResponse() != null)
                    .map(subcomponent -> subcomponent.getResponse().getName())
                    .toList();
            case PAIRWISE_LINKS -> List.of(getPairwiseResponseVariable((PairwiseLinks) component));
            // Question component -> look at inner components
            case QUESTION -> ((Question) component).getComponents().stream()
                    .map(LunaticUtils::getDirectResponseNames)
                    .flatMap(Collection::stream)
                    .toList();
            // Default = unexpected type
            default -> throw new IllegalArgumentException(
                    "Unexpected component type '" + component.getComponentType() + "'.");
        };
    }

    // ----- Above method should be refactored within the Lunatic-Model lib
    // ----- Below methods contain come Eno business rules and should stay here

    /**
     * From a Lunatic loop object given, returns the list of collected variable names that belong to this loop.
     * @param loop A Lunatic loop.
     * @return A list of collected variable names.
     */
    public static List<String> getCollectedVariablesInLoop(Loop loop) {
        List<String> result = new ArrayList<>();
        loop.getComponents().forEach(component -> {
            switch (component.getComponentType()) {
                case CHECKBOX_BOOLEAN, INPUT_NUMBER, INPUT, TEXTAREA, SUGGESTER, DATEPICKER, DURATION, RADIO,
                        CHECKBOX_ONE, DROPDOWN, CHECKBOX_GROUP, TABLE ->
                        result.addAll(getDirectResponseNames(component));
                case QUESTIONNAIRE, SEQUENCE, SUBSEQUENCE, TEXT, ACCORDION ->
                        doNothing();
                case QUESTION ->
                        throw new IllegalStateException("This method does not support the question component.");
                case ROSTER_FOR_LOOP ->
                        throw new LunaticLoopException(String.format(
                                "Dynamic tables are forbidden in loops: loop '%s' contains a dynamic table.",
                                loop.getId()));
                case LOOP, ROUNDABOUT ->
                        throw new LunaticLoopException(String.format(
                                "Nested loop are forbidden: loop '%s' contains an other loop.",
                                loop.getId()));
                case PAIRWISE_LINKS ->
                        throw new LunaticLoopException(String.format(
                                "Pairwise components are forbidden in loops: loop '%s' contains a pairwise component.",
                                loop.getId()));
            }
        });
        return result;
    }

    private static void doNothing() {
        /* No-op method */
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

    public static Optional<ComponentType> findComponentById(Questionnaire lunaticQuestionnaire, String id) {
        // Search in questionnaire components
        List<ComponentType> components = lunaticQuestionnaire.getComponents();
        Optional<ComponentType> searchedComponent = findComponentInList(id, components);
        if (searchedComponent.isPresent())
            return searchedComponent;
        // If not found, may be in a nesting component (such as loop, roundabout, pairwise)
        return lunaticQuestionnaire.getComponents().stream()
                .filter(ComponentNestingType.class::isInstance)
                .map(ComponentNestingType.class::cast)
                .map(ComponentNestingType::getComponents)
                .map(componentList -> findComponentInList(id, componentList))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny();
    }

    private static Optional<ComponentType> findComponentInList(String id, List<ComponentType> componentList) {
        return componentList.stream().filter(component -> id.equals(component.getId())).findAny();
    }
    public static List<String> getCollectedVariablesByComponent(ComponentType componentType){
        List<String> collectedVars = new ArrayList<>();
        if (componentType instanceof ComponentSimpleResponseType simpleResponseType) {
            collectedVars.add(simpleResponseType.getResponse().getName());
            if (componentType instanceof Suggester suggester && suggester.getArbitrary() != null) {
                collectedVars.add(suggester.getArbitrary().getResponse().getName());
            }
            if (componentType instanceof CheckboxOne checkboxOne) {
                collectedVars.addAll(checkboxOne.getOptions().stream()
                        .filter(o -> o.getDetail() != null && o.getDetail().getResponse() != null)
                        .map(o -> o.getDetail().getResponse().getName()).toList());
            }
            if (componentType instanceof Radio radio) {
                collectedVars.addAll(radio.getOptions().stream()
                        .filter(o -> o.getDetail() != null && o.getDetail().getResponse() != null)
                        .map(o -> o.getDetail().getResponse().getName()).toList());
            }
            if (componentType instanceof Dropdown dropdown) {
                collectedVars.addAll(dropdown.getOptions().stream()
                        .filter(o -> o.getDetail() != null && o.getDetail().getResponse() != null)
                        .map(o -> o.getDetail().getResponse().getName()).toList());
            }
        }
        if (componentType instanceof ComponentMultipleResponseType) {
            switch (componentType.getComponentType()) {
                case TABLE -> collectedVars.addAll(((Table) componentType).getBodyLines().stream()
                        .map(BodyLine::getBodyCells)
                        .flatMap(Collection::stream)
                        .map(BodyCell::getResponse)
                        .filter(Objects::nonNull)
                        .map(ResponseType::getName)
                        .toList());

                case ROSTER_FOR_LOOP ->
                        collectedVars.addAll(((RosterForLoop) componentType).getComponents().stream()
                                .map(BodyCell::getResponse)
                                .filter(Objects::nonNull)
                                .map(ResponseType::getName)
                                .toList());

                case CHECKBOX_GROUP -> {
                    collectedVars.addAll(((CheckboxGroup) componentType).getResponses().stream()
                            .map(ResponseCheckboxGroup::getResponse)
                            .map(ResponseType::getName)
                            .toList());

                    collectedVars.addAll(((CheckboxGroup) componentType).getResponses().stream()
                            .map(ResponseCheckboxGroup::getDetail)
                            .filter(Objects::nonNull)
                            .map(DetailResponse::getResponse)
                            .map(ResponseType::getName)
                            .toList());
                }
            }
        }
        return collectedVars;
    }

    /**
     *
     * @param lunaticQuestionnaire
     * @return A map of QuestionName: List of collected variables in the question.
     */
    public static Map<String, List<String>> getCollectedVariablesByQuestion(Questionnaire lunaticQuestionnaire) {
        Map<String, List<String>> questionCollectedVarIndex = new HashMap<>();
        lunaticQuestionnaire.getComponents().stream()
                .map(componentType -> {
                    if (componentType instanceof Loop loop) return loop.getComponents();
                    return List.of(componentType);
                })
                .flatMap(Collection::stream)
                .forEach(componentType -> {
                    String questionId = componentType.getId();
                    List<String> collectedVariables = getCollectedVariablesByComponent(componentType);
                    questionCollectedVarIndex.put(questionId, collectedVariables);
                });
        return questionCollectedVarIndex;
    }

    public static boolean isConditionFilterActive(ConditionFilterType conditionFilter){
        if(conditionFilter == null) return false;
        if(conditionFilter.getValue().isEmpty()) return false;
        return !DEFAULT_FILTER_VALUE.equals(conditionFilter.getValue());
    }

}
