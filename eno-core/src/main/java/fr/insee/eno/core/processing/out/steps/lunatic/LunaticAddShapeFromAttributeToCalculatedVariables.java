package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.eno.core.processing.out.steps.lunatic.calculatedvariable.RetrieveShapeFromAttribute;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

/**
 * Add shapeFrom attribute on calculated variables. Exclude shapeFrom for non eno calculated variables
 * (shapeFrom on filter results calculated variables and pairwise calculated variables are generated in their processings)
 */
public class LunaticAddShapeFromAttributeToCalculatedVariables implements ProcessingStep<Questionnaire> {
    private final EnoQuestionnaire enoQuestionnaire;

    public LunaticAddShapeFromAttributeToCalculatedVariables(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        List<VariableType> lunaticCalculatedVariables = lunaticQuestionnaire.getVariables().stream()
                .map(VariableType.class::cast)
                .filter(variableType -> variableType.getVariableType().equals(VariableTypeEnum.CALCULATED))
                .toList();

        for(VariableType lunaticCalculatedVariable : lunaticCalculatedVariables) {
            RetrieveShapeFromAttribute.getShapeFrom(lunaticCalculatedVariable.getName(), enoQuestionnaire)
                    .ifPresent(shapeFromVariable -> lunaticCalculatedVariable.setShapeFrom(shapeFromVariable.getName()));
        }
    }
}
