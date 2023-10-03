package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.ShapefromAttributeRetrieval;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.VariableType;
import fr.insee.lunatic.model.flat.VariableTypeEnum;

import java.util.List;

/**
 * Add shapeFrom attribute on calculated variables. Exclude shapeFrom for non eno calculated variables
 * (shapeFrom on filter results calculated variables and pairwise calculated variables are generated in their processings)
 */
public class LunaticAddShapeToCalculatedVariables implements ProcessingStep<Questionnaire> {
    private final EnoQuestionnaire enoQuestionnaire;
    private final ShapefromAttributeRetrieval shapeFromAttributeRetrieval;

    public LunaticAddShapeToCalculatedVariables(EnoQuestionnaire enoQuestionnaire, ShapefromAttributeRetrieval shapeFromAttributeRetrieval) {
        this.enoQuestionnaire = enoQuestionnaire;
        this.shapeFromAttributeRetrieval = shapeFromAttributeRetrieval;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<VariableType> lunaticCalculatedVariables = lunaticQuestionnaire.getVariables().stream()
                .filter(VariableType.class::isInstance)
                .map(VariableType.class::cast)
                .filter(variableType -> variableType.getVariableType().equals(VariableTypeEnum.CALCULATED))
                .toList();

        for(VariableType lunaticCalculatedVariable : lunaticCalculatedVariables) {
            shapeFromAttributeRetrieval.getShapeFrom(lunaticCalculatedVariable.getName(), enoQuestionnaire)
                    .ifPresent(shapeFromVariable -> lunaticCalculatedVariable.setShapeFrom(shapeFromVariable.getName()));
        }
    }
}
