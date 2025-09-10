package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.PoguesToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.articulation.ArticulationItem;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArticulationIntegrationTest {

    private static Stream<Arguments> mapToEno() throws ParsingException {
        // Given
        ClassLoader classLoader = ArticulationIntegrationTest.class.getClassLoader();
        fr.insee.pogues.model.Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/pogues/pogues-articulation.json"));
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/ddi/ddi-articulation.xml"));
        return Stream.of(
                Arguments.of(PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire)),
                Arguments.of(PoguesToEno.fromObject(poguesQuestionnaire))
        );
    }

    @ParameterizedTest
    @MethodSource("mapToEno")
    void integrationTest(InToEno inToEno) {
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC);

        // When
        EnoQuestionnaire enoQuestionnaire = inToEno.transform(
                enoParameters);
        Questionnaire lunaticQuestionnaire = new Questionnaire();
        new LunaticMapper().mapQuestionnaire(enoQuestionnaire, lunaticQuestionnaire);

        // Then
        assertNotNull(lunaticQuestionnaire.getArticulation());
        assertEquals("mfdr4hlz", lunaticQuestionnaire.getArticulation().getSource());
        assertEquals(3, lunaticQuestionnaire.getArticulation().getItems().size());
        ArticulationItem item1 = lunaticQuestionnaire.getArticulation().getItems().get(0);
        ArticulationItem item2 = lunaticQuestionnaire.getArticulation().getItems().get(1);
        ArticulationItem item3 = lunaticQuestionnaire.getArticulation().getItems().get(2);
        assertEquals("Prénom", item1.getLabel());
        assertEquals("FIRST_NAME", item1.getValue());
        assertEquals("Sexe", item2.getLabel());
        assertEquals("GENDER", item2.getValue());
        assertEquals("Age", item3.getLabel());
        assertEquals("cast(AGE_NVL, string)", item3.getValue());
        lunaticQuestionnaire.getArticulation().getItems().forEach(item ->
                assertEquals(LabelTypeEnum.VTL, item.getType()));
    }

}
