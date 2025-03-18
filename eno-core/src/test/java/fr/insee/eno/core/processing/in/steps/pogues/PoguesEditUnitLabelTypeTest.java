package fr.insee.eno.core.processing.in.steps.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.label.DynamicLabel;
import fr.insee.eno.core.model.question.NumericQuestion;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PoguesEditUnitLabelTypeTest {

    @Test
    void unitTest() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        NumericQuestion numericQuestion = new NumericQuestion();
        DynamicLabel unit = new DynamicLabel();
        unit.setValue("mois");
        numericQuestion.setUnit(unit);
        enoQuestionnaire.getSingleResponseQuestions().add(numericQuestion);
        //
        assertEquals("mois", numericQuestion.getUnit().getValue());
        assertEquals("VTL|MD", numericQuestion.getUnit().getType());
        //
        new PoguesEditUnitLabelType().apply(enoQuestionnaire);
        //
        assertEquals("mois", unit.getValue());
        assertEquals("VTL", unit.getType());
    }

    @Test
    void integrationTest() throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-dynamic-unit.json")),
                enoQuestionnaire);
        //
        new PoguesEditUnitLabelType().apply(enoQuestionnaire);
        //
        assertEquals(5, enoQuestionnaire.getSingleResponseQuestions().size());
        //
        NumericQuestion question = (NumericQuestion) enoQuestionnaire.getSingleResponseQuestions().get(1);
        assertEquals("€", question.getUnit().getValue());
        assertEquals("VTL", question.getUnit().getType());
    }
}

