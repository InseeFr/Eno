package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.utils.LunaticUtils;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

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

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance)
                .map(Loop.class::cast)
                .forEach(this::replaceLoopVariables);
    }

    /**
     * Iterates on the loop components to find the variables that are collected within the loop.
     * Then, replaces corresponding variable objects by new variable array objects.
     * Note: with the current implementation, the replacement is not "in-place".
     * @param loop A Lunatic loop components.
     */
    private void replaceLoopVariables(Loop loop) {
        List<String> collectedVariables = LunaticUtils.getCollectedVariablesInLoop(loop);
        //
        lunaticQuestionnaire.getVariables().removeIf(variable -> collectedVariables.contains(variable.getName()));
        //
        collectedVariables.forEach(variableName -> {
            VariableTypeArray variableTypeArray = new VariableTypeArray();
            variableTypeArray.setVariableType(VariableTypeEnum.COLLECTED);
            variableTypeArray.setName(variableName);
            variableTypeArray.setValues(new ValuesTypeArray());
            lunaticQuestionnaire.getVariables().add(variableTypeArray);
        });
    }

}
