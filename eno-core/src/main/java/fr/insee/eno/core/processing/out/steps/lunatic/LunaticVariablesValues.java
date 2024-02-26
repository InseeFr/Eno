package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <p>Processing step aimed to replace Lunatic simple variable objects by:</p>
 *  <ul>
 *   <li>"one dimension" array variable objects for variables that belong to a loop</li>
 *   <li>"two dimensions" array variable object for the pairwise variable</li>
 * </ul>
 * <p>By doing this, the "values" part of the Lunatic variables will have the correct format when serializing
 * the questionnaire.</p>
 */
public class LunaticVariablesValues implements ProcessingStep<Questionnaire> {

    private Questionnaire lunaticQuestionnaire;

    /**
     * Replace "simple" variables that are collected in iterated component (such as loop, dynamic table, pairwise)
     * by array variables in the given questionnaire.
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
                .forEach(this::replaceLoopVariables);
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(RosterForLoop.class::isInstance)
                .map(RosterForLoop.class::cast)
                .forEach(this::replaceRosterForLoopVariables);
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(PairwiseLinks.class::isInstance)
                .map(PairwiseLinks.class::cast)
                .forEach(this::replacePairwiseVariable);
    }

    /**
     * Iterates on the loop components to find the variables that are collected within the loop.
     * Then, replaces corresponding variable objects by new variable array objects.
     * Note: with the current implementation, the replacement is not "in-place".
     * @param loop A Lunatic loop component.
     */
    private void replaceLoopVariables(Loop loop) {
        Set<String> collectedVariables = LunaticUtils.getCollectedVariablesInLoop(loop);
        replaceArrayVariables(collectedVariables);
    }

    /**
     * Iterates on the roster for loop components to find the variables that are collected within the roster.
     * Then, replaces corresponding variable objects by new variable array objects.
     * @param rosterForLoop A Lunatic roster for loop component.
     */
    private void replaceRosterForLoopVariables(RosterForLoop rosterForLoop) {
        List<String> collectedVariables = rosterForLoop.getComponents().stream()
                .map(bodyCell -> bodyCell.getResponse().getName())
                .toList();
        replaceArrayVariables(collectedVariables);
    }

    private void replaceArrayVariables(Collection<String> collectedVariableNames) {
        //
        lunaticQuestionnaire.getVariables().removeIf(variable -> collectedVariableNames.contains(variable.getName()));
        //
        collectedVariableNames.forEach(variableName -> {
            VariableTypeArray variableTypeArray = new VariableTypeArray();
            variableTypeArray.setVariableType(VariableTypeEnum.COLLECTED);
            variableTypeArray.setName(variableName);
            lunaticQuestionnaire.getVariables().add(variableTypeArray);
        });
    }

    /**
     * Iterates on the pairwise links components to find the variables that are collected within the pairwise.
     * Then, replaces corresponding variable objects by new two dimensions variable array objects.
     * @param pairwiseLinks A Lunatic pairwise links component.
     */
    private void replacePairwiseVariable(PairwiseLinks pairwiseLinks) {
        String pairwiseVariableName = LunaticUtils.getPairwiseResponseVariable(pairwiseLinks);
        //
        lunaticQuestionnaire.getVariables().removeIf(variable -> pairwiseVariableName.equals(variable.getName()));
        //
        VariableTypeTwoDimensionsArray variableTypeTwoDimensionsArray = new VariableTypeTwoDimensionsArray();
        variableTypeTwoDimensionsArray.setVariableType(VariableTypeEnum.COLLECTED);
        variableTypeTwoDimensionsArray.setName(pairwiseVariableName);
        lunaticQuestionnaire.getVariables().add(variableTypeTwoDimensionsArray);
    }

}
