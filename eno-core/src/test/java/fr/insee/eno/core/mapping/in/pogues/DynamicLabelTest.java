package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicLabelTest {

    @Test
    void labelInDeclaration_withDollarSign() {
        // Given
        Questionnaire poguesQuestionnaire = new Questionnaire();
        SequenceType poguesSequence = new SequenceType();
        poguesSequence.setGenericName(GenericNameEnum.MODULE);
        DeclarationType poguesDeclaration = new DeclarationType();
        poguesDeclaration.setPosition(DeclarationPositionEnum.BEFORE_QUESTION_TEXT);
        String labelValue = "\"Your revenue last year was \" || $EXT_REVENUE$";
        poguesDeclaration.setText(labelValue);
        poguesSequence.getDeclaration().add(poguesDeclaration);
        poguesQuestionnaire.getChild().add(poguesSequence);

        // When
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);

        // Then
        DynamicLabel enoLabel = enoQuestionnaire.getSequences().getFirst().getDeclarations().getFirst().getLabel();
        assertEquals("\"Your revenue last year was \" || EXT_REVENUE", enoLabel.getValue());
    }

}
