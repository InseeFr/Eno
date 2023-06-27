package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.exceptions.technical.LunaticSortingException;
import fr.insee.eno.core.model.EnoComponent;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.Sequence;
import fr.insee.eno.core.model.sequence.Subsequence;
import fr.insee.eno.core.processing.OutProcessingInterface;
import fr.insee.lunatic.model.flat.ComponentType;
import fr.insee.lunatic.model.flat.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static fr.insee.eno.core.model.sequence.SequenceItem.SequenceItemType.*;

@Slf4j
public class LunaticSortComponents implements OutProcessingInterface<Questionnaire> {

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
                // FIXME: case where there is a loop over a sequence
                throw new LunaticSortingException(String.format(
                        "Sequence '%s' found in the Eno questionnaire is not present in Lunatic components.",
                        enoSequence.getId()));
            }
            lunaticComponents.add(toBeInserted);
            addSequenceComponents(lunaticComponents, enoSequence);
        });
        // Safety check
        int finalSize = lunaticComponents.size();
        if (finalSize != componentsCount && false) {
            throw new LunaticSortingException(String.format(
                    "Some components were lost during sorting. Initial count: %s, after sorting: %s.",
                    componentsCount, finalSize));
        }
    }

    /** Add Lunatic components described ine the Eno sequence given in the list given in the right order. */
    private void addSequenceComponents(List<ComponentType> lunaticComponents, Sequence enoSequence) {
        for (String enoComponentReference : getSequenceStructure(enoSequence)) {
            Optional<EnoComponent> enoComponent = insertLunaticComponent(lunaticComponents, enoComponentReference);
            if (enoComponent.isEmpty())
                continue;
            if (enoComponent.get() instanceof Subsequence enoSubsequence) {
                for (String enoComponentReference2 : getSequenceStructure(enoSubsequence)) {
                    insertLunaticComponent(lunaticComponents, enoComponentReference2);
                }
            }
        }
    }

    /** Construct the ordered list of subsequences and/or questions identifiers that belong to the given sequence.
     * This is done by using "sequence items" list. When a loop sequence item is encountered, it is the sequence
     * reference defined in the loop that is inserted in the resulting list.
     * TODO: manage filter case..........
     * */
    private List<String> getSequenceStructure(AbstractSequence enoSequence) {
        return enoSequence.getSequenceItems().stream()
                .filter(sequenceItem -> Set.of(SUBSEQUENCE, LOOP, QUESTION).contains(sequenceItem.getType()))
                .map(sequenceItem -> {
                    if (sequenceItem.getType() == LOOP)
                        return ((Loop) enoQuestionnaire.get(sequenceItem.getId())).getSequenceReference();
                    return sequenceItem.getId();
                })
                .toList();
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
        getSequenceStructure(enoSequence).forEach(enoComponentReference -> {
            insertLunaticComponent(lunaticComponents, enoComponentReference);
            if (enoQuestionnaire.get(enoComponentReference) instanceof AbstractSequence enoSequence2)
                addSequenceComponentsRec(lunaticComponents, enoSequence2);
        });
    }

}
