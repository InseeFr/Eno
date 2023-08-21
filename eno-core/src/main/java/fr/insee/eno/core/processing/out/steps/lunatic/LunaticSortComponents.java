package fr.insee.eno.core.processing.out.steps.lunatic;

import fr.insee.eno.core.exceptions.technical.LunaticSortingException;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.ProcessingStep;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LunaticSortComponents implements ProcessingStep<Questionnaire> {

    /** The Eno questionnaire contains the information of the components' order. */
    private final EnoQuestionnaire enoQuestionnaire;
    /** Map used during sorting to transfer components. */
    private Map<String, ComponentType> transientMap;

    public LunaticSortComponents(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
    }

    @Override
    public void apply(Questionnaire lunaticQuestionnaire) {
        //
        int componentsCount = lunaticQuestionnaire.getComponents().size();
        List<ComponentType> lunaticComponents = lunaticQuestionnaire.getComponents();
        // Transfer components in a temporary map
        transientMap = new HashMap<>();
        for (Iterator<ComponentType> iterator = lunaticComponents.iterator(); iterator.hasNext();) {
            ComponentType component = iterator.next();
            transientMap.put(component.getId(), component);
            iterator.remove();
        }
        // Insert the components from the temporary map in the right order
        enoQuestionnaire.getSequences().forEach(enoSequence -> {
            ComponentType toBeInserted = transientMap.remove(enoSequence.getId());
            if (toBeInserted == null) {
                throw new LunaticSortingException(String.format(
                        "Sequence '%s' found in the Eno questionnaire is not present in Lunatic components.",
                        enoSequence.getId()));
            }
            lunaticComponents.add(toBeInserted);
            addSequenceComponents(lunaticComponents, enoSequence);
        });
        // Re-insert loops
        lunaticComponents.addAll(transientMap.values());
        // Safety check
        int finalSize = lunaticComponents.size();
        if (finalSize != componentsCount) {
            throw new LunaticSortingException(String.format(
                    "Some components were lost during sorting. Initial count: %s, after sorting: %s.",
                    componentsCount, finalSize));
        }
    }

    /** Add Lunatic components described ine the Eno sequence given in the list given in the right order. */
    private void addSequenceComponents(List<ComponentType> lunaticComponents, Sequence enoSequence) {
        for (String enoComponentReference : enoSequence.getSequenceStructure().stream().map(StructureItemReference::getId).toList()) {
            Optional<EnoComponent> enoComponent = insertLunaticComponent(lunaticComponents, enoComponentReference);
            if (enoComponent.isEmpty())
                continue;
            if (enoComponent.get() instanceof Subsequence enoSubsequence) {
                for (String enoComponentReference2 : enoSubsequence.getSequenceStructure().stream().map(StructureItemReference::getId).toList()) {
                    insertLunaticComponent(lunaticComponents, enoComponentReference2);
                }
            }
        }
    }

    /**  */
    private Optional<EnoComponent> insertLunaticComponent(List<ComponentType> lunaticComponents, String enoComponentReference) {
        EnoComponent enoComponent = (EnoComponent) enoQuestionnaire.get(enoComponentReference);
        ComponentType toBeInserted = transientMap.remove(enoComponent.getId());
        // Here component can be null (if some component exists in Eno-model but not in Lunatic)
        if (toBeInserted == null) {
            log.debug("Eno component "+enoComponent+" has no match in Lunatic questionnaire.");
            return Optional.empty();
        }
        lunaticComponents.add(toBeInserted);
        return Optional.of(enoComponent);
    }

    /** (Recursive equivalent of iterative method above.) */
    private void addSequenceComponentsRec(List<ComponentType> lunaticComponents, AbstractSequence enoSequence) {
        enoSequence.getSequenceStructure().stream().map(StructureItemReference::getId).forEachOrdered(enoComponentReference -> {
            insertLunaticComponent(lunaticComponents, enoComponentReference);
            if (enoQuestionnaire.get(enoComponentReference) instanceof AbstractSequence enoSequence2)
                addSequenceComponentsRec(lunaticComponents, enoSequence2);
        });
    }

}
