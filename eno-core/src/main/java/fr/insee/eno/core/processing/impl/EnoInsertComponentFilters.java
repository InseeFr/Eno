package fr.insee.eno.core.processing.impl;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.navigation.Filter;
import fr.insee.eno.core.model.question.Question;
import fr.insee.eno.core.model.sequence.AbstractSequence;
import fr.insee.eno.core.model.sequence.StructureItemReference;
import fr.insee.eno.core.model.sequence.StructureItemReference.StructureItemType;
import fr.insee.eno.core.processing.EnoProcessingInterface;

public class EnoInsertComponentFilters implements EnoProcessingInterface {

    private EnoQuestionnaire enoQuestionnaire;

    public void apply(EnoQuestionnaire enoQuestionnaire) {
        this.enoQuestionnaire = enoQuestionnaire;
        enoQuestionnaire.getFilters().forEach(this::iterateOnFilterScope);
    }

    private void iterateOnFilterScope(Filter filter) {
        filter.getFilterScope().forEach(structureItemReference ->
                insertFilterInComponents(filter, structureItemReference));
    }

    private void insertFilterInComponents(Filter filter, StructureItemReference structureItemReference) {
        if (StructureItemType.QUESTION.equals(structureItemReference.getType())) {
            Question question = (Question) enoQuestionnaire.get(structureItemReference.getId());
            question.getComponentFilter().addFilter(filter);
            return;
        }
        AbstractSequence sequence = (AbstractSequence) enoQuestionnaire.get(structureItemReference.getId());
        sequence.getComponentFilter().addFilter(filter);
        sequence.getSequenceStructure().forEach(structureItemReference1 ->
                insertFilterInComponents(filter, structureItemReference1));
    }

}
