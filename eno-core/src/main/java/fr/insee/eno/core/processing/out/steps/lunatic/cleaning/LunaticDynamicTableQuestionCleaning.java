package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.navigation.ComponentFilter;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.RosterForLoop;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.variable.VariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.getFinalBindingReferencesWithCalculatedVariables;
import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.processCleaningForFilterExpression;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;
import static fr.insee.eno.core.utils.LunaticUtils.isConditionFilterActive;

public class LunaticDynamicTableQuestionCleaning {

    private final Questionnaire lunaticQuestionnaire;
    private final Map<String, VariableType> variableIndex;
    private final Map<String, String> variableShapeFromIndex;

    public LunaticDynamicTableQuestionCleaning(Questionnaire lunaticQuestionnaire,
                                               Map<String, VariableType> variableIndex,
                                               Map<String, String> variableShapeFromIndex){
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.variableIndex = variableIndex;
        this.variableShapeFromIndex = variableShapeFromIndex;
    }


    public void processCleaningDynamicTableQuestion(DynamicTableQuestion dynamicTableQuestion){
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, dynamicTableQuestion.getId());
        if(lunaticComponent.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + dynamicTableQuestion + ".");
        }
        if(!(lunaticComponent.get() instanceof RosterForLoop rosterForLoop)){
            throw new MappingException("Lunatic component for " + dynamicTableQuestion + " is not a RosterForLoop.");
        }
        dynamicTableQuestion.getResponseCells()
                .forEach(this::processCleaningResponseCell);
    }

    private List<String> getCollectedVariablesName(ResponseCell responseCell){
        List<String> collectedVariables = new ArrayList<>();
        collectedVariables.add(responseCell.getResponse().getVariableName());
        // should add ARBITRARY response of suggester but is not implemented yet
        return collectedVariables;
    }

    private void processCleaningResponseCell(ResponseCell responseCell){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        List<String> variablesToClean = getCollectedVariablesName(responseCell);
        ComponentFilter componentFilter = responseCell.getComponentFilter();
        if(isConditionFilterActive(componentFilter)){
            processCleaningForFilterExpression(
                    cleaning, variableIndex, variableShapeFromIndex,
                    componentFilter.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(componentFilter, variableIndex),
                    variablesToClean);
        }
    }
}
