package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.processing.in.steps.ddi.DDIDeserializeSuggesterConfiguration;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.Suggester;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SuggesterQuestionTest {

    @Test
    void suggester_integrationTest() throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(
                this.getClass().getClassLoader().getResourceAsStream(
                        "integration/ddi/ddi-suggester.xml"));
        //
        DDIMapper ddiMapper = new DDIMapper();
        EnoQuestionnaire enoQuestionnaire = new EnoQuestionnaire();
        ddiMapper.mapDDI(ddiInstanceDocument, enoQuestionnaire);
        new DDIDeserializeSuggesterConfiguration().apply(enoQuestionnaire);
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        lunaticMapper.mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        //
        Map<String, Suggester> suggesterComponents = new HashMap<>();
        lunaticQuestionnaire.getComponents().stream()
                .filter(Suggester.class::isInstance)
                .map(Suggester.class::cast)
                .forEach(suggester -> {
                    assertEquals(ComponentTypeEnum.SUGGESTER, suggester.getComponentType());
                    suggesterComponents.put(suggester.getId(), suggester);
                });
        //
        assertEquals(4, suggesterComponents.size());
        //
        Suggester suggester1 = suggesterComponents.get("lrueihko");
        assertEquals("L_ACTIVITES-1-0-0", suggester1.getStoreName());
        assertEquals("ACTIVITE", suggester1.getResponse().getName());
        //
        Suggester suggester2 = suggesterComponents.get("lruent2x");
        assertEquals("L_COMMUNEPASSEE-1-2-0", suggester2.getStoreName());
        assertEquals("COMMUNE", suggester2.getResponse().getName());
        //
        Suggester suggester3 = suggesterComponents.get("lrueotzf");
        assertEquals("L_DEPNAIS-1-1-0", suggester3.getStoreName());
        assertEquals("DPARTEMENT", suggester3.getResponse().getName());
        //
        Suggester suggester4 = suggesterComponents.get("lruexn64");
        assertEquals("L_DIPLOMES-1-0-0", suggester4.getStoreName());
        assertEquals("DIPLOMES", suggester4.getResponse().getName());
    }

}
