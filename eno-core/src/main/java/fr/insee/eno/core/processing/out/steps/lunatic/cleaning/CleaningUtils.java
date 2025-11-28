package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.calculated.CalculatedExpression;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.lunatic.model.flat.ConditionFilterType;
import fr.insee.lunatic.model.flat.cleaning.CleanedVariableEntry;
import fr.insee.lunatic.model.flat.cleaning.CleaningExpression;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.cleaning.CleaningVariableEntry;
import fr.insee.lunatic.model.flat.variable.CalculatedVariableType;
import fr.insee.lunatic.model.flat.variable.VariableType;

import java.util.*;

import static fr.insee.eno.core.utils.vtl.VtlSyntaxUtils.isAggregatorUsedInsideExpression;

public class CleaningUtils {

    private CleaningUtils(){}

    /**
     *
     * @param filterExpression
     * @param allVariableNamesForFilter
     * @return true if an aggregator function of VTL language is used in filter (or in its dependencies)
     */
    private static boolean isAggregatorUsedInFilter(String filterExpression, List<String> allVariableNamesForFilter,
            Map<String, VariableType> variableIndex){
        if(isAggregatorUsedInsideExpression(filterExpression)) return true;
        for(String vName: allVariableNamesForFilter){
            VariableType variable = variableIndex.get(vName);
            if(variable instanceof CalculatedVariableType calculatedVariable &&
                    isAggregatorUsedInsideExpression(calculatedVariable.getExpression().getValue())) {
                return true;
            }
        }
        return false;
    }

    private static void addNewCleaningExpression(CleanedVariableEntry cleanedVariable,
                                                 String filterExpression,
                                                 String shapeFrom,
                                                 boolean isAggregatorUsed){
        cleanedVariable.getCleaningExpressions().add(new CleaningExpression(filterExpression, shapeFrom, isAggregatorUsed) );
    }

    public static List<String> removeCalculatedVariables(List<String> variableNames, Map<String, VariableType> variableIndex){
        return variableNames.stream()
                .filter(vName -> !(variableIndex.get(vName) instanceof CalculatedVariableType))
                .toList();
    }

    private static List<String> getFinalBindingReferencesWithCalculatedVariables(List<String> variablesOfExpression, Map<String, VariableType> variableIndex){
        return variablesOfExpression.stream()
                .map(variableIndex::get)
                .filter(Objects::nonNull)
                .map(variable -> {
                    List<String> variablesNames = new ArrayList<>(List.of(variable.getName()));
                    if ((variable instanceof CalculatedVariableType calculatedVariable)) variablesNames.addAll(calculatedVariable.getBindingDependencies());
                    return variablesNames;
                })
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    public static List<String> getFinalBindingReferencesWithCalculatedVariables(CalculatedExpression expression, Map<String, VariableType> variableIndex) {
        return getFinalBindingReferencesWithCalculatedVariables(
                expression.getBindingReferences().stream().map(BindingReference::getVariableName).toList(),
                variableIndex);
    }

    public static List<String> getFinalBindingReferencesWithCalculatedVariables(ComponentFilter expression, Map<String, VariableType> variableIndex) {
        return getFinalBindingReferencesWithCalculatedVariables(
                expression.getBindingReferences().stream().map(BindingReference::getVariableName).toList(),
                variableIndex);
    }

    public static List<String> getFinalBindingReferencesWithCalculatedVariables(ConditionFilterType expression, Map<String, VariableType> variableIndex) {
        return getFinalBindingReferencesWithCalculatedVariables(expression.getBindingDependencies(), variableIndex);
    }

    /**
     * Cleaning logic, update the cleaning object according to 3 params (filter, variables that influence filter, variables collected inside filter)
     * @param cleaning, the final cleaning object in lunatic-model to update
     * @param filterExpression, the expression of filter (as string expression)
     * @param allVariablesThatInfluenceFilterExpression, list of all variables that influence the filter
     * @param variablesCollectedInsideFilter, list of all variable that collected inside filter i.e that should be cleaned
     */
    public static void processCleaningForFilterExpression(
            CleaningType cleaning,
            Map<String, VariableType> variableIndex,
            Map<String, String> shapeFromIndex,
            String filterExpression,
            List<String> allVariablesThatInfluenceFilterExpression,
            List<String> variablesCollectedInsideFilter){
        boolean isAggregatorUsedOfFilter = isAggregatorUsedInFilter(filterExpression, allVariablesThatInfluenceFilterExpression, variableIndex);
        removeCalculatedVariables(allVariablesThatInfluenceFilterExpression, variableIndex).forEach(
                variableName -> {
                    boolean isExistCleaningVariableEntry = cleaning.getCleaningVariableNames().contains(variableName);
                    if (!isExistCleaningVariableEntry) {
                        CleaningVariableEntry cleaningVariableEntry = new CleaningVariableEntry(variableName);
                        variablesCollectedInsideFilter.stream()
                                // Prevent self cleaning: keep only those variables which are not the variable causing the change
                                .filter(variableToClean -> !variableName.equals(variableToClean))
                                .forEach(variableToClean -> {
                                    CleanedVariableEntry cleanedVariableEntry = new CleanedVariableEntry(variableToClean);
                                    addNewCleaningExpression(cleanedVariableEntry,
                                            filterExpression,
                                            shapeFromIndex.get(variableToClean),
                                            isAggregatorUsedOfFilter);
                                    cleaningVariableEntry.addCleanedVariable(cleanedVariableEntry);
                                });
                        cleaning.addCleaningEntry(cleaningVariableEntry);
                    } else {
                        CleaningVariableEntry existingCleaningVariableEntry = cleaning.getCleaningEntry(variableName);
                        variablesCollectedInsideFilter.stream()
                                // Prevent self cleaning: keep only those variables which are not the variable causing the change
                                .filter(variableToClean -> !variableName.equals(variableToClean))
                                .forEach(variableToClean -> {
                                    boolean isExistCleanedVariableEntry = existingCleaningVariableEntry.getCleanedVariableNames().contains(variableToClean);
                                    CleanedVariableEntry cleanedVariableEntry = isExistCleanedVariableEntry
                                            ? existingCleaningVariableEntry.getCleanedVariable(variableToClean)
                                            : new CleanedVariableEntry(variableToClean);
                                    addNewCleaningExpression(cleanedVariableEntry,
                                            filterExpression,
                                            shapeFromIndex.get(variableToClean),
                                            isAggregatorUsedOfFilter);
                                    existingCleaningVariableEntry.addCleanedVariable(cleanedVariableEntry);
                                });
                    }
                }
        );
    }
}
