package fr.insee.eno.core.processing.out.steps.lunatic.cleaning;

import fr.insee.eno.core.model.question.PairwiseQuestion;
import fr.insee.eno.core.utils.vtl.VtlSyntaxUtils;
import fr.insee.lunatic.model.flat.*;
import fr.insee.lunatic.model.flat.cleaning.CleaningType;
import fr.insee.lunatic.model.flat.variable.VariableType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.getFinalBindingReferencesWithCalculatedVariables;
import static fr.insee.eno.core.processing.out.steps.lunatic.cleaning.CleaningUtils.processCleaningForFilterExpression;
import static fr.insee.eno.core.utils.LunaticUtils.findComponentById;

public class LunaticPairwiseQuestionCleaning {

    private final Questionnaire lunaticQuestionnaire;
    private final Map<String, VariableType> variableIndex;
    private final Map<String, String> variableShapeFromIndex;

    public LunaticPairwiseQuestionCleaning(Questionnaire lunaticQuestionnaire,
                                           Map<String, VariableType> variableIndex,
                                           Map<String, String> variableShapeFromIndex){
        this.lunaticQuestionnaire = lunaticQuestionnaire;
        this.variableIndex = variableIndex;
        this.variableShapeFromIndex = variableShapeFromIndex;
    }

    public void processCleaningPairwiseQuestion(PairwiseQuestion pairwiseQuestion){
        CleaningType cleaning = lunaticQuestionnaire.getCleaning();

        Optional<ComponentType> foundComponent = findComponentById(lunaticQuestionnaire, pairwiseQuestion.getId());
        if(foundComponent.isPresent()){
            String loopVariableNameBasedOn = pairwiseQuestion.getLoopVariableName();
            ConditionFilterType specialConditionFilter = new ConditionFilterType();
            specialConditionFilter.setType(LabelTypeEnum.VTL);
            String loopVariableNameNotEmpty = VtlSyntaxUtils.expressionNotEqualToOther(loopVariableNameBasedOn, "\"\"");
            specialConditionFilter.setValue(loopVariableNameNotEmpty);
            specialConditionFilter.setBindingDependencies(List.of(loopVariableNameBasedOn));

            PairwiseLinks pairwiseLinks = (PairwiseLinks) foundComponent.get();
            ComponentSimpleResponseType simpleResponseComponent = (ComponentSimpleResponseType) pairwiseLinks.getComponents().getFirst();
            String responseNameToClean = simpleResponseComponent.getResponse().getName();

            processCleaningForFilterExpression(cleaning, variableIndex, variableShapeFromIndex,
                    specialConditionFilter.getValue(),
                    getFinalBindingReferencesWithCalculatedVariables(specialConditionFilter, variableIndex),
                    List.of(responseNameToClean)
            );
        }
    }
}
