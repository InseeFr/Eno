package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.variable.CollectedVariableType;
import fr.insee.lunatic.model.flat.variable.CollectedVariableValues;
import fr.insee.lunatic.model.flat.variable.VariableTypeEnum;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>Processing step aimed to set values of Lunatic collected variable:</p>
 *  <ul>
 *   <li>array values objects for variables that belong to a loop or roster for loop</li>
 *   <li>"double" values variable object for the pairwise variable</li>
 *   <li>scalar values object for the others</li>
 * </ul>
 * <p>By doing this, the "values" part of the Lunatic collected variables will have the correct format when serializing
 * the questionnaire.</p>
 */
public class LunaticVariablesValues implements ProcessingStep<Questionnaire> {

    private Questionnaire lunaticQuestionnaire;

    /**
     * Set the values property of collected variables.
     * @param lunaticQuestionnaire Lunatic questionnaire.
     */
    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance)
                .map(Loop.class::cast)
                .forEach(this::setLoopVariablesValues);
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance)
                .map(RosterForLoop.class::cast)
                .forEach(this::setRosterForLoopVariablesValues);
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(PairwiseLinks.class::isInstance)
                .map(PairwiseLinks.class::cast)
                .forEach(this::replacePairwiseVariable);

        // All variables that have not their values set at this point are scalar variables
        lunaticQuestionnaire.getVariables().stream()
                .filter(CollectedVariableType.class::isInstance)
                .map(CollectedVariableType.class::cast)
                .filter(collectedVariableType -> collectedVariableType.getValues() == null)
                .forEach(collectedVariableType ->
                        collectedVariableType.setValues(new CollectedVariableValues.Scalar()));
    }

    /**
     * Iterates on the loop components to find the variables that are collected within the loop.
     * Then, sets array values on these.
     * @param loop A Lunatic loop component.
     */
    private void setLoopVariablesValues(Loop loop) {
        Set<String> collectedVariables = LunaticUtils.getCollectedVariablesInLoop(loop);
        setArrayValuesOnVariables(collectedVariables);
    }

    /**
     * Iterates on the roster for loop components to find the variables that are collected within the roster.
     * Then, sets array values on these.
     * @param rosterForLoop A Lunatic roster for loop component.
     */
    private void setRosterForLoopVariablesValues(RosterForLoop rosterForLoop) {
        List<String> collectedVariables = rosterForLoop.getComponents().stream()
                .map(bodyCell -> bodyCell.getResponse().getName())
                .toList();
        setArrayValuesOnVariables(collectedVariables);
    }

    private void setArrayValuesOnVariables(Collection<String> collectedVariableNames) {
        lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> collectedVariableNames.contains(variable.getName()))
                .map(CollectedVariableType.class::cast)
                .forEach(collectedVariableType ->
                        collectedVariableType.setValues(new CollectedVariableValues.Array()));
    }

    /**
     * Iterates on the pairwise links components to find the variables that are collected within the pairwise.
     * Then, replaces corresponding variable objects by new two dimensions variable array objects.
     * @param pairwiseLinks A Lunatic pairwise links component.
     */
    private void replacePairwiseVariable(PairwiseLinks pairwiseLinks) {
        String pairwiseVariableName = LunaticUtils.getPairwiseResponseVariable(pairwiseLinks);
        Optional<CollectedVariableType> pairwiseVariable = lunaticQuestionnaire.getVariables().stream()
                .filter(variable -> pairwiseVariableName.equals(variable.getName()))
                .map(CollectedVariableType.class::cast)
                .findAny();
        if (pairwiseVariable.isEmpty())
            throw new MappingException(String.format(
                    "Unable to find the collected variable '%s' associated to the pairwise links component (id=%s).",
                    pairwiseVariableName, pairwiseLinks.getId()));
        pairwiseVariable.get().setValues(new CollectedVariableValues.DoubleArray());
    }

}
