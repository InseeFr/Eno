package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.AbstractSequence;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Subsequence;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.eno.core.reference.EnoIndex;
import fr.insee.eno.core.reference.LunaticCatalog;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class LunaticSortComponents implements OutProcessingInterface<Questionnaire> {

    private EnoQuestionnaire enoQuestionnaire;
    private LunaticCatalog lunaticCatalog;

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        // Get the Eno index
        EnoIndex enoIndex = enoQuestionnaire.getIndex();
        assert enoIndex != null;
        // Lunatic questionnaire components
        List<ComponentType> lunaticComponents = lunaticQuestionnaire.getComponents();
        // Empty the component list (to be refilled using the Lunatic catalog)
        lunaticComponents.clear();
        // Iterate on the Eno questionnaire to add components in the right order
        enoQuestionnaire.getSequences().forEach(enoSequence -> {
                    lunaticComponents.add(lunaticCatalog.getComponent(enoSequence.getId()));
                    addSequenceComponentsRec2(lunaticComponents, enoSequence,enoIndex);
                });
    }

    /** Add Lunatic components described ine the Eno sequence given in the list given in the right order. */
    private void addSequenceComponents(List<ComponentType> lunaticComponents, AbstractSequence enoSequence, EnoIndex enoIndex) {
        for (String enoComponentReference : enoSequence.getComponentReferences()) {
            EnoComponent enoComponent = (EnoComponent) enoIndex.get(enoComponentReference);
            lunaticComponents.add(
                    lunaticCatalog.getComponent(enoComponent.getId()));
            if (enoComponent instanceof Subsequence enoSubsequence) {
                for (String enoComponentReference2 : enoSubsequence.getComponentReferences()) {
                    EnoComponent enoComponent2 = (EnoComponent) enoIndex.get(enoComponentReference2);
                    lunaticComponents.add(
                            lunaticCatalog.getComponent(enoComponent2.getId()));
                }
            }
        }
    }

    /** (Unused recursive equivalent of iterative method above.) */
    private void addSequenceComponentsRec(List<ComponentType> lunaticComponents, AbstractSequence enoSequence, EnoIndex enoIndex) {
        enoSequence.getComponentReferences().forEach(enoComponentReference -> {
            lunaticComponents.add(
                    lunaticCatalog.getComponent(enoComponentReference));
            if (enoIndex.get(enoComponentReference) instanceof AbstractSequence enoSequence2)
                addSequenceComponentsRec(lunaticComponents, enoSequence2, enoIndex);
        });
    }

    /** (Slightly optimized version.) */
    private void addSequenceComponentsRec2(List<ComponentType> lunaticComponents, AbstractSequence enoSequence, EnoIndex enoIndex) {
        enoSequence.getComponentReferences().forEach(enoComponentReference -> {
            ComponentType lunaticComponent = lunaticCatalog.getComponent(enoComponentReference);
            lunaticComponents.add(lunaticComponent);
            if (lunaticComponent instanceof fr.insee.lunatic.model.flat.Subsequence) {
                AbstractSequence enoSequence2 = (AbstractSequence) enoIndex.get(enoComponentReference);
                addSequenceComponentsRec2(lunaticComponents, enoSequence2, enoIndex);
            }
        });
    }

}
