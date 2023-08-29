package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.calculated.BindingReference;
import fr.insee.eno.core.model.lunatic.LunaticResizingLoopVariable;
import fr.insee.eno.core.model.lunatic.LunaticResizingPairWiseVariable;
import fr.insee.eno.core.model.variable.CalculatedVariable;
import fr.insee.eno.core.model.variable.CollectedVariable;
import fr.insee.eno.core.model.variable.Variable;
import fr.insee.eno.core.model.variable.VariableGroup;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.reference.EnoCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class LunaticAddResizing implements ProcessingStep<Questionnaire> {

    private EnoQuestionnaire enoQuestionnaire;
    private EnoCatalog enoCatalog;

    public LunaticAddResizing(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.enoCatalog = new EnoCatalog(enoQuestionnaire);
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        ResizingType resizingType = lunaticQuestionnaire.getResizing();
        if(resizingType == null) {
            resizingType = new ResizingType();
        }

        List<Object> resizings = resizingType.getAny();

        for(ComponentType component: lunaticQuestionnaire.getComponents()) {
            if(component.getComponentType().equals(ComponentTypeEnum.LOOP)) {
                resizings.addAll(buildResizingVariablesForLoop((Loop) component));
            }

            if(component.getComponentType().equals(ComponentTypeEnum.PAIRWISE_LINKS)) {
                resizings.addAll(buildResizingVariablesForPairwise((PairwiseLinks) component));
            }
        }

        if(!resizings.isEmpty()) {
            lunaticQuestionnaire.setResizing(resizingType);
        }
    }


    /**
     * Build resizing variables for a pairwise link
     * @param links pairwise link
     * @return list of resizing variables of a pairwise link
     */
    private List<LunaticResizingPairWiseVariable> buildResizingVariablesForPairwise(PairwiseLinks links) {
        List<String> sizesVTLFormula = List.of(links.getXAxisIterations().getValue(), links.getYAxisIterations().getValue());
        List<Variable> resizingVariables = getResizingVariables(links.getId());
        List<String> variablesNames = getVariablesUsedInFormula(links.getId());
        List<String> collectedVariablesFormulaDependencies = getCollectedResizingVariables(resizingVariables);

        return collectedVariablesFormulaDependencies.stream()
                .map(collectedVariable -> new LunaticResizingPairWiseVariable(collectedVariable, sizesVTLFormula, variablesNames))
                .toList();
    }

    /**
     * Build resizing variables for a loop
     * @param loop loop
     * @return list of resizing variables of a loop
     */
    private List<LunaticResizingLoopVariable> buildResizingVariablesForLoop(Loop loop) {
        // no need to handle linked loops, variables from main loop includes variables from linked loops
        if(isLinkedLoop(loop)) {
            return new ArrayList<>();
        }

        String sizeVTLFormula = loop.getLines().getMax().getValue();

        // if VTL formula is a numeric value, no need to handle variables
        if(sizeVTLFormula.chars().allMatch(Character::isDigit)) {
            return new ArrayList<>();
        }

        List<Variable> resizingVariables = getResizingVariables(loop.getId());
        List<String> variablesNames = getVariablesUsedInFormula(loop.getId());
        List<String> collectedVariablesFormulaDependencies = getCollectedResizingVariables(resizingVariables);

        return collectedVariablesFormulaDependencies.stream()
                .map(collectedVariable -> new LunaticResizingLoopVariable(collectedVariable, sizeVTLFormula, variablesNames))
                .toList();
    }

    /**
     * @param loopId loop id
     * @return all the variables used in the resizing formula
     */
    private List<String> getVariablesUsedInFormula(String loopId) {
        return enoQuestionnaire.getVariableGroups().stream()
                .filter(variableGroup -> variableGroup.getName().equals(loopId))
                .map(VariableGroup::getVariables)
                .flatMap(Collection::stream)
                .map(Variable::getName)
                .toList();
    }

    /**
     * Get resizing variables of a loop/pairwise
     * @param loopId id of the loop/pairwise
     * @return resizing variable list
     */
    private List<Variable> getResizingVariables(String loopId) {
        // TODO: change to retrieve resizing variables instead of links variables
        throw new UnsupportedOperationException("getResizingVariables needs some code !!");
    }

    /**
     * Return collected variables needed to run the VTL formula on calculated variables extracted from a variable list
     *
     * @param resizingVariables resizing variable list
     * @return list of collected variables that trigger resizing from calculated variable
     */
    private List<String> getCollectedResizingVariables(List<Variable> resizingVariables) {
        return getCollectedVariablesFromResizingVariables(resizingVariables).stream()
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
    private List<CollectedVariable> getCollectedVariablesFromCalculatedVariable(CalculatedVariable calculatedVariable) {
        List<Variable> bindingVariables = calculatedVariable.getExpression().getBindingReferences().stream()
                .map(BindingReference::getVariableName)
                .map(variableName -> enoCatalog.getVariable(variableName))
                .toList();
        // The binding references can include calculated variables too, hence the recursion
        return getCollectedVariablesFromResizingVariables(bindingVariables);
    }

    /**
     * Return collected variables from a list of resizing variables (collected + calculated)
     *
     * @param resizingVariables resizing variables (xontaining collected and calculated variables)
     * @return list of collected resizing variables
     */
    private List<CollectedVariable> getCollectedVariablesFromResizingVariables(List<Variable> resizingVariables) {
        List<CollectedVariable> collectedVariables = new ArrayList<>();
        collectedVariables.addAll(resizingVariables.stream()
                .filter(variable -> Variable.CollectionType.COLLECTED.equals(variable.getCollectionType()))
                .map(CollectedVariable.class::cast)
                .toList());

        collectedVariables.addAll(resizingVariables.stream()
                .filter(variable -> Variable.CollectionType.CALCULATED.equals(variable.getCollectionType()))
                .map(CalculatedVariable.class::cast)
                .map(this::getCollectedVariablesFromCalculatedVariable)
                .flatMap(Collection::stream)
                .toList());
        return collectedVariables;
    }

    /**
     * Check if loop is a main or linked loop
     * @param loop loop to check
     * @return true if linked loop, false otherwise
     */
    private boolean isLinkedLoop(Loop loop) {
        return loop.getLines() == null;
    }
}
