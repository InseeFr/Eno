package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.DeclarationInterface;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.reference.LunaticCatalog;
import fr.insee.lunatic.model.flat.*;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LunaticAddBindingDependencies implements OutProcessingInterface<Questionnaire> {

    private LunaticCatalog lunaticCatalog;
    private EnoIndex enoIndex;

    public void apply(Questionnaire lunaticQuestionnaire) {
        // First step: keep only 'known' components to avoid surprises
        List<ComponentType> lunaticSequences = filterComponentsByType(lunaticQuestionnaire.getComponents(),
                LunaticCatalog.sequenceClassTypes);
        List<ComponentType> lunaticQuestions = filterComponentsByType(lunaticQuestionnaire.getComponents(),
                LunaticCatalog.questionClassTypes);
        List<ComponentType> knownLunaticComponents = new ArrayList<>(lunaticSequences);
        knownLunaticComponents.addAll(lunaticQuestions);
        //
        for (ComponentType lunaticComponent : knownLunaticComponents) {
            //
            lunaticComponent.getDeclarations().forEach(declarationType -> {
                DeclarationInterface enoDeclaration = (DeclarationInterface) enoIndex.get(declarationType.getId());
                lunaticComponent.getBindingDependencies().addAll(enoDeclaration.getVariableNames());
            });
            //
            // TODO: not finished
        }
    }

    private static List<ComponentType> filterComponentsByType(List<ComponentType> components, List<Class<?>> types) {
        return components.stream()
                .filter(component -> types.contains(component.getClass()))
                .toList();
    }

}
