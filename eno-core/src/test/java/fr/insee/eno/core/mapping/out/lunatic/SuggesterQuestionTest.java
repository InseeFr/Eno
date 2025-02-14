package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.PoguesDDIToLunatic;
import fr.insee.eno.core.exceptions.business.DDIParsingException;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.DDIMapper;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.processing.in.steps.ddi.DDIDeserializeSuggesterConfiguration;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.lunatic.model.flat.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SuggesterQuestionTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    void suggester_integrationTest() throws DDIParsingException {
        //
        DDIInstanceDocument ddiInstanceDocument = DDIDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/ddi/ddi-suggester.xml"));
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

    @Test
    void suggesterWithArbitrary() throws ParsingException {
        // Given + When
        Questionnaire lunaticQuestionnaire = PoguesDDIToLunatic.fromInputStreams(
                classLoader.getResourceAsStream("integration/pogues/pogues-suggester-arbitrary.json"),
                classLoader.getResourceAsStream("integration/ddi/ddi-suggester-arbitrary.xml"))
                .transform(EnoParameters.of(Context.HOUSEHOLD, ModeParameter.CAWI, Format.LUNATIC));
        // Then
        Question question1 = (Question) lunaticQuestionnaire.getComponents().get(1);
        Question question2 = (Question) lunaticQuestionnaire.getComponents().get(2);
        Suggester suggester1 = assertInstanceOf(Suggester.class, question1.getComponents().getFirst());
        Suggester suggester2 = assertInstanceOf(Suggester.class, question2.getComponents().getFirst());
//      Suggester suggester2 = assertInstanceOf(Suggester.class, lunaticQuestionnaire.getComponents().get(2));
//      Suggester suggester1 = assertInstanceOf(Suggester.class, lunaticQuestionnaire.getComponents().get(1));
//      Suggester suggester2 = assertInstanceOf(Suggester.class, lunaticQuestionnaire.getComponents().get(2));
        //
        assertEquals("COUNTRY", suggester1.getResponse().getName());
        assertNull(suggester1.getArbitrary());
        //
        ArbitraryType arbitrary = suggester2.getArbitrary();
        assertEquals("ACTIVITY", suggester2.getResponse().getName());
        assertEquals("ACTIVITY_ARBITRARY", arbitrary.getResponse().getName());
        assertNull(arbitrary.getLabel());
        assertNull(arbitrary.getInputLabel());
    }

}
