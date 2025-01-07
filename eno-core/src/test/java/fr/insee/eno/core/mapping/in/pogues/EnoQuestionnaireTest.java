package fr.insee.eno.core.mapping.in.pogues;

import fr.insee.eno.core.exceptions.business.PoguesDeserializationException;
import fr.insee.eno.core.mappers.PoguesMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnoQuestionnaireTest {

    @Test
    void mapIdFromPogues() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setId("foo-questionnaire-id");
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        assertEquals("foo-questionnaire-id", enoQuestionnaire.getId());
    }

    @Test
    void mapQuestionnaireLabelFromPogues() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.getLabel().add("Questionnaire label");
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        assertEquals("Questionnaire label", enoQuestionnaire.getLabel().getValue());
    }

    @Test
    void mapAgencyFromPogues() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setAgency("fr.insee");
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        assertEquals("fr.insee", enoQuestionnaire.getAgency());
    }

    @Test
    void mapQuestionnaireModelFromPogues() {
        //
        Questionnaire poguesQuestionnaire = new Questionnaire();
        poguesQuestionnaire.setName("QUESTIONNAIRE_MODEL");
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        //
        PoguesMapper poguesMapper = new PoguesMapper();
        poguesMapper.mapPoguesQuestionnaire(poguesQuestionnaire, enoQuestionnaire);
        //
        assertEquals("QUESTIONNAIRE_MODEL", enoQuestionnaire.getQuestionnaireModel());
    }

    @Test
    void integrationTest() throws PoguesDeserializationException {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        new PoguesMapper().mapPoguesQuestionnaire(
                PoguesDeserializer.deserialize(this.getClass().getClassLoader().getResourceAsStream(
                        "integration/pogues/pogues-simple.json")),
                enoQuestionnaire);
        //
        assertEquals("lmyoceix", enoQuestionnaire.getId());
        assertEquals("fr.insee", enoQuestionnaire.getAgency());
        assertEquals("ENO_SIMPLE", enoQuestionnaire.getQuestionnaireModel());
        assertEquals("Eno - Simple questionnaire", enoQuestionnaire.getLabel().getValue());
    }

}
