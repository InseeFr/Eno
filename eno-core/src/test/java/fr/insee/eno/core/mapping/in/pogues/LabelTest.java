package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.Label;
import fr.insee.pogues.model.GenericNameEnum;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.SequenceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LabelTest {

    @Test
    void sequenceLabel_dollarSigns() {
        // Given
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        String labelValue = "\"Questions for \" || $FIRST_NAME$ || \" \" || $LAST_NAME$";
        poguesSequence.getLabel().add(labelValue);
        poguesQuestionnaire.getChild().add(poguesSequence);

        // When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        Label enoLabel = enoQuestionnaire.getSequences().getFirst().getLabel();
        assertEquals("\"Questions for \" || FIRST_NAME || \" \" || LAST_NAME", enoLabel.getValue());
    }

}
