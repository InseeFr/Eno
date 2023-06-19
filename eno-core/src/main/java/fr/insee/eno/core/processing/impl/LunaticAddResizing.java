package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class LunaticAddResizing implements OutProcessingInterface<Questionnaire> {

    private EnoQuestionnaire enoQuestionnaire;
    private EnoCatalog enoCatalog;

    public LunaticAddResizing(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoCatalog = new EnoCatalog(enoQuestionnaire);
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<Object> resizings = lunaticQuestionnaire.getResizing().getAny();

        for(ComponentType component: lunaticQuestionnaire.getComponents()) {
            if(component.getComponentType().equals(ComponentTypeEnum.LOOP)) {
                resizings.addAll(buildResizingVariablesForLoop((Loop) component));
            }

            if(component.getComponentType().equals(ComponentTypeEnum.PAIRWISE_LINKS)) {
                resizings.addAll(buildResizingVariablesForPairwise((PairwiseLinks) component));
            }
        }
    }


    private List<LunaticResizingPairWiseVariable> buildResizingVariablesForPairwise(PairwiseLinks links) {
        List<String> sizesVTLFormula = List.of(links.getXAxisIterations().getValue(), links.getYAxisIterations().getValue());
        List<Variable> variables = getVariables(links.getId());
        List<String> collectedVariablesFormulaDependencies = getCollectedVariablesFormulaDependencies(variables);

        return variables.stream()
                .map(variable -> new LunaticResizingPairWiseVariable(variable.getName(), sizesVTLFormula, collectedVariablesFormulaDependencies))
                .toList();
    }


    private List<LunaticResizingLoopVariable> buildResizingVariablesForLoop(Loop loop) {
        String sizeVTLFormula = getSizeVTLFormula(loop);

        // if VTL formula is a numeric value, no need to handle variables
        if(sizeVTLFormula.chars().allMatch(Character::isDigit)) {
            return new ArrayList<>();
        }

        List<Variable> variables = getVariables(loop.getId());
        List<String> collectedVariablesFormulaDependencies = getCollectedVariablesFormulaDependencies(variables);

        return variables.stream()
                .map(variable -> new LunaticResizingLoopVariable(variable.getName(), sizeVTLFormula, collectedVariablesFormulaDependencies))
                .toList();
    }


    private List<Variable> getVariables(String loopId) {
        return enoQuestionnaire.getVariableGroups().stream()
                .filter(variableGroup -> variableGroup.getName().equals(loopId))
                .map(VariableGroup::getVariables)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<String> getCollectedVariablesFormulaDependencies(List<Variable> variables) {
        return getCollectedVariablesFromVariables(variables).stream()
                .map(Variable::getName)
                .distinct()
                .toList();
    }

    /**
     * Return collected variables needed to run the VTL formula from a calculated variable
     * To get collected variables from a calculated variable, we have to retrieve the binding references of the calculated variable
     *
     * @param calculatedVariable calculated variable
     * @return list of collected variables that trigger resizing from calculated variable
     */
    private List<Variable> getCollectedVariablesFromCalculatedVariable(Variable calculatedVariable) {
        List<Variable> bindingVariables = calculatedVariable.getExpression().getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .map(variableName -> enoCatalog.getVariable(variableName))
                .toList();
        // The binding references can include calculated variables too, hence the recursion
        return getCollectedVariablesFromVariables(bindingVariables);
    }

    /**
     * Return collected variables needed to run the VTL formula from a list of variables (calculated and collected).
     *
     * @param variables variables needed to run the VTL formula (collected and calculated)
     * @return list of collected variables needed to run the VTL formula
     */
    private List<Variable> getCollectedVariablesFromVariables(List<Variable> variables) {
        List<Variable> collectedVariables = variables.stream()
                .filter(variable -> variable.getCollected().equals("COLLECTED"))
                .toList();

        List<Variable> collectedVariablesFromCalculatedVariables = variables.stream()
                .filter(variable -> variable.getCollected().equals("CALCULATED"))
                .map(this::getCollectedVariablesFromCalculatedVariable)
                .flatMap(Collection::stream)
                .toList();

        collectedVariables.addAll(collectedVariablesFromCalculatedVariables);
        return collectedVariables;
    }

    /**
     * Get size VTL formula from loop
     * @param loop loop to retrieve vtl formula
     * @return size VTL formula
     */
    private String getSizeVTLFormula(Loop loop) {
        String sizeVTLFormula = null;
        if(loop.getLines() != null) {
            sizeVTLFormula = loop.getLines().getMax().getValue();
        }

        if(sizeVTLFormula == null) {
            sizeVTLFormula = loop.getIterations().getValue();
        }

        return sizeVTLFormula;
    }
}
