package fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.processing.out.steps.lunatic.LunaticLoopResolution;
import fr.insee.eno.core.reference.EnoIndex;

import java.util.Optional;

/**
 * Retrieve the lunatic shapeFrom attribute from eno variable groups
 * In a loop, the lunatic shapeFrom attribute of a calculated variable is the name of the first collected variable of the loop.
 * It permits for Lunatic to define the dimension the calculated variable should take in the loop.
 */
public class ShapefromAttributeRetrievalFromVariableGroups implements ShapefromAttributeRetrieval {

    /**
     * Retrieves the Lunatic "shapeFrom" attribute. It is empty if the calculated variable with given name is not in a
     * loop. Otherwise, it is the name of the first collected variable that is in the scope of the corresponding loop.
     * This method uses the Eno questionnaire to retrieve this information.
     * @param lunaticCalculatedVariableName name of the calculated variable
     * @param enoQuestionnaire eno questionnaire
     * @return the first collected variable if the calculated variable is in a loop, nothing otherwise
     */
    public Optional<Variable> getShapeFrom(String lunaticCalculatedVariableName, EnoQuestionnaire enoQuestionnaire) {
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        return getShapeFrom(lunaticCalculatedVariableName, enoQuestionnaire, enoIndex);
    }

    // Note: the pattern with eno questionnaire and eno index is not satisfying as it currently is.
    // It would probably better to separate the two.
    // The first method is there so to not change the signature of the "shape from retrieval" interface for now.

    // Note 2: using the eno index as little as possible to improve code maintainability in exchange
    // for a slight loss of performance.

    private Optional<Variable> getShapeFrom(String lunaticCalculatedVariableName, EnoQuestionnaire enoQuestionnaire,
                                           EnoIndex enoIndex) {
        // retrieve the variable group in which the variable is present, excluding the "Questionnaire"
        // variable group which does not correspond to a "loop" variable group
        Optional<VariableGroup> variableGroup = enoQuestionnaire.getVariableGroups().stream()
                .filter(vGroup -> !vGroup.getType().equals(VariableGroup.DDI_QUESTIONNAIRE_TYPE))
                .filter(vGroup -> vGroup.getVariableByName(lunaticCalculatedVariableName).isPresent())
                .findFirst();

        if (variableGroup.isEmpty())
            return Optional.empty();

        // then retrieve the "main" loop or dynamic table that corresponds to this variable group
        String mainIterableReference = variableGroup.get().getLoopReferences().get(0);

        // loop case
        Optional<Loop> mainLoop = enoQuestionnaire.getLoops().stream()
                .filter(loop -> mainIterableReference.equals(loop.getId()))
                .findAny();
        if (mainLoop.isPresent())
            return Optional.of(findFirstCollectedVariableOfLoop(mainLoop.get(), enoQuestionnaire, enoIndex));

        // dynamic table case
        Optional<DynamicTableQuestion> dynamicTable = enoQuestionnaire.getMultipleResponseQuestions().stream()
                .filter(DynamicTableQuestion.class::isInstance)
                .map(DynamicTableQuestion.class::cast)
                .filter(loop -> mainIterableReference.equals(loop.getId()))
                .findAny();
        if (dynamicTable.isPresent())
            return Optional.of(findFirstCollectedVariableOfDynamicTable(dynamicTable.get(), enoQuestionnaire));

        throw new MappingException(String.format(
                "Unable to retrieve 'shapeFrom' property for the calculated variable '%s'. " +
                        "This probably means that some information has not been mapped as expected during mapping.",
                lunaticCalculatedVariableName));
    }

    // Note: not sure why this method returns a Variable object and not a CollectedVariable, maybe it could be improved

    private Variable findFirstCollectedVariableOfLoop(Loop enoLoop, EnoQuestionnaire enoQuestionnaire, EnoIndex enoIndex) {
        String contextErrorMessage = "Unable to find its first question to retrieve Lunatic \"shapeFrom\" property.";
        String firstQuestionVariableName = LunaticLoopResolution.findFirstResponseNameOfLoop(
                enoLoop, enoIndex, contextErrorMessage);
        return getVariableFromName(enoQuestionnaire, firstQuestionVariableName);
    }

    private Variable findFirstCollectedVariableOfDynamicTable(DynamicTableQuestion enoDynamicTable,
                                                              EnoQuestionnaire enoQuestionnaire) {
        String variableName =  enoDynamicTable.getVariableNames().get(0);
        return getVariableFromName(enoQuestionnaire, variableName);
    }

    private static Variable getVariableFromName(EnoQuestionnaire enoQuestionnaire, String variableName) {
        Optional<Variable> searchedVariable = enoQuestionnaire.getVariables()
                .stream()
                .filter(variable -> variableName.equals(variable.getName())).findAny();
        if (searchedVariable.isEmpty())
            throw new MappingException(String.format(
                    "Unable to find variable with name '%s' in the Eno questionnaire variables " +
                            "(while trying to retrieve Lunatic 'shapeFrom' property).",
                    variableName));
        return searchedVariable.get();
    }

}
