package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.ddi.lifecycle33.instance.DDIInstanceDocument;
import fr.insee.eno.core.EnoToLunatic;
import fr.insee.eno.core.InToEno;
import fr.insee.eno.core.PoguesDDIToEno;
import fr.insee.eno.core.exceptions.business.ParsingException;
import fr.insee.eno.core.model.EnoQuestionnaire;
import fr.insee.eno.core.parameter.EnoParameters;
import fr.insee.eno.core.parameter.EnoParameters.Context;
import fr.insee.eno.core.parameter.EnoParameters.ModeParameter;
import fr.insee.eno.core.parameter.Format;
import fr.insee.eno.core.serialize.DDIDeserializer;
import fr.insee.eno.core.serialize.PoguesDeserializer;
import fr.insee.lunatic.model.flat.LabelTypeEnum;
import fr.insee.lunatic.model.flat.Questionnaire;
import fr.insee.lunatic.model.flat.multimode.Multimode;
import fr.insee.lunatic.model.flat.multimode.MultimodeRule;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MultimodeIntegrationTest {

    private static Stream<Arguments> mapToEno() throws ParsingException {
        // Given
        ClassLoader classLoader = MultimodeIntegrationTest.class.getClassLoader();
        fr.insee.pogues.model.Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/pogues/pogues-multimode.json"));
        DDIInstanceDocument ddiQuestionnaire = DDIDeserializer.deserialize(
                classLoader.getResourceAsStream("integration/ddi/ddi-multimode.xml"));
        return Stream.of(
                Arguments.of(PoguesDDIToEno.fromObjects(poguesQuestionnaire, ddiQuestionnaire))
                //, Arguments.of(PoguesToEno.fromObject(poguesQuestionnaire)) // (roundabout is not mapped in Pogues yet)
        );
    }

    @ParameterizedTest
    @MethodSource("mapToEno")
    void integrationTest(InToEno inToEno) {
        EnoParameters enoParameters = EnoParameters.of(Context.DEFAULT, ModeParameter.CAWI, Format.LUNATIC);

        // When
        EnoQuestionnaire enoQuestionnaire = inToEno.transform(enoParameters);
        Questionnaire lunaticQuestionnaire = new EnoToLunatic().transform(enoQuestionnaire, enoParameters);

        // Then
        assertNotNull(lunaticQuestionnaire.getMultimode());
        Multimode lunaticMultimode = lunaticQuestionnaire.getMultimode();

        assertNotNull(lunaticMultimode.getQuestionnaire());
        MultimodeRule questionnaireIsMovedRule = lunaticMultimode.getQuestionnaire().getRules().get("IS_MOVED");
        assertNotNull(questionnaireIsMovedRule);
        assertEquals(LabelTypeEnum.VTL, questionnaireIsMovedRule.getType());
        assertEquals("HAS_ADDRESS_CHANGED_NVL", questionnaireIsMovedRule.getValue());

        assertNotNull(lunaticMultimode.getLeaf());
        assertEquals("mff5q24e", lunaticMultimode.getLeaf().getSource());
        MultimodeRule leafIsMovedRule = lunaticMultimode.getLeaf().getRules().get("IS_MOVED");
        assertNotNull(leafIsMovedRule);
        assertEquals(LabelTypeEnum.VTL, leafIsMovedRule.getType());
        assertEquals("HAS_MOVED", leafIsMovedRule.getValue());
    }

}
