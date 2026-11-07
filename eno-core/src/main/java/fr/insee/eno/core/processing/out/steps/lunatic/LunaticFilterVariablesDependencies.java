package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processing step that adds the "variablesDependencies" object in the condition filters of a Lunatic questionnaire.
 * For each filter, the object lists the variables directly mentioned in the filter expression (no transitive
 * resolution), grouped by variable type: COLLECTED, EXTERNAL and CALCULATED. The three lists are always present
 * (empty if there is no variable of the type).
 */
@Slf4j
public class LunaticFilterVariablesDependencies implements ProcessingStep<Questionnaire> {

    private final EnoIndex enoIndex;

    /** Variable types indexed by variable name. Built from the variables of the processed questionnaire. */
    private Map<String, VariableTypeEnum> variableTypes;

    /** @param enoIndex Eno index, used to retrieve the Eno filter object that corresponds to a Lunatic component. */
    public LunaticFilterVariablesDependencies(EnoIndex enoIndex) {
        this.enoIndex = enoIndex;
    }

    /**
     * Indexes the variable types of the questionnaire, then sets the variables dependencies of every
     * condition filter found in its components.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        variableTypes = new HashMap<>();
        lunaticQuestionnaire.getVariables()
                .forEach(variable -> variableTypes.put(variable.getName(), typeOf(variable)));
        processComponents(lunaticQuestionnaire.getComponents(), null);
    }

    /**
     * Returns the type of the given variable.
     * Tolerant: if the variable type was not set, it falls back on the Java class of the variable.
     * @param variable Lunatic variable.
     * @return The variable type, or null if it cannot be determined.
     */
    private static VariableTypeEnum typeOf(VariableType variable) {
        if (variable.getVariableType() != null)
            return variable.getVariableType();
        return switch (variable) {
            case CalculatedVariableType _ -> VariableTypeEnum.CALCULATED;
            case CollectedVariableType _ -> VariableTypeEnum.COLLECTED;
            case ExternalVariableType _ -> VariableTypeEnum.EXTERNAL;
            default -> null;
        };
    }

    /**
     * Iterates on the given components, recursively diving into nesting components (loops, pairwise, roundabouts),
     * and processes the filter of each one.
     * @param components Lunatic components.
     * @param parent Nesting component that holds the given components, null at questionnaire level.
     */
    private void processComponents(List<ComponentType> components, ComponentType parent) {
        for (ComponentType component : components) {
            if (component instanceof ComponentNestingType nesting)
                processComponents(nesting.getComponents(), component);
            processFilter(component, parent);
        }
    }

    /**
     * Sets the variables dependencies of the condition filter of the given component.
     * Nothing is done if the component has no filter, or if it is a filter description.
     * @param component Lunatic component.
     * @param parent Nesting component that holds the given component, null at questionnaire level.
     */
    private void processFilter(ComponentType component, ComponentType parent) {
        // A filter description shares the condition filter object of the component it describes:
        // it is handled with that component.
        if (component instanceof FilterDescription)
            return;
        ConditionFilterType lunaticFilter = component.getConditionFilter();
        if (lunaticFilter == null)
            return;
        boolean synthetic = parent instanceof PairwiseLinks
                || component instanceof Loop
                || component instanceof Roundabout;
        lunaticFilter.setVariablesDependencies(
                buildDependencies(namesOf(component, lunaticFilter, synthetic)));
    }

    /**
     * Returns the names of the variables directly mentioned in the filter of the given component.
     * Synthetic filters (loops, pairwise inner component) are not built from an Eno filter:
     * their direct dependencies are in the Lunatic "bindingDependencies".
     * Other components: direct binding references of the Eno filter (not the resolved ones, which are transitive).
     * @param component Lunatic component.
     * @param lunaticFilter Condition filter of the component.
     * @param synthetic True if the filter was created by a processing step and has no Eno counterpart.
     * @return Variable names. Empty (with a warning) if the Eno filter cannot be found.
     */
    private List<String> namesOf(ComponentType component, ConditionFilterType lunaticFilter, boolean synthetic) {
        if (synthetic)
            return lunaticFilter.getBindingDependencies();
        Object enoObject = enoIndex.get(component.getId());
        if (enoObject instanceof EnoComponent enoComponent && enoComponent.getComponentFilter() != null)
            return enoComponent.getComponentFilter().getBindingReferences().stream()
                    .map(BindingReference::getVariableName).toList();
        log.warn("No Eno filter found for Lunatic component '{}' ({}): no variable dependencies.",
                component.getId(), component.getClass().getSimpleName());
        return List.of();
    }

    /**
     * Builds the variables dependencies object from the given variable names.
     * The three lists of the object are always present, empty if no variable has the corresponding type.
     * @param names Variable names (duplicates are ignored).
     * @return The variables dependencies object.
     */
    private VariablesDependencies buildDependencies(List<String> names) {
        VariablesDependencies dependencies = new VariablesDependencies();
        names.stream().distinct().forEach(name -> addByType(dependencies, name));
        return dependencies;
    }

    /**
     * Adds the given variable name in the list that corresponds to its type.
     * @param dependencies Variables dependencies object being built.
     * @param name Variable name.
     * @throws MappingException if the variable is unknown, or if its type cannot be determined.
     */
    private void addByType(VariablesDependencies dependencies, String name) {
        if (!variableTypes.containsKey(name))
            throw new MappingException(String.format(
                    "Unable to find the variable '%s' used in a filter.", name));
        VariableTypeEnum type = variableTypes.get(name);
        if (type == null)
            throw new MappingException(String.format(
                    "Unable to determine the type of the variable '%s' used in a filter.", name));
        switch (type) {
            case COLLECTED -> dependencies.getCollected().add(name);
            case EXTERNAL -> dependencies.getExternal().add(name);
            case CALCULATED -> dependencies.getCalculated().add(name);
        }
    }
}
