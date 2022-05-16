package fr.insee.eno.core.mappers;

import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.model.Variable;
import fr.insee.lunatic.model.flat.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunaticMapperTest {

    @Test
    public void modelToLunaticTest() {
        //
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        enoQuestionnaire.setId("TEST-ID");
        Variable v1 = new Variable();
        v1.setName("foo1");
        enoQuestionnaire.getVariables().add(v1);
        Variable v2 = new Variable();
        v2.setName("foo2");
        enoQuestionnaire.getVariables().add(v2);
        //
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        LunaticMapper.map(enoQuestionnaire, lunaticQuestionnaire);
        //
        assertEquals("TEST-ID", lunaticQuestionnaire.getId());
        assertEquals("foo1", lunaticQuestionnaire.getVariables().get(0).getName());
        assertEquals("foo2", lunaticQuestionnaire.getVariables().get(1).getName());
    }
}
