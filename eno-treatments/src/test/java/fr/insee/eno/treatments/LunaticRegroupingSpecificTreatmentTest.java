package fr.insee.eno.treatments;

import fr.insee.eno.treatments.dto.Regroupement;
import fr.insee.lunatic.conversion.JsonDeserializer;
import fr.insee.lunatic.exception.SerializationException;
import fr.insee.lunatic.model.flat.Input;
import fr.insee.lunatic.model.flat.Question;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.ResponseType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LunaticRegroupingSpecificTreatmentTest {

    @Test
    void regroupQuestions() {
        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setPagination("question");
        Input input1 = new Input();
        input1.setPage("1");
        input1.setResponse(new ResponseType());
        input1.getResponse().setName("RESPONSE1");
        Input input2 = new Input();
        input2.setPage("2");
        input2.setResponse(new ResponseType());
        input2.getResponse().setName("RESPONSE2");
        questionnaire.getComponents().add(input1);
        questionnaire.getComponents().add(input2);
        questionnaire.setMaxPage("2");
        //
        Regroupement regroupement = new Regroupement(List.of("RESPONSE1", "RESPONSE2"));
        new LunaticRegroupingSpecificTreatment(List.of(regroupement)).apply(questionnaire);
        //
        assertEquals("1", questionnaire.getComponents().get(0).getPage());
        assertEquals("1", questionnaire.getComponents().get(1).getPage());
        assertEquals("1", questionnaire.getMaxPage());
    }

    @Test
    void regroupQuestions_dsfrMode() {
        //
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setPagination("question");
        //
        Question question1 = new Question();
        question1.setPage("1");
        Input input1 = new Input();
        input1.setPage("1");
        input1.setResponse(new ResponseType());
        input1.getResponse().setName("RESPONSE1");
        question1.getComponents().add(input1);
        //
        Question question2 = new Question();
        question2.setPage("2");
        Input input2 = new Input();
        input2.setPage("2");
        input2.setResponse(new ResponseType());
        input2.getResponse().setName("RESPONSE2");
        question2.getComponents().add(input2);
        //
        questionnaire.getComponents().add(question1);
        questionnaire.getComponents().add(question2);
        questionnaire.setMaxPage("2");

        //
        Regroupement regroupement = new Regroupement(List.of("RESPONSE1", "RESPONSE2"));
        new LunaticRegroupingSpecificTreatment(List.of(regroupement)).apply(questionnaire);

        //
        assertEquals("1", questionnaire.getComponents().get(0).getPage());
        assertEquals("1", ((Question) questionnaire.getComponents().get(0)).getComponents().getFirst().getPage());
        assertEquals("1", questionnaire.getComponents().get(1).getPage());
        assertEquals("1", ((Question) questionnaire.getComponents().get(1)).getComponents().getFirst().getPage());
        assertEquals("1", questionnaire.getMaxPage());
    }

    @Test
    void integrationTest_dsfrMode() throws SerializationException {
        //
        Questionnaire lunaticQuestionnaire = new JsonDeserializer().deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "regrouping/questionnaire-dsfr-before.json"));
        List<Regroupement> regroupementList = new SpecificTreatmentsDeserializer().deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "regrouping/regrouping-treatment.json")).regroupements();
        //
        new LunaticRegroupingSpecificTreatment(regroupementList).apply(lunaticQuestionnaire);
        //
        assertEquals("2", lunaticQuestionnaire.getComponents().get(1).getPage());
        assertEquals("2", ((Question) lunaticQuestionnaire.getComponents().get(1)).getComponents().getFirst().getPage());
        assertEquals("2", lunaticQuestionnaire.getComponents().get(2).getPage());
        assertEquals("2", ((Question) lunaticQuestionnaire.getComponents().get(2)).getComponents().getFirst().getPage());
        assertEquals("2", lunaticQuestionnaire.getMaxPage());
    }

}
