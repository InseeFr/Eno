package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.exceptions.technical.MappingException;
import fr.insee.eno.core.model.question.DynamicTableQuestion;
import fr.insee.eno.core.model.question.table.ResponseCell;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.variable.VariableType;

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


    public void processCleaningRosterForLoopQuestion(DynamicTableQuestion dynamicTableQuestion){
        Optional<ComponentType> lunaticComponent = findComponentById(lunaticQuestionnaire, dynamicTableQuestion.getId());
        if(lunaticComponent.isEmpty()){
            throw new MappingException("Cannot find Lunatic component for " + dynamicTableQuestion + ".");
        }
        if(!(lunaticComponent.get() instanceof RosterForLoop rosterForLoop)){
            throw new MappingException("Lunatic component for " + dynamicTableQuestion + " is not a RosterForLoop.");
        }
        rosterForLoop.getComponents().forEach(this::processCleaningForBodyCellComponent);
    }

    private void processCleaningForBodyCellComponent(BodyCell bodyCell){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();
        String variableToClean = bodyCell.getResponse().getName();
        ConditionFilterType conditionFilter = bodyCell.getConditionFilter();
        if(isConditionFilterActive(bodyCell.getConditionFilter())){
            processCleaningForFilterExpression(
                    cleaning, variableIndex, variableShapeFromIndex,
                    conditionFilter.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(conditionFilter, variableIndex),
                    List.of(variableToClean));
        }
    }
}
