package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.*;

import java.util.List;

public class LunaticSequenceDescription implements ProcessingStep<Questionnaire> {

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        moveSequencesDescription(lunaticQuestionnaire.getComponents());
        //
        lunaticQuestionnaire.getComponents().stream()
                .filter(Loop.class::isInstance).map(Loop.class::cast)
                .map(Loop::getComponents)
                .forEach(this::moveSequencesDescription);
    }

    private void moveSequencesDescription(List<ComponentType> lunaticComponents) {
        lunaticComponents.stream()
                .filter(component -> ComponentTypeEnum.SEQUENCE.equals(component.getComponentType())
                        || ComponentTypeEnum.SUBSEQUENCE.equals(component.getComponentType()))
                .forEach(this::moveSequencesDescription);
    }

    private void moveSequencesDescription(ComponentType sequenceOrSubsequence) {
        if (sequenceOrSubsequence.getDeclarations().isEmpty())
            return;
        sequenceOrSubsequence.setDescription(
                sequenceOrSubsequence.getDeclarations().getFirst().getLabel());
        sequenceOrSubsequence.getDeclarations().clear();
    }

}
